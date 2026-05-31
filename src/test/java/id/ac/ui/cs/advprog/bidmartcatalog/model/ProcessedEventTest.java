package id.ac.ui.cs.advprog.bidmartcatalog.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProcessedEventTest {

    private ProcessedEvent processedEvent;

    @BeforeEach
    void setUp() {
        processedEvent = ProcessedEvent.builder()
                .id(1L)
                .eventId("evt-123")
                .eventType("BidPlaced")
                .processedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Test ProcessedEvent Getters and Setters")
    void testGettersAndSetters() {
        processedEvent.setEventId("evt-456");
        processedEvent.setEventType("AuctionClosed");
        Instant now = Instant.now();
        processedEvent.setProcessedAt(now);

        assertEquals("evt-456", processedEvent.getEventId());
        assertEquals("AuctionClosed", processedEvent.getEventType());
        assertEquals(now, processedEvent.getProcessedAt());
    }

    @Test
    @DisplayName("Test PrePersist sets processedAt if null")
    void testPrePersist() {
        ProcessedEvent newEvent = new ProcessedEvent();
        newEvent.prePersist();
        assertNotNull(newEvent.getProcessedAt());
    }

    @Test
    @DisplayName("Test PrePersist does not overwrite processedAt if already set")
    void testPrePersistWithExistingDate() {
        Instant existingDate = Instant.now().minusSeconds(100);
        ProcessedEvent newEvent = new ProcessedEvent();
        newEvent.setProcessedAt(existingDate);
        newEvent.prePersist();
        assertEquals(existingDate, newEvent.getProcessedAt());
    }
}
