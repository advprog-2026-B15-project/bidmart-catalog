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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ListingRepositoryTest {

    @Autowired
    private ListingRepository listingRepository;

    private static final String RAKET_BADMINTON = "Raket Badminton";
    private String sellerId = "user-123";

    @BeforeEach
    void setUp() {
        // Tentukan waktu akhir, misal: 7 hari dari sekarang
        java.time.LocalDateTime futureDate = java.time.LocalDateTime.now().plusDays(7);

        Listing listing1 = Listing.builder()
                .title(RAKET_BADMINTON)
                .sellerId(sellerId)
                .status(ListingStatus.ACTIVE)
                .startingPrice(500000.0)
                .currentPrice(500000.0)
                .endTime(futureDate) // TAMBAHKAN INI
                .build();

        Listing listing2 = Listing.builder()
                .title("Sepatu Lari")
                .sellerId(sellerId)
                .status(ListingStatus.DRAFT)
                .startingPrice(750000.0)
                .currentPrice(750000.0)
                .endTime(futureDate) // TAMBAHKAN INI
                .build();

        listingRepository.save(listing1);
        listingRepository.save(listing2);
    }
    @Test
    @DisplayName("Test findBySellerId - Should return all listings for a seller")
    void testFindBySellerId() {
        List<Listing> found = listingRepository.findBySellerId(sellerId);
        assertAll("Verify find by seller ID",
            () -> assertEquals(2, found.size(), "Should return 2 listings"),
            () -> assertTrue(found.stream().anyMatch(l -> RAKET_BADMINTON.equals(l.getTitle())), "Should contain " + RAKET_BADMINTON)
        );
    }

    @Test
    @DisplayName("Test findByStatus with Pagination")
    void testFindByStatus() {
        Page<Listing> activePage = listingRepository.findByStatus(
                ListingStatus.ACTIVE,
                PageRequest.of(0, 10)
        );

        assertAll("Verify find by status",
            () -> assertEquals(1, activePage.getTotalElements(), "Should return 1 active listing"),
            () -> assertEquals(RAKET_BADMINTON, activePage.getContent().get(0).getTitle(), "Title should match " + RAKET_BADMINTON)
        );
    }
}