package id.ac.ui.cs.advprog.bidmartcatalog.service;

import id.ac.ui.cs.advprog.bidmartcatalog.dto.ListingResponseDTO;
import id.ac.ui.cs.advprog.bidmartcatalog.exception.FileStorageException;
import id.ac.ui.cs.advprog.bidmartcatalog.exception.ListingNotFoundException;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingImage;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ListingRepository;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ListingSpecification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final StorageService storageService;

    @Value("${feature.enable-bid-restriction:true}")
    private boolean enableBidRestriction;

    public ListingServiceImpl(ListingRepository listingRepository, StorageService storageService) {
        this.listingRepository = listingRepository;
        this.storageService = storageService;
    }

    @Override
    public Map<String, Object> getSellerStatistics(String sellerId) {
        List<Object[]> results = listingRepository.countByStatusForSeller(sellerId);
        Map<String, Object> stats = new HashMap<>();
        long total = 0;
        stats.put("activeListings", 0L);
        stats.put("closedListings", 0L);
        stats.put("draftListings", 0L);

        for (Object[] result : results) {
            ListingStatus status = (ListingStatus) result[0];
            long count = (long) result[1];
            total += count;
            if (status == ListingStatus.ACTIVE) stats.put("activeListings", count);
            else if (status == ListingStatus.CLOSED) stats.put("closedListings", count);
            else if (status == ListingStatus.DRAFT) stats.put("draftListings", count);
        }
        stats.put("totalListings", total);
        return stats;
    }

    @Override
    @Transactional
    public Listing createListing(Listing listing, MultipartFile... files) {
        listing.setStatus(ListingStatus.DRAFT);
        listing.setCurrentPrice(listing.getStartingPrice());
        if (files != null && files.length > 0) {
            boolean isPrimary = true;
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                try {
                    String imageUrl = storageService.store(file);
                    ListingImage image = new ListingImage();
                    image.setImageUrl(imageUrl);
                    image.setPrimary(isPrimary);
                    image.setListing(listing);
                    listing.getImages().add(image);
                    isPrimary = false;
                } catch (IOException e) {
                    throw new FileStorageException("Gagal menyimpan file gambar", e);
                }
            }
        }
        return listingRepository.save(listing);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "listings", key = "#id")
    public Listing getListingById(UUID id) {
        return listingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ListingNotFoundException(id));
    }

    @Override
    public Page<Listing> getAllListings(Pageable pageable) {
        return listingRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "activeListings", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Listing> getActiveListings(Pageable pageable) {
        return listingRepository.findByStatus(ListingStatus.ACTIVE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ListingResponseDTO> searchAndFilterListings(
            String title,
            List<UUID> categoryIds,
            Double minPrice,
            Double maxPrice,
            ListingStatus status,
            String sellerId,
            Pageable pageable) {

        Specification<Listing> spec =
                ListingSpecification.filterListings(title, categoryIds, minPrice, maxPrice, status, sellerId);

        Page<Listing> results = listingRepository.findAll(spec, pageable);
        
        // Force mapping di sini agar terjadi di dalam transaksi
        List<ListingResponseDTO> dtoList = results.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
        return new org.springframework.data.domain.PageImpl<>(dtoList, pageable, results.getTotalElements());
    }

    private ListingResponseDTO convertToDTO(Listing listing) {
        ListingResponseDTO.CategoryResponseDTO categoryDTO = null;
        if (listing.getCategory() != null) {
            // Mengakses getName() akan mentrigger loading jika belum terload
            String categoryName = listing.getCategory().getName();
            categoryDTO = ListingResponseDTO.CategoryResponseDTO.builder()
                    .id(listing.getCategory().getId())
                    .name(categoryName)
                    .build();
        }

        List<ListingResponseDTO.ImageResponseDTO> imageDTOs = new ArrayList<>();
        if (listing.getImages() != null) {
            // Mentrigger loading koleksi gambar di dalam transaksi
            imageDTOs = listing.getImages().stream()
                    .filter(img -> img != null && img.getImageUrl() != null)
                    .map(img -> ListingResponseDTO.ImageResponseDTO.builder()
                            .imageUrl(img.getImageUrl())
                            .build())
                    .collect(Collectors.toList());
        }

        return ListingResponseDTO.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .categoryId(listing.getCategory() != null ? listing.getCategory().getId() : null)
                .categoryName(listing.getCategory() != null ? listing.getCategory().getName() : null)
                .category(categoryDTO)
                .images(imageDTOs)
                .imageUrls(imageDTOs.stream().map(ListingResponseDTO.ImageResponseDTO::getImageUrl).collect(Collectors.toList()))
                .sellerId(listing.getSellerId())
                .startingPrice(listing.getStartingPrice())
                .currentPrice(listing.getCurrentPrice())
                .reservePrice(listing.getReservePrice())
                .endTime(listing.getEndTime())
                .status(listing.getStatus())
                .bidCount(listing.getBidCount())
                .createdAt(listing.getCreatedAt())
                .updatedAt(listing.getUpdatedAt())
                .build();
    }

    @Override
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

    @Override
    @Transactional
    @CacheEvict(value = {"listings", "activeListings"}, allEntries = true)
    public Listing updateCurrentPrice(UUID id, Double newPrice) {
        Listing listing = getListingById(id);
        if (newPrice <= listing.getCurrentPrice()) {
            throw new IllegalArgumentException("Penawaran baru harus lebih tinggi");
        }
        listing.setCurrentPrice(newPrice);
        int currentBidCount = listing.getBidCount() != null ? listing.getBidCount() : 0;
        listing.setBidCount(currentBidCount + 1);
        return listingRepository.save(listing);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"listings", "activeListings"}, allEntries = true)
    public Listing closeListing(UUID id) {
        Listing listing = listingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ListingNotFoundException(id));
        if (listing.getStatus() != ListingStatus.CLOSED) {
            listing.setStatus(ListingStatus.CLOSED);
            return listingRepository.save(listing);
        }
        return listing;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"listings", "activeListings"}, allEntries = true)
    public Listing updateListing(UUID id, Listing updateData) {
        Listing existingListing = getListingById(id);
        
        // Cek restriksi: sudah ada penawaran atau status CLOSED
        if (enableBidRestriction && existingListing.getBidCount() != null && existingListing.getBidCount() > 0) {
            throw new IllegalStateException("Sudah ada penawaran.");
        }
        if (existingListing.getStatus() == ListingStatus.CLOSED) {
            throw new IllegalStateException("Listing sudah ditutup.");
        }

        existingListing.setTitle(updateData.getTitle());
        existingListing.setDescription(updateData.getDescription());
        if (updateData.getCategory() != null) existingListing.setCategory(updateData.getCategory());
        if (updateData.getReservePrice() != null) existingListing.setReservePrice(updateData.getReservePrice());
        
        return listingRepository.save(existingListing);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"listings", "activeListings"}, allEntries = true)
    public void deleteListing(UUID id) {
        Listing existingListing = getListingById(id);
        
        // Cek restriksi: tidak boleh hapus jika sudah ada bid
        if (existingListing.getBidCount() != null && existingListing.getBidCount() > 0) {
            throw new IllegalStateException("Sudah ada penawaran, tidak bisa dihapus.");
        }
        
        listingRepository.delete(existingListing);
    }
}
