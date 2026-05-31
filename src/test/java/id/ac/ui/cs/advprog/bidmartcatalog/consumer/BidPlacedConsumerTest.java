package id.ac.ui.cs.advprog.bidmartcatalog.consumer;

import com.rabbitmq.client.Channel;
import id.ac.ui.cs.advprog.bidmartcatalog.event.BidPlacedEvent;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BidPlacedConsumerTest {

    @Mock ListingService listingService;
    @Mock ProcessedEventRepository processedEventRepository;
    @Mock Channel channel;

    @InjectMocks BidPlacedConsumer consumer;

    private static final long DELIVERY_TAG = 1L;
    private static final String EVENT_ID   = "evt-bid-001";
    private static final UUID   LISTING_ID = UUID.randomUUID();
    private static final Long   BID_AMOUNT = 1_600_000L;

    private BidPlacedEvent buildEvent(String eventId) {
        BidPlacedEvent event = new BidPlacedEvent();
        event.setEventId(eventId);
        event.setEventType("BidPlaced");
        event.setOccurredAt(Instant.now());

        BidPlacedEvent.Payload payload = new BidPlacedEvent.Payload();
        payload.setListingId(LISTING_ID);
        payload.setBidAmount(BID_AMOUNT);
        event.setPayload(payload);
        return event;
    }

    @Test
    @DisplayName("Sukses: update price dan ack saat event baru")
    void handle_newEvent_updatesAndAcks() throws IOException {
        when(processedEventRepository.existsByEventId(anyString())).thenReturn(false);
        when(processedEventRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        consumer.handle(buildEvent(EVENT_ID), channel, DELIVERY_TAG);

        verify(listingService).updateCurrentPrice(LISTING_ID, BID_AMOUNT.doubleValue());
        ArgumentCaptor<ProcessedEvent> captor = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(processedEventRepository).save(captor.capture());
        assertThat(captor.getValue().getEventId()).isEqualTo(EVENT_ID);
        verify(channel).basicAck(DELIVERY_TAG, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    @DisplayName("Idempotency: skip dan ack saat eventId sudah ada")
    void handle_duplicateEvent_skipsAndAcks() throws IOException {
        when(processedEventRepository.existsByEventId(EVENT_ID)).thenReturn(true);

        consumer.handle(buildEvent(EVENT_ID), channel, DELIVERY_TAG);

        verify(listingService, never()).updateCurrentPrice(any(), anyDouble());
        verify(channel).basicAck(DELIVERY_TAG, false);
    }

    @Test
    @DisplayName("Idempotency: skip dan ack saat eventId null")
    void handle_nullEventId_skipsAndAcks() throws IOException {
        consumer.handle(buildEvent(null), channel, DELIVERY_TAG);

        verify(listingService, never()).updateCurrentPrice(any(), anyDouble());
        verify(channel).basicAck(DELIVERY_TAG, false);
    }

    @Test
    @DisplayName("Invalid payload: nack tanpa requeue")
    void handle_nullPayload_nacksWithoutRequeue() throws IOException {
        BidPlacedEvent event = new BidPlacedEvent();
        event.setEventId(EVENT_ID);
        event.setPayload(null);

        consumer.handle(event, channel, DELIVERY_TAG);

        verify(channel).basicNack(DELIVERY_TAG, false, false);
    }

    @Test
    @DisplayName("Out-of-order bid: nack tanpa requeue (IllegalArgumentException)")
    void handle_outOfOrderBid_nacksWithoutRequeue() throws IOException {
        when(processedEventRepository.existsByEventId(anyString())).thenReturn(false);
        doThrow(new IllegalArgumentException("Harga terlalu rendah"))
                .when(listingService).updateCurrentPrice(any(), anyDouble());

        consumer.handle(buildEvent(EVENT_ID), channel, DELIVERY_TAG);

        verify(channel).basicNack(DELIVERY_TAG, false, false);
        verify(processedEventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Unexpected exception: nack tanpa requeue")
    void handle_unexpectedException_nacksWithoutRequeue() throws IOException {
        when(processedEventRepository.existsByEventId(anyString())).thenReturn(false);
        doThrow(new RuntimeException("DB down"))
                .when(listingService).updateCurrentPrice(any(), anyDouble());

        consumer.handle(buildEvent(EVENT_ID), channel, DELIVERY_TAG);

        verify(channel).basicNack(DELIVERY_TAG, false, false);
    }
}
