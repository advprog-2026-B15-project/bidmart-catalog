package id.ac.ui.cs.advprog.bidmartcatalog.dto;

import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingResponseDTO {
    private UUID id;
    private String title;
    private String description;
    private String categoryName;
    private UUID categoryId;
    private CategoryResponseDTO category;
    private List<String> imageUrls;
    private String sellerId;
    private Double startingPrice;
    private Double currentPrice;
    private Double reservePrice;
    private LocalDateTime endTime;
    private ListingStatus status;
    private Integer bidCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryResponseDTO {
        private UUID id;
        private String name;
    }
}
