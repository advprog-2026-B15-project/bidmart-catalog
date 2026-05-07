package id.ac.ui.cs.advprog.bidmartcatalog.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO untuk event BidPlaced yang dipublikasikan oleh bidmart-auction.
 *
 * Contoh payload lengkap:
 * {
 *   "eventId":   "f3b19d42-8e3f-4a7c-9d1b-2f6e4a8c5b19",
 *   "eventType": "BidPlaced",
 *   "eventVersion": 1,
 *   "occurredAt": "2026-04-14T13:25:00Z",
 *   "source":    "bidmart-auction",
 *   "payload": {
 *     "bidId":                "9f2a8c11-...",
 *     "auctionId":            "7b2a9c11-...",
 *     "listingId":            "d290f1ee-...",
 *     "sellerUserId":         "e1a4d8c2-...",
 *     "bidderUserId":         "e1a4d8c2-...",
 *     "previousBidderUserId": "c5b19d42-...",
 *     "bidAmount":            1600000,
 *     "itemName":             "Mechanical Keyboard Custom"
 *   }
 * }
 *
 * Sisi catalog hanya memerlukan listingId dan bidAmount untuk
 * memperbarui currentPrice dan bidCount.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidPlacedEvent {

    private String eventId;
    private String eventType;
    private Integer eventVersion;
    private Instant occurredAt;
    private String source;

    private Payload payload;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {
        private UUID bidId;
        private UUID auctionId;
        private UUID listingId;
        private UUID sellerUserId;
        private UUID bidderUserId;
        private UUID previousBidderUserId; // nullable – bid pertama
        private Long bidAmount;
        private String itemName;
    }
}
