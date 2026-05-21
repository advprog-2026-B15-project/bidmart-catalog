package id.ac.ui.cs.advprog.bidmartcatalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Tabel pencatat event yang sudah berhasil diproses.
 *
 * Digunakan untuk menjamin idempotency pada consumer RabbitMQ:
 * sebelum memproses event, consumer mengecek apakah eventId sudah ada
 * di tabel ini. Jika ya → pesan di-ack tanpa diproses ulang.
 *
 * Kolom eventId dibuat UNIQUE agar DB-level constraint turut menjaga
 * keunikan, bahkan dalam kondisi race condition.
 */
@Entity
@Table(
    name = "processed_events",
    indexes = @Index(name = "idx_processed_events_event_id", columnList = "eventId", unique = true)
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID unik event dari producer (field eventId pada envelope). */
    @Column(nullable = false, unique = true, length = 100)
    private String eventId;

    /** Nama event type, misal: BidPlaced, AuctionClosed. */
    @Column(nullable = false, length = 100)
    private String eventType;

    /** Waktu event ini pertama kali berhasil diproses oleh consumer. */
    @Column(nullable = false)
    private Instant processedAt;

    @PrePersist
    void prePersist() {
        if (processedAt == null) processedAt = Instant.now();
    }
}
