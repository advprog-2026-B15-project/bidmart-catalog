package id.ac.ui.cs.advprog.bidmartcatalog.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ListingRequestDTO {
    private String title;
    private String description;
    private Double startingPrice;
    private Double reservePrice;
    private UUID categoryId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;
}