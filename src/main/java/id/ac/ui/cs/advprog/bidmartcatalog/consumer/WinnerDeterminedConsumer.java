package id.ac.ui.cs.advprog.bidmartcatalog.consumer;

import com.rabbitmq.client.Channel;
import id.ac.ui.cs.advprog.bidmartcatalog.config.RabbitMQConfig;
import id.ac.ui.cs.advprog.bidmartcatalog.event.WinnerDeterminedEvent;
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
 * Consumer async untuk event WinnerDetermined.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WinnerDeterminedConsumer {

    private final ListingService listingService;
    private final ProcessedEventRepository processedEventRepository;

    @RabbitListener(
        queues = RabbitMQConfig.QUEUE_WINNER_DETERMINED,
        containerFactory = "rabbitListenerContainerFactory"
    )
    @Transactional
    public void handle(
            WinnerDeterminedEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        String eventId = event.getEventId();

        // ── Idempotency guard ─────────────────────────────────────────────────
        if (eventId == null || processedEventRepository.existsByEventId(eventId)) {
            log.warn("[WinnerDetermined] Duplicate or null eventId={}, skipping.", eventId);
            channel.basicAck(deliveryTag, false);
            return;
        }

        WinnerDeterminedEvent.Payload payload = event.getPayload();
        if (payload == null || payload.getListingId() == null) {
            log.error("[WinnerDetermined] Invalid payload for eventId={}, sending to DLQ.", eventId);
            channel.basicNack(deliveryTag, false, false);
            return;
        }

        log.info("[WinnerDetermined] Processing eventId={} listingId={}",
                eventId, payload.getListingId());

        try {
            // ── Close listing (Mark as sold/closed) ───────────────────────────
            listingService.closeListing(payload.getListingId());

            // ── Mark as processed ─────────────────────────────────────────────
            processedEventRepository.save(ProcessedEvent.builder()
                    .eventId(eventId)
                    .eventType(event.getEventType())
                    .build());

            channel.basicAck(deliveryTag, false);
            log.info("[WinnerDetermined] Successfully processed eventId={}", eventId);

        } catch (DataIntegrityViolationException ex) {
            log.warn("[WinnerDetermined] Race condition on eventId={}, treating as duplicate.", eventId);
            channel.basicAck(deliveryTag, false);

        } catch (Exception ex) {
            log.error("[WinnerDetermined] Unexpected error processing eventId={}: {}", eventId, ex.getMessage(), ex);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
