package id.ac.ui.cs.advprog.bidmartcatalog.producer;

import id.ac.ui.cs.advprog.bidmartcatalog.config.RabbitMQConfig;
import id.ac.ui.cs.advprog.bidmartcatalog.event.ListingPublishedEvent;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CatalogEventProducerTest {

    @Mock RabbitTemplate rabbitTemplate;
    @InjectMocks CatalogEventProducer producer;

    @Test
    @DisplayName("Sukses: kirim event ListingPublished")
    void sendListingPublished_success() {
        Listing listing = Listing.builder()
                .id(UUID.randomUUID())
                .title("Test Item")
                .sellerId("user-123")
                .startingPrice(100.0)
                .reservePrice(150.0)
                .endTime(LocalDateTime.now().plusDays(1))
                .build();

        producer.sendListingPublished(listing);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_AUCTION_EVENTS),
                eq(RabbitMQConfig.RK_LISTING_PUBLISHED),
                any(ListingPublishedEvent.class)
        );
    }
}
