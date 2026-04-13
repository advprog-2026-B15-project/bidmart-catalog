package id.ac.ui.cs.advprog.bidmartcatalog.service;

import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ListingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    /**
     * Create a new listing dengan dukungan multi-gambar
     */
    public Listing createListing(Listing listing, org.springframework.web.multipart.MultipartFile[] files) {
        listing.setStatus(ListingStatus.DRAFT);
        listing.setCurrentPrice(listing.getStartingPrice());
        listing.setCreatedAt(java.time.LocalDateTime.now());

        // Logika untuk menyimpan file gambar
        if (files != null && files.length > 0) {
            java.io.File uploadDir = new java.io.File("uploads/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Buat folder jika belum ada
            }

            boolean isPrimary = true;
            for (org.springframework.web.multipart.MultipartFile file : files) {
                if (file.isEmpty()) continue;

                // Buat nama file unik agar tidak bentrok
                String fileName = java.util.UUID.randomUUID() + "_" + file.getOriginalFilename();
                try {
                    java.nio.file.Path path = java.nio.file.Paths.get(uploadDir.getAbsolutePath(), fileName);
                    java.nio.file.Files.write(path, file.getBytes());

                    // Hubungkan gambar dengan listing
                    id.ac.ui.cs.advprog.bidmartcatalog.model.ListingImage image = new id.ac.ui.cs.advprog.bidmartcatalog.model.ListingImage();
                    image.setImageUrl("/uploads/" + fileName);
                    image.setPrimary(isPrimary);
                    image.setListing(listing); // Relasi ke parent

                    listing.getImages().add(image);
                    isPrimary = false; // Gambar pertama otomatis jadi primary
                } catch (java.io.IOException e) {
                    throw new RuntimeException("Gagal menyimpan file gambar", e);
                }
            }
        }

        return listingRepository.save(listing);
    }

    /**
     * Get listing by ID
     */
    public Listing getListingById(UUID id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
    }

    /**
     * Get all listings with pagination
     */
    public Page<Listing> getAllListings(Pageable pageable) {
        return listingRepository.findAll(pageable);
    }

    /**
     * Publish listing (DRAFT → ACTIVE)
     */
    public Listing publishListing(UUID id) {

        Listing listing = getListingById(id);

        if (listing.getStatus() == ListingStatus.DRAFT) {
            listing.setStatus(ListingStatus.ACTIVE);
            return listingRepository.save(listing);
        }

        return listing; // tidak melakukan apa-apa jika sudah ACTIVE
    }

    /**
     * Memperbarui harga terkini (currentPrice) saat ada penawaran baru dari Modul Lelang
     */
    public Listing updateCurrentPrice(UUID id, Double newPrice) {
        Listing listing = getListingById(id);

        // Validasi sederhana: Harga baru harus lebih besar dari harga saat ini
        if (newPrice > listing.getCurrentPrice()) {
            listing.setCurrentPrice(newPrice);
            return listingRepository.save(listing);
        }

        throw new IllegalArgumentException("Penawaran baru harus lebih tinggi dari harga saat ini");
    }

    /**
     * Mengambil daftar listing yang HANYA berstatus ACTIVE (untuk API Publik)
     */
    public org.springframework.data.domain.Page<Listing> getActiveListings(org.springframework.data.domain.Pageable pageable) {
        return listingRepository.findByStatus(id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus.ACTIVE, pageable);
    }

    public Page<Listing> searchAndFilterListings(
            String title,
            List<UUID> categoryIds,
            Double minPrice,
            Double maxPrice,
            Pageable pageable) {

        // Memanggil Specification yang baru kita buat
        org.springframework.data.jpa.domain.Specification<Listing> spec =
                id.ac.ui.cs.advprog.bidmartcatalog.repository.ListingSpecification
                        .filterListings(title, categoryIds, minPrice, maxPrice);

        return listingRepository.findAll(spec, pageable);
    }

    /**
     * FITUR RESTRIKSI EDIT
     */
    public Listing updateListing(UUID id, Listing updateData) {
        Listing existingListing = getListingById(id);

        // Validasi: Tolak jika sudah ada bid
        if (existingListing.getBidCount() != null && existingListing.getBidCount() > 0) {
            throw new IllegalStateException("Tidak dapat mengubah listing. Penawaran (bid) sudah dilakukan.");
        }

        // Validasi: Tolak jika lelang sudah ditutup
        if (existingListing.getStatus() == ListingStatus.CLOSED) {
            throw new IllegalStateException("Tidak dapat mengubah listing yang sudah ditutup.");
        }

        // Lakukan update field (contoh)
        existingListing.setTitle(updateData.getTitle());
        existingListing.setDescription(updateData.getDescription());
        // Jangan lupa update field lainnya sesuai kebutuhan, tapi JANGAN ubah startingPrice

        return listingRepository.save(existingListing);
    }

    /**
     * FITUR RESTRIKSI DELETE
     */
    public void deleteListing(UUID id) {
        Listing existingListing = getListingById(id);

        // Validasi yang sama dengan Edit
        if (existingListing.getBidCount() != null && existingListing.getBidCount() > 0) {
            throw new IllegalStateException("Tidak dapat menghapus listing karena sudah ada penawaran (bid).");
        }

        listingRepository.delete(existingListing);
    }
}