package id.ac.ui.cs.advprog.bidmartcatalog.repository;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ListingRepositoryTest {

    @Autowired
    private ListingRepository listingRepository;

    private String sellerId = "user-123";

    @BeforeEach
    void setUp() {
        Listing listing1 = Listing.builder()
                .title("Raket Badminton")
                .sellerId(sellerId)
                .status(ListingStatus.ACTIVE)
                .startingPrice(500000.0)
                .currentPrice(500000.0)
                .build();

        Listing listing2 = Listing.builder()
                .title("Sepatu Lari")
                .sellerId(sellerId)
                .status(ListingStatus.DRAFT)
                .startingPrice(750000.0)
                .currentPrice(750000.0)
                .build();

        listingRepository.save(listing1);
        listingRepository.save(listing2);
    }

    @Test
    @DisplayName("Test findBySellerId - Should return all listings for a seller")
    void testFindBySellerId() {
        List<Listing> found = listingRepository.findBySellerId(sellerId);
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(l -> l.getTitle().equals("Raket Badminton")));
    }

    @Test
    @DisplayName("Test findByStatus with Pagination")
    void testFindByStatus() {
        Page<Listing> activePage = listingRepository.findByStatus(
                ListingStatus.ACTIVE,
                PageRequest.of(0, 10)
        );

        assertEquals(1, activePage.getTotalElements());
        assertEquals("Raket Badminton", activePage.getContent().get(0).getTitle());
    }
}