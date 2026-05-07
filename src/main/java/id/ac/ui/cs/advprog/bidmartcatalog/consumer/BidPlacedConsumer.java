package id.ac.ui.cs.advprog.bidmartcatalog.consumer;

import com.rabbitmq.client.Channel;
import id.ac.ui.cs.advprog.bidmartcatalog.config.RabbitMQConfig;
import id.ac.ui.cs.advprog.bidmartcatalog.event.BidPlacedEvent;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ProcessedEvent;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ProcessedEventRepository;
import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * Consumer async untuk event BidPlaced.
 *
 * Tanggung jawab:
 *   1. Idempotency check: lewati pesan jika eventId sudah diproses.
 *   2. Panggil ListingService#updateCurrentPrice dengan bidAmount dari event.
 *   3. Catat eventId ke tabel processed_events dalam transaksi yang sama.
 *   4. Ack pesan jika sukses; nack (tanpa requeue) jika terjadi error tak terduga
 *      sehingga pesan masuk ke DLQ.
 *
 * Out-of-order handling: ListingService#updateCurrentPrice memvalidasi bahwa
 * bidAmount harus > currentPrice, sehingga pesan lama yang terlambat datang
 * otomatis ditolak (IllegalArgumentException) dan masuk ke DLQ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BidPlacedConsumer {

    private final ListingService listingService;
    private final ProcessedEventRepository processedEventRepository;

    @RabbitListener(
        queues = RabbitMQConfig.QUEUE_BID_PLACED,
        containerFactory = "rabbitListenerContainerFactory"
    )
    @Transactional
    public void handle(
            BidPlacedEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        String eventId = event.getEventId();

        // ── Idempotency guard ─────────────────────────────────────────────────
        if (eventId == null || processedEventRepository.existsByEventId(eventId)) {
            log.warn("[BidPlaced] Duplicate or null eventId={}, skipping.", eventId);
            channel.basicAck(deliveryTag, false);
            return;
        }

        BidPlacedEvent.Payload payload = event.getPayload();
        if (payload == null || payload.getListingId() == null || payload.getBidAmount() == null) {
            log.error("[BidPlaced] Invalid payload for eventId={}, sending to DLQ.", eventId);
            channel.basicNack(deliveryTag, false, false);
            return;
        }

        log.info("[BidPlaced] Processing eventId={} listingId={} bidAmount={}",
                eventId, payload.getListingId(), payload.getBidAmount());

        try {
            // ── Update currentPrice ───────────────────────────────────────────
            listingService.updateCurrentPrice(
                    payload.getListingId(),
                    payload.getBidAmount().doubleValue());

            // ── Mark as processed ─────────────────────────────────────────────
            processedEventRepository.save(ProcessedEvent.builder()
                    .eventId(eventId)
                    .eventType(event.getEventType())
                    .build());

            channel.basicAck(deliveryTag, false);
            log.info("[BidPlaced] Successfully processed eventId={}", eventId);

        } catch (DataIntegrityViolationException ex) {
            // Race condition: eventId sudah diinsert oleh transaksi lain
            log.warn("[BidPlaced] Race condition on eventId={}, treating as duplicate.", eventId);
            channel.basicAck(deliveryTag, false);

        } catch (IllegalArgumentException ex) {
            // BidAmount <= currentPrice → pesan out-of-order, kirim ke DLQ
            log.warn("[BidPlaced] Out-of-order or invalid bid for eventId={}: {}", eventId, ex.getMessage());
            channel.basicNack(deliveryTag, false, false);

        } catch (Exception ex) {
            log.error("[BidPlaced] Unexpected error processing eventId={}: {}", eventId, ex.getMessage(), ex);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
