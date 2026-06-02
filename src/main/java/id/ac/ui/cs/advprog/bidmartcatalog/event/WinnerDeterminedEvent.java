package id.ac.ui.cs.advprog.bidmartcatalog.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Event yang dipublikasikan oleh Auction Service ketika lelang berakhir dengan pemenang.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WinnerDeterminedEvent {

    private String eventId;
    private String eventType;
    private int eventVersion;
    private LocalDateTime occurredAt;
    private String source;
    private Payload payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private String auctionId;
        private UUID listingId;
        private String sellerUserId;
        private String winnerUserId;
        private Long finalPrice;
        private String currency;
        private String itemName;
        private Integer quantity;
        private List<String> loserUserIds;
    }
}
