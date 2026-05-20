package id.ac.ui.cs.advprog.bidmartcatalog.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ListingRequestDTO {
    private String title;
    private String description;
    private Double startingPrice;
    private UUID categoryId;
    private LocalDateTime endTime;
}