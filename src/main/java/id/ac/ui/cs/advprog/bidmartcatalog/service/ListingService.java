package id.ac.ui.cs.advprog.bidmartcatalog.service;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ListingService {
    Map<String, Object> getSellerStatistics(String sellerId);
    Listing createListing(Listing listing, MultipartFile... files);
    Listing getListingById(UUID id);
    Page<Listing> getAllListings(Pageable pageable);
    Page<Listing> getActiveListings(Pageable pageable);
    Page<id.ac.ui.cs.advprog.bidmartcatalog.dto.ListingResponseDTO> searchAndFilterListings(String title, List<UUID> categoryIds, Double minPrice, Double maxPrice, ListingStatus status, String sellerId, Pageable pageable);
    Listing publishListing(UUID id);
    Listing updateCurrentPrice(UUID id, Double newPrice);
    Listing closeListing(UUID id);
    Listing updateListing(UUID id, Listing updateData);
    void deleteListing(UUID id);
}
