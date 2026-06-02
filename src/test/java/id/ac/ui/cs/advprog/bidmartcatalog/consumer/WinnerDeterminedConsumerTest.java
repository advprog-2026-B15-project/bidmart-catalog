package id.ac.ui.cs.advprog.bidmartcatalog.consumer;

import com.rabbitmq.client.Channel;
import id.ac.ui.cs.advprog.bidmartcatalog.event.WinnerDeterminedEvent;
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
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WinnerDeterminedConsumerTest {

    @Mock ListingService listingService;
    @Mock ProcessedEventRepository processedEventRepository;
    @Mock Channel channel;

    @InjectMocks WinnerDeterminedConsumer consumer;

    private static final long DELIVERY_TAG = 3L;
    private static final String EVENT_ID   = "evt-winner-001";
    private static final UUID   LISTING_ID = UUID.randomUUID();

    private WinnerDeterminedEvent buildEvent(String eventId) {
        return WinnerDeterminedEvent.builder()
                .eventId(eventId)
                .eventType("WinnerDetermined")
                .occurredAt(LocalDateTime.now())
                .payload(WinnerDeterminedEvent.Payload.builder()
                        .listingId(LISTING_ID)
                        .auctionId("auc-123")
                        .winnerUserId("user-456")
                        .finalPrice(1000000L)
                        .build())
                .build();
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
        verify(channel).basicAck(DELIVERY_TAG, false);
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
    @DisplayName("Invalid payload: nack tanpa requeue")
    void handle_nullPayload_nacksWithoutRequeue() throws IOException {
        WinnerDeterminedEvent event = WinnerDeterminedEvent.builder()
                .eventId(EVENT_ID)
                .payload(null)
                .build();

        consumer.handle(event, channel, DELIVERY_TAG);

        verify(channel).basicNack(DELIVERY_TAG, false, false);
    }

    @Test
    @DisplayName("Error sistem: nack tanpa requeue")
    void handle_systemError_nacksWithoutRequeue() throws IOException {
        when(processedEventRepository.existsByEventId(anyString())).thenReturn(false);
        doThrow(new RuntimeException("DB Error")).when(listingService).closeListing(any());

        consumer.handle(buildEvent(EVENT_ID), channel, DELIVERY_TAG);

        verify(channel).basicNack(DELIVERY_TAG, false, false);
    }
}
