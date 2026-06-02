package id.ac.ui.cs.advprog.bidmartcatalog.producer;

import id.ac.ui.cs.advprog.bidmartcatalog.config.RabbitMQConfig;
import id.ac.ui.cs.advprog.bidmartcatalog.event.ListingPublishedEvent;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogEventProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Mempublikasikan event ListingPublished ke RabbitMQ.
     */
    public void sendListingPublished(Listing listing) {
        ListingPublishedEvent event = ListingPublishedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(LocalDateTime.now())
                .payload(ListingPublishedEvent.Payload.builder()
                        .listingId(listing.getId())
                        .title(listing.getTitle())
                        .sellerId(listing.getSellerId())
                        .startingPrice(listing.getStartingPrice())
                        .reservePrice(listing.getReservePrice())
                        .endTime(listing.getEndTime())
                        .build())
                .build();

        if (log.isInfoEnabled()) {
            log.info("[CatalogProducer] Publishing ListingPublishedEvent for listingId={}", listing.getId());
        }
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_AUCTION_EVENTS,
                RabbitMQConfig.RK_LISTING_PUBLISHED,
                event
        );
    }
}
