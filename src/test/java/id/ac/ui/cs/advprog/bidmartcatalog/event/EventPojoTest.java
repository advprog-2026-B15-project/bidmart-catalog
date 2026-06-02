package id.ac.ui.cs.advprog.bidmartcatalog.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;

class EventPojoTest {

    @Test
    @DisplayName("ListingPublishedEvent POJO test")
    void listingPublishedEventTest() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        ListingPublishedEvent.Payload payload = ListingPublishedEvent.Payload.builder()
                .listingId(id)
                .title("Title")
                .sellerId("seller")
                .startingPrice(100.0)
                .reservePrice(200.0)
                .endTime(now)
                .build();

        ListingPublishedEvent event = ListingPublishedEvent.builder()
                .eventId("evt-1")
                .occurredAt(now)
                .payload(payload)
                .build();

        assertAll("Verify ListingPublishedEvent POJO fields",
            () -> assertThat(event.getEventId()).as("Event ID should match").isEqualTo("evt-1"),
            () -> assertThat(event.getOccurredAt()).as("OccurredAt should match").isEqualTo(now),
            () -> assertThat(event.getEventType()).as("Event type should be ListingPublished").isEqualTo("ListingPublished"),
            () -> assertThat(event.getEventVersion()).as("Version should be 1").isEqualTo(1),
            () -> assertThat(event.getSource()).as("Source should be bidmart-catalog").isEqualTo("bidmart-catalog"),
            () -> assertThat(event.getPayload().getListingId()).as("Payload listing ID should match").isEqualTo(id),
            () -> {
                ListingPublishedEvent emptyEvent = new ListingPublishedEvent();
                emptyEvent.setEventId("id");
                assertThat(emptyEvent.getEventId()).as("Setter/Getter for event ID should work").isEqualTo("id");
            }
        );
    }

    @Test
    @DisplayName("WinnerDeterminedEvent POJO test")
    void winnerDeterminedEventTest() {
        UUID id = UUID.randomUUID();
        WinnerDeterminedEvent.Payload payload = WinnerDeterminedEvent.Payload.builder()
                .listingId(id)
                .auctionId("auc-1")
                .winnerUserId("winner")
                .finalPrice(1000L)
                .loserUserIds(Collections.singletonList("loser"))
                .build();

        WinnerDeterminedEvent event = WinnerDeterminedEvent.builder()
                .eventId("evt-2")
                .payload(payload)
                .build();

        assertAll("Verify WinnerDeterminedEvent POJO fields",
            () -> assertThat(event.getEventId()).as("Event ID should match").isEqualTo("evt-2"),
            () -> assertThat(event.getPayload().getWinnerUserId()).as("Winner user ID should match").isEqualTo("winner"),
            () -> {
                WinnerDeterminedEvent empty = new WinnerDeterminedEvent();
                empty.setEventType("Type");
                assertThat(empty.getEventType()).as("Setter/Getter for event type should work").isEqualTo("Type");
            }
        );
    }
}
