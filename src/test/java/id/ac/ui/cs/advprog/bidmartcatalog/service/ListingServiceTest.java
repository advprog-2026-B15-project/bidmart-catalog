package id.ac.ui.cs.advprog.bidmartcatalog.service;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @InjectMocks
    private ListingService listingService;

    private Listing listing;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        listing = new Listing();
        listing.setId(id);
        listing.setStartingPrice(100000.0);
        listing.setCurrentPrice(100000.0);
        listing.setStatus(ListingStatus.DRAFT);
    }

    @Test
    @DisplayName("Test Publish Listing - Success from DRAFT to ACTIVE")
    void testPublishListingSuccess() {
        when(listingRepository.findById(id)).thenReturn(Optional.of(listing));
        when(listingRepository.save(any(Listing.class))).thenReturn(listing);

        Listing result = listingService.publishListing(id);

        assertEquals(ListingStatus.ACTIVE, result.getStatus());
        verify(listingRepository, times(1)).save(listing);
    }

    @Test
    @DisplayName("Test Update Price - Should throw Exception if new price is lower")
    void testUpdatePriceFailure() {
        when(listingRepository.findById(id)).thenReturn(Optional.of(listing));

        assertThrows(IllegalArgumentException.class, () -> {
            listingService.updateCurrentPrice(id, 50000.0); // Lebih rendah dari 100k
        });

        verify(listingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test Get Listing - Should throw Exception if ID not found")
    void testGetListingNotFound() {
        when(listingRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            listingService.getListingById(UUID.randomUUID());
        });
    }
}