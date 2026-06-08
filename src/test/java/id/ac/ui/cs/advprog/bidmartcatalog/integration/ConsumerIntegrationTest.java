package id.ac.ui.cs.advprog.bidmartcatalog.integration;

import id.ac.ui.cs.advprog.bidmartcatalog.consumer.AuctionClosedConsumer;
import id.ac.ui.cs.advprog.bidmartcatalog.consumer.BidPlacedConsumer;
import id.ac.ui.cs.advprog.bidmartcatalog.event.AuctionClosedEvent;
import id.ac.ui.cs.advprog.bidmartcatalog.event.BidPlacedEvent;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ProcessedEvent;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ListingRepository;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ProcessedEventRepository;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Integration test untuk alur BidPlaced dan AuctionClosed.
 *
 * Strategi:
 *   - H2 in-memory (application-test.properties).
 *   - @MockBean ConnectionFactory menggantikan bean yang dibuat RabbitAutoConfiguration,
 *     sehingga context naik tanpa broker nyata. RabbitTemplate juga di-mock
 *     untuk menghindari dependency transitif ke ConnectionFactory.
 *   - Consumer dipanggil langsung (bukan via listener container) dan hasilnya
 *     diverifikasi di DB.
 */
@SpringBootTest
@ActiveProfiles("test")
class ConsumerIntegrationTest {

    // ── Mock AMQP beans agar tidak butuh broker nyata ─────────────────────────
    @MockitoBean ConnectionFactory connectionFactory;
    @MockitoBean RabbitTemplate rabbitTemplate;

    @Autowired BidPlacedConsumer bidPlacedConsumer;
    @Autowired AuctionClosedConsumer auctionClosedConsumer;
    @Autowired ListingRepository listingRepository;
    @Autowired ProcessedEventRepository processedEventRepository;

    private Listing savedListing;
    private Channel mockChannel;

    @BeforeEach
    void setUp() {
        processedEventRepository.deleteAll();
        listingRepository.deleteAll();

        savedListing = listingRepository.save(Listing.builder()
                .title("Test Keyboard")
                .description("Mechanical keyboard for testing")
                .sellerId("usr-seller-01")
                .startingPrice(1_000_000.0)
                .currentPrice(1_000_000.0)
                .bidCount(0)
                .status(ListingStatus.ACTIVE)
                .endTime(LocalDateTime.now().plusDays(1))
                .build());

        mockChannel = mock(Channel.class);
    }

    @AfterEach
    void tearDown() {
        processedEventRepository.deleteAll();
        listingRepository.deleteAll();
    }

    // ── BidPlaced ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("BidPlaced: currentPrice dan bidCount ter-update di DB")
    void bidPlaced_updatesCurrentPriceInDb() throws IOException {
        String eventId = UUID.randomUUID().toString();
        bidPlacedConsumer.handle(buildBidPlacedEvent(eventId, savedListing.getId(), 1_500_000L),
                mockChannel, 1L);

        Listing updated = listingRepository.findById(savedListing.getId()).orElseThrow();
        assertAll("Verify price and bid count update",
            () -> assertThat(updated.getCurrentPrice()).as("Current price should be updated").isEqualTo(1_500_000.0),
            () -> assertThat(updated.getBidCount()).as("Bid count should be incremented").isEqualTo(1)
        );
    }

    @Test
    @DisplayName("BidPlaced: ProcessedEvent tersimpan setelah sukses")
    void bidPlaced_savesProcessedEvent() throws IOException {
        String eventId = UUID.randomUUID().toString();
        bidPlacedConsumer.handle(buildBidPlacedEvent(eventId, savedListing.getId(), 1_500_000L),
                mockChannel, 1L);

        ProcessedEvent pe = processedEventRepository.findAll().get(0);
        assertAll("Verify processed event saved",
            () -> assertThat(processedEventRepository.existsByEventId(eventId)).as("Processed event should exist").isTrue(),
            () -> assertThat(pe.getEventType()).as("Event type should be BidPlaced").isEqualTo("BidPlaced"),
            () -> assertThat(pe.getProcessedAt()).as("Processed date should be set").isNotNull()
        );
    }

    @Test
    @DisplayName("BidPlaced: event duplikat tidak mengubah state DB")
    void bidPlaced_duplicateEvent_doesNotUpdateDb() throws IOException {
        String eventId = UUID.randomUUID().toString();
        bidPlacedConsumer.handle(buildBidPlacedEvent(eventId, savedListing.getId(), 1_500_000L),
                mockChannel, 1L);
        bidPlacedConsumer.handle(buildBidPlacedEvent(eventId, savedListing.getId(), 1_800_000L),
                mockChannel, 2L);

        Listing after = listingRepository.findById(savedListing.getId()).orElseThrow();
        assertAll("Verify duplicate event does not update DB",
            () -> assertThat(after.getCurrentPrice()).as("Price should remain from first event").isEqualTo(1_500_000.0),
            () -> assertThat(after.getBidCount()).as("Bid count should remain from first event").isEqualTo(1)
        );
    }

    @Test
    @DisplayName("BidPlaced: out-of-order bid tidak mengubah state DB")
    @SuppressWarnings("PMD.EmptyCatchBlock")
    void bidPlaced_outOfOrderBid_doesNotUpdateDb() throws IOException {
        String eventId = UUID.randomUUID().toString();
        try {
            bidPlacedConsumer.handle(buildBidPlacedEvent(eventId, savedListing.getId(), 800_000L),
                    mockChannel, 1L);
        } catch (Exception e) {
            // Expected: UnexpectedRollbackException might happen here, we ignore it to check the DB state.
            // PMD: Ignored because we verify the DB state after failure.
            // intentionally ignored
        }

        Listing unchanged = listingRepository.findById(savedListing.getId()).orElseThrow();
        assertAll("Verify out-of-order bid does not update DB",
            () -> assertThat(unchanged.getCurrentPrice()).as("Price should remain unchanged").isEqualTo(1_000_000.0),
            () -> assertThat(unchanged.getBidCount()).as("Bid count should remain unchanged").isEqualTo(0)
        );
    }

    // ── AuctionClosed ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("AuctionClosed: status listing berubah menjadi CLOSED di DB")
    void auctionClosed_changesStatusToClosedInDb() throws IOException {
        String eventId = UUID.randomUUID().toString();
        auctionClosedConsumer.handle(buildAuctionClosedEvent(eventId, savedListing.getId()),
                mockChannel, 1L);

        Listing closed = listingRepository.findById(savedListing.getId()).orElseThrow();
        assertThat(closed.getStatus()).as("Status should be CLOSED").isEqualTo(ListingStatus.CLOSED);
    }

    @Test
    @DisplayName("AuctionClosed: ProcessedEvent tersimpan setelah sukses")
    void auctionClosed_savesProcessedEvent() throws IOException {
        String eventId = UUID.randomUUID().toString();
        auctionClosedConsumer.handle(buildAuctionClosedEvent(eventId, savedListing.getId()),
                mockChannel, 1L);

        assertAll("Verify processed event saved",
            () -> assertThat(processedEventRepository.existsByEventId(eventId)).as("Processed event should exist").isTrue(),
            () -> assertThat(processedEventRepository.findAll().get(0).getEventType())
                    .as("Event type should be AuctionClosed").isEqualTo("AuctionClosed")
        );
    }

    @Test
    @DisplayName("AuctionClosed: event duplikat tidak mengubah state DB dua kali")
    void auctionClosed_duplicateEvent_isIdempotent() throws IOException {
        String eventId = UUID.randomUUID().toString();
        auctionClosedConsumer.handle(buildAuctionClosedEvent(eventId, savedListing.getId()),
                mockChannel, 1L);
        auctionClosedConsumer.handle(buildAuctionClosedEvent(eventId, savedListing.getId()),
                mockChannel, 2L);

        Listing closed = listingRepository.findById(savedListing.getId()).orElseThrow();
        assertAll("Verify idempotency of AuctionClosed event",
            () -> assertThat(processedEventRepository.count()).as("Only one processed event should be saved").isEqualTo(1),
            () -> assertThat(closed.getStatus()).as("Status should be CLOSED").isEqualTo(ListingStatus.CLOSED)
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private BidPlacedEvent buildBidPlacedEvent(String eventId, UUID listingId, Long amount) {
        BidPlacedEvent event = new BidPlacedEvent();
        event.setEventId(eventId);
        event.setEventType("BidPlaced");
        event.setOccurredAt(Instant.now());

        BidPlacedEvent.Payload payload = new BidPlacedEvent.Payload();
        payload.setListingId(listingId);
        payload.setBidAmount(amount);
        payload.setBidId(UUID.randomUUID());
        event.setPayload(payload);
        return event;
    }

    private AuctionClosedEvent buildAuctionClosedEvent(String eventId, UUID listingId) {
        AuctionClosedEvent event = new AuctionClosedEvent();
        event.setEventId(eventId);
        event.setEventType("AuctionClosed");
        event.setOccurredAt(Instant.now());

        AuctionClosedEvent.Payload payload = new AuctionClosedEvent.Payload();
        payload.setListingId(listingId);
        payload.setAuctionId(UUID.randomUUID());
        payload.setClosedAt(Instant.now());
        event.setPayload(payload);
        return event;
    }
}
