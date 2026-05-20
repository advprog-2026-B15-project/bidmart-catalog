package id.ac.ui.cs.advprog.bidmartcatalog.consumer;

import com.rabbitmq.client.Channel;
import id.ac.ui.cs.advprog.bidmartcatalog.event.AuctionClosedEvent;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ProcessedEvent;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ProcessedEventRepository;
import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionClosedConsumerTest {

    @Mock ListingService listingService;
    @Mock ProcessedEventRepository processedEventRepository;
    @Mock Channel channel;

    @InjectMocks AuctionClosedConsumer consumer;

    private static final long DELIVERY_TAG = 2L;
    private static final String EVENT_ID   = "evt-closed-001";
    private static final UUID   LISTING_ID = UUID.randomUUID();

    private AuctionClosedEvent buildEvent(String eventId) {
        AuctionClosedEvent event = new AuctionClosedEvent();
        event.setEventId(eventId);
        event.setEventType("AuctionClosed");
        event.setOccurredAt(Instant.now());

        AuctionClosedEvent.Payload payload = new AuctionClosedEvent.Payload();
        payload.setListingId(LISTING_ID);
        payload.setAuctionId(UUID.randomUUID());
        payload.setClosedAt(Instant.now());
        event.setPayload(payload);
        return event;
    }

    @Test
    @DisplayName("Sukses: close listing dan ack saat event baru")
    void handle_newEvent_closesAndAcks() throws IOException {
        when(processedEventRepository.existsByEventId(anyString())).thenReturn(false);
        when(processedEventRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        consumer.handle(buildEvent(EVENT_ID), channel, DELIVERY_TAG);

        verify(listingService).closeListing(LISTING_ID);
        ArgumentCaptor<ProcessedEvent> captor = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(processedEventRepository).save(captor.capture());
        assertThat(captor.getValue().getEventId()).isEqualTo(EVENT_ID);
        assertThat(captor.getValue().getEventType()).isEqualTo("AuctionClosed");
        verify(channel).basicAck(DELIVERY_TAG, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    @DisplayName("Idempotency: skip dan ack saat eventId sudah ada")
    void handle_duplicateEvent_skipsAndAcks() throws IOException {
        when(processedEventRepository.existsByEventId(EVENT_ID)).thenReturn(true);

        consumer.handle(buildEvent(EVENT_ID), channel, DELIVERY_TAG);

        verify(listingService, never()).closeListing(any());
        verify(channel).basicAck(DELIVERY_TAG, false);
    }

    @Test
    @DisplayName("Idempotency: skip dan ack saat eventId null")
    void handle_nullEventId_skipsAndAcks() throws IOException {
        consumer.handle(buildEvent(null), channel, DELIVERY_TAG);

        verify(listingService, never()).closeListing(any());
        verify(channel).basicAck(DELIVERY_TAG, false);
    }

    @Test
    @DisplayName("Invalid payload: nack tanpa requeue")
    void handle_nullPayload_nacksWithoutRequeue() throws IOException {
        AuctionClosedEvent event = new AuctionClosedEvent();
        event.setEventId(EVENT_ID);
        event.setPayload(null);

        consumer.handle(event, channel, DELIVERY_TAG);

        verify(channel).basicNack(DELIVERY_TAG, false, false);
        verify(listingService, never()).closeListing(any());
    }

    @Test
    @DisplayName("Listing not found: nack tanpa requeue")
    void handle_listingNotFound_nacksWithoutRequeue() throws IOException {
        when(processedEventRepository.existsByEventId(anyString())).thenReturn(false);
        doThrow(new RuntimeException("Listing not found"))
                .when(listingService).closeListing(any());

        consumer.handle(buildEvent(EVENT_ID), channel, DELIVERY_TAG);

        verify(channel).basicNack(DELIVERY_TAG, false, false);
        verify(processedEventRepository, never()).save(any());
    }
}
