package id.ac.ui.cs.advprog.bidmartcatalog.service;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingImage;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ListingRepository;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ListingSpecification;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public Listing createListing(Listing listing, MultipartFile[] files) {
        listing.setStatus(ListingStatus.DRAFT);
        listing.setCurrentPrice(listing.getStartingPrice());

        if (files != null && files.length > 0) {
            File uploadDir = new File("uploads/");
            if (!uploadDir.exists()) uploadDir.mkdirs();

            boolean isPrimary = true;
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                try {
                    Path path = Paths.get(uploadDir.getAbsolutePath(), fileName);
                    Files.write(path, file.getBytes());

                    ListingImage image = new ListingImage();
                    image.setImageUrl("/uploads/" + fileName);
                    image.setPrimary(isPrimary);
                    image.setListing(listing);
                    listing.getImages().add(image);
                    isPrimary = false;
                } catch (IOException e) {
                    throw new RuntimeException("Gagal menyimpan file gambar", e);
                }
            }
        }

        return listingRepository.save(listing);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /**
     * Membaca detail listing by ID. Hasil di-cache per ID.
     * Cache di-evict otomatis setiap kali listing diubah.
     */
    @Cacheable(value = "listings", key = "#id")
    public Listing getListingById(UUID id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
    }

    public Page<Listing> getAllListings(Pageable pageable) {
        return listingRepository.findAll(pageable);
    }

    @Cacheable(value = "activeListings", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Listing> getActiveListings(Pageable pageable) {
        return listingRepository.findByStatus(ListingStatus.ACTIVE, pageable);
    }

    public Page<Listing> searchAndFilterListings(
            String title,
            List<UUID> categoryIds,
            Double minPrice,
            Double maxPrice,
            ListingStatus status,
            Pageable pageable) {

        Specification<Listing> spec =
                ListingSpecification.filterListings(title, categoryIds, minPrice, maxPrice, status);
        return listingRepository.findAll(spec, pageable);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = {"listings", "activeListings"}, allEntries = true)
    public Listing publishListing(UUID id) {
        Listing listing = getListingById(id);
        if (listing.getStatus() == ListingStatus.DRAFT) {
            listing.setStatus(ListingStatus.ACTIVE);
            return listingRepository.save(listing);
        }
        return listing;
    }

    /**
     * Memperbarui currentPrice saat event BidPlaced diterima.
     * Validasi out-of-order: harga baru harus > currentPrice.
     * Cache listing-detail di-evict agar pembaca berikutnya mendapat data segar.
     */
    @Transactional
    @CacheEvict(value = {"listings", "activeListings"}, allEntries = true)
    public Listing updateCurrentPrice(UUID id, Double newPrice) {
        Listing listing = getListingById(id);

        if (newPrice <= listing.getCurrentPrice()) {
            throw new IllegalArgumentException(
                "Penawaran baru harus lebih tinggi dari harga saat ini");
        }

        listing.setCurrentPrice(newPrice);
        int currentBidCount = listing.getBidCount() != null ? listing.getBidCount() : 0;
        listing.setBidCount(currentBidCount + 1);

        return listingRepository.save(listing);
    }

    /**
     * Menutup listing ketika event AuctionClosed diterima (lelang berakhir UNSOLD).
     * Status diubah menjadi CLOSED agar tidak bisa di-bid lagi.
     */
    @Transactional
    @CacheEvict(value = {"listings", "activeListings"}, allEntries = true)
    public Listing closeListing(UUID id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found: " + id));

        if (listing.getStatus() != ListingStatus.CLOSED) {
            listing.setStatus(ListingStatus.CLOSED);
            return listingRepository.save(listing);
        }

        return listing; // sudah CLOSED → tidak perlu update
    }

    @Transactional
    @CacheEvict(value = {"listings", "activeListings"}, allEntries = true)
    public Listing updateListing(UUID id, Listing updateData) {
        Listing existingListing = getListingById(id);

        if (existingListing.getBidCount() != null && existingListing.getBidCount() > 0) {
            throw new IllegalStateException(
                "Tidak dapat mengubah listing. Penawaran (bid) sudah dilakukan.");
        }
        if (existingListing.getStatus() == ListingStatus.CLOSED) {
            throw new IllegalStateException("Tidak dapat mengubah listing yang sudah ditutup.");
        }

        existingListing.setTitle(updateData.getTitle());
        existingListing.setDescription(updateData.getDescription());
        if (updateData.getCategory() != null) existingListing.setCategory(updateData.getCategory());
        if (updateData.getReservePrice() != null) existingListing.setReservePrice(updateData.getReservePrice());

        return listingRepository.save(existingListing);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = {"listings", "activeListings"}, allEntries = true)
    public void deleteListing(UUID id) {
        Listing existingListing = getListingById(id);
        if (existingListing.getBidCount() != null && existingListing.getBidCount() > 0) {
            throw new IllegalStateException(
                "Tidak dapat menghapus listing karena sudah ada penawaran (bid).");
        }
        listingRepository.delete(existingListing);
    }
}
