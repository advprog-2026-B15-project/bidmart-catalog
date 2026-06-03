package id.ac.ui.cs.advprog.bidmartcatalog.dto;

import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ListingResponseDTOTest {

    @Test
    @DisplayName("Test ListingResponseDTO builder and getters")
    void testListingResponseDTO() {
        UUID id = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        List<String> images = List.of("img1.jpg");

        ListingResponseDTO dto = ListingResponseDTO.builder()
                .id(id)
                .title("Title")
                .description("Desc")
                .categoryName("Cat")
                .categoryId(catId)
                .imageUrls(images)
                .sellerId("seller")
                .startingPrice(100.0)
                .currentPrice(150.0)
                .reservePrice(200.0)
                .endTime(now)
                .status(ListingStatus.ACTIVE)
                .bidCount(5)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertAll("Verify ListingResponseDTO properties",
            () -> assertEquals(id, dto.getId(), "ID should match"),
            () -> assertEquals("Title", dto.getTitle(), "Title should match"),
            () -> assertEquals("Desc", dto.getDescription(), "Description should match"),
            () -> assertEquals("Cat", dto.getCategoryName(), "Category name should match"),
            () -> assertEquals(catId, dto.getCategoryId(), "Category ID should match"),
            () -> assertEquals(images, dto.getImageUrls(), "Images should match"),
            () -> assertEquals("seller", dto.getSellerId(), "Seller ID should match"),
            () -> assertEquals(100.0, dto.getStartingPrice(), "Starting price should match"),
            () -> assertEquals(150.0, dto.getCurrentPrice(), "Current price should match"),
            () -> assertEquals(200.0, dto.getReservePrice(), "Reserve price should match"),
            () -> assertEquals(now, dto.getEndTime(), "End time should match"),
            () -> assertEquals(ListingStatus.ACTIVE, dto.getStatus(), "Status should match"),
            () -> assertEquals(5, dto.getBidCount(), "Bid count should match"),
            () -> assertEquals(now, dto.getCreatedAt(), "Created at should match"),
            () -> assertEquals(now, dto.getUpdatedAt(), "Updated at should match")
        );
    }

    @Test
    @DisplayName("Test ListingResponseDTO NoArgsConstructor and Setters")
    void testListingResponseDTONoArgsAndSetters() {
        ListingResponseDTO dto = new ListingResponseDTO();
        dto.setTitle("SetTitle");
        assertEquals("SetTitle", dto.getTitle(), "Setter should work for title");
    }
}
