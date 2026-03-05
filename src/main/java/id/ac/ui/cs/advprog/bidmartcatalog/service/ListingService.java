package id.ac.ui.cs.advprog.bidmartcatalog.service;

import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ListingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    /**
     * Create a new listing
     */
    public Listing createListing(Listing listing) {

        listing.setStatus(ListingStatus.DRAFT);
        listing.setCurrentPrice(listing.getStartingPrice());
        listing.setCreatedAt(LocalDateTime.now());

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
}