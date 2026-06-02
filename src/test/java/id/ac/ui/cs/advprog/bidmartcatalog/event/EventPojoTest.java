package id.ac.ui.cs.advprog.bidmartcatalog.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

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

        assertThat(event.getEventId()).isEqualTo("evt-1");
        assertThat(event.getOccurredAt()).isEqualTo(now);
        assertThat(event.getEventType()).isEqualTo("ListingPublished");
        assertThat(event.getEventVersion()).isEqualTo(1);
        assertThat(event.getSource()).isEqualTo("bidmart-catalog");
        assertThat(event.getPayload().getListingId()).isEqualTo(id);
        
        // Coverage for no-args constructor and setters if necessary
        ListingPublishedEvent emptyEvent = new ListingPublishedEvent();
        emptyEvent.setEventId("id");
        assertThat(emptyEvent.getEventId()).isEqualTo("id");
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

        assertThat(event.getEventId()).isEqualTo("evt-2");
        assertThat(event.getPayload().getWinnerUserId()).isEqualTo("winner");
        
        WinnerDeterminedEvent empty = new WinnerDeterminedEvent();
        empty.setEventType("Type");
        assertThat(empty.getEventType()).isEqualTo("Type");
    }
}
