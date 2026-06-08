package id.ac.ui.cs.advprog.bidmartcatalog.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event yang dipublikasikan ketika sebuah listing baru diaktifkan (DRAFT -> ACTIVE).
 * Consumer (seperti Auction Service) dapat menggunakan ini untuk memulai siklus lelang.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingPublishedEvent {

    private String eventId;
    @Builder.Default
    private String eventType = "ListingPublished";
    @Builder.Default
    private int eventVersion = 1;
    private LocalDateTime occurredAt;
    @Builder.Default
    private String source = "bidmart-catalog";
    private Payload payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private UUID listingId;
        private String title;
        private String sellerId;
        private Double startingPrice;
        private Double reservePrice;
        private LocalDateTime endTime;
    }
}
