package id.ac.ui.cs.advprog.bidmartcatalog.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO untuk event AuctionClosed yang dipublikasikan oleh bidmart-auction.
 *
 * Event ini hanya dipublikasikan ketika lelang berakhir WITHOUT pemenang
 * (status = UNSOLD). Untuk kasus ada pemenang, event WinnerDetermined yang dipakai.
 *
 * Contoh payload lengkap:
 * {
 *   "eventId":      "evt-9c3e2a1b-...",
 *   "eventType":    "AuctionClosed",
 *   "eventVersion": 1,
 *   "occurredAt":   "2026-05-03T09:20:00Z",
 *   "source":       "bidmart-auction",
 *   "payload": {
 *     "auctionId":    "auc-1001",
 *     "listingId":    "lst-5001",
 *     "sellerUserId": "usr-2001",
 *     "closedAt":     "2026-05-03T09:20:00Z",
 *     "allBidderIds": ["usr-3002","usr-3003"]
 *   }
 * }
 *
 * Sisi catalog: memperbarui status listing menjadi CLOSED.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuctionClosedEvent {

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
        private UUID auctionId;
        private UUID listingId;
        private UUID sellerUserId;
        private Instant closedAt;
        private List<String> allBidderIds; // nullable / bisa kosong
    }
}
