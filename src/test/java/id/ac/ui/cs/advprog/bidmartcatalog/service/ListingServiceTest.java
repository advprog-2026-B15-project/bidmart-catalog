package id.ac.ui.cs.advprog.bidmartcatalog.service;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock ListingRepository listingRepository;
    @Mock StorageService storageService;
    @Mock id.ac.ui.cs.advprog.bidmartcatalog.producer.CatalogEventProducer eventProducer;
    @InjectMocks ListingServiceImpl listingService;

    private static final String NEW_TITLE = "New Title";
    private Listing activeListing;
    private UUID listingId;

    @BeforeEach
    void setUp() {
        listingId = UUID.randomUUID();
        activeListing = Listing.builder()
                .id(listingId)
                .title("Test Listing")
                .startingPrice(1_000_000.0)
                .currentPrice(1_000_000.0)
                .bidCount(0)
                .status(ListingStatus.ACTIVE)
                .build();
    }

    // ── updateCurrentPrice ────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateCurrentPrice")
    class UpdateCurrentPrice {

        @Test
        @DisplayName("Sukses: update harga dan increment bidCount")
        void success_updatesAndIncrements() {
            when(listingRepository.findById(listingId)).thenReturn(Optional.of(activeListing));
            when(listingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Listing result = listingService.updateCurrentPrice(listingId, 1_600_000.0);

            assertAll("Update current price properties",
                () -> assertThat(result.getCurrentPrice()).as("Current price should be updated").isEqualTo(1_600_000.0),
                () -> assertThat(result.getBidCount()).as("Bid count should be incremented").isEqualTo(1)
            );
        }

        @Test
        @DisplayName("Gagal: harga baru <= currentPrice → IllegalArgumentException")
        void fail_newPriceTooLow_throwsException() {
            when(listingRepository.findById(listingId)).thenReturn(Optional.of(activeListing));

            assertThatThrownBy(() -> listingService.updateCurrentPrice(listingId, 500_000.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("lebih tinggi");

            verify(listingRepository, never()).save(any());
        }

        @Test
        @DisplayName("Gagal: listing tidak ditemukan → RuntimeException")
        void fail_listingNotFound_throwsException() {
            when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> listingService.updateCurrentPrice(listingId, 1_600_000.0))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("not found");
        }
    }

    // ── closeListing ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("closeListing")
    class CloseListing {

        @Test
        @DisplayName("Sukses: status berubah menjadi CLOSED")
        void success_changesStatusToClosed() {
            when(listingRepository.findById(listingId)).thenReturn(Optional.of(activeListing));
            when(listingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Listing result = listingService.closeListing(listingId);

            ArgumentCaptor<Listing> captor = ArgumentCaptor.forClass(Listing.class);
            
            assertAll("Close listing status and verification",
                () -> {
                    verify(listingRepository).save(captor.capture());
                    assertThat(captor.getValue().getStatus()).as("Saved listing status should be CLOSED").isEqualTo(ListingStatus.CLOSED);
                },
                () -> assertThat(result.getStatus()).as("Result status should be CLOSED").isEqualTo(ListingStatus.CLOSED)
            );
        }

        @Test
        @DisplayName("Idempotent: listing sudah CLOSED tidak di-save ulang")
        void idempotent_alreadyClosed_doesNotSaveAgain() {
            activeListing.setStatus(ListingStatus.CLOSED);
            when(listingRepository.findById(listingId)).thenReturn(Optional.of(activeListing));

            Listing result = listingService.closeListing(listingId);

            assertAll("Idempotent close properties",
                () -> assertThat(result.getStatus()).as("Status should remain CLOSED").isEqualTo(ListingStatus.CLOSED),
                () -> verify(listingRepository, never()).save(any())
            );
        }

        @Test
        @DisplayName("Gagal: listing tidak ditemukan → RuntimeException")
        void fail_listingNotFound_throwsException() {
            when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> listingService.closeListing(listingId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(listingId.toString());
        }
    }

    // ── updateListing (restriksi bid) ─────────────────────────────────────────

    @Nested
    @DisplayName("updateListing (restriksi bid)")
    class UpdateListing {

        @Test
        @DisplayName("Gagal: sudah ada bid → IllegalStateException")
        void fail_hasBid_throwsIllegalState() {
            activeListing.setBidCount(3);
            when(listingRepository.findById(listingId)).thenReturn(Optional.of(activeListing));

            Listing update = Listing.builder().title(NEW_TITLE).build();
            assertThatThrownBy(() -> listingService.updateListing(listingId, update))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("Gagal: listing CLOSED → IllegalStateException")
        void fail_isClosed_throwsIllegalState() {
            activeListing.setStatus(ListingStatus.CLOSED);
            when(listingRepository.findById(listingId)).thenReturn(Optional.of(activeListing));

            Listing update = Listing.builder().title(NEW_TITLE).build();
            assertThatThrownBy(() -> listingService.updateListing(listingId, update))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // ── publishListing ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("publishListing")
    class PublishListing {

        @Test
        @DisplayName("DRAFT → ACTIVE")
        void success_draftBecomesActive() {
            activeListing.setStatus(ListingStatus.DRAFT);
            when(listingRepository.findById(listingId)).thenReturn(Optional.of(activeListing));
            when(listingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Listing result = listingService.publishListing(listingId);

            assertAll("Publish listing success",
                () -> assertThat(result.getStatus()).as("Status should be ACTIVE").isEqualTo(ListingStatus.ACTIVE),
                () -> verify(eventProducer).sendListingPublished(any(Listing.class))
            );
        }

        @Test
        @DisplayName("Sudah ACTIVE: tidak ada perubahan")
        void noOp_alreadyActive() {
            when(listingRepository.findById(listingId)).thenReturn(Optional.of(activeListing));

            Listing result = listingService.publishListing(listingId);

            assertAll("Already active no-op",
                () -> assertThat(result.getStatus()).as("Status should remain ACTIVE").isEqualTo(ListingStatus.ACTIVE),
                () -> verify(listingRepository, never()).save(any()),
                () -> verify(eventProducer, never()).sendListingPublished(any())
            );
        }
    }

    // ── getSellerStatistics ───────────────────────────────────────────────────

    @Nested
    @DisplayName("getSellerStatistics")
    class GetSellerStatistics {
        @Test
        @DisplayName("Sukses: mengembalikan map statistik")
        void success_returnsStats() {
            String sellerId = "seller-1";
            when(listingRepository.countBySellerId(sellerId)).thenReturn(10L);
            when(listingRepository.countBySellerIdAndStatus(sellerId, ListingStatus.ACTIVE)).thenReturn(5L);
            when(listingRepository.countBySellerIdAndStatus(sellerId, ListingStatus.CLOSED)).thenReturn(3L);
            when(listingRepository.countBySellerIdAndStatus(sellerId, ListingStatus.DRAFT)).thenReturn(2L);

            java.util.Map<String, Object> stats = listingService.getSellerStatistics(sellerId);

            assertAll("Seller statistics",
                () -> assertThat(stats.get("totalListings")).as("Total listings should match").isEqualTo(10L),
                () -> assertThat(stats.get("activeListings")).as("Active listings should match").isEqualTo(5L),
                () -> assertThat(stats.get("closedListings")).as("Closed listings should match").isEqualTo(3L),
                () -> assertThat(stats.get("draftListings")).as("Draft listings should match").isEqualTo(2L)
            );
        }
    }

    // ── deleteListing ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteListing")
    class DeleteListing {
        @Test
        @DisplayName("Sukses: menghapus listing tanpa bid")
        void success_deletesWithoutBid() {
            when(listingRepository.findById(listingId)).thenReturn(Optional.of(activeListing));

            listingService.deleteListing(listingId);

            verify(listingRepository).delete(activeListing);
        }

        @Test
        @DisplayName("Gagal: menghapus listing dengan bid → IllegalStateException")
        void fail_hasBid_throwsIllegalState() {
            activeListing.setBidCount(1);
            when(listingRepository.findById(listingId)).thenReturn(Optional.of(activeListing));

            assertThatThrownBy(() -> listingService.deleteListing(listingId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("createListing")
    class CreateListing {
        @Test
        @DisplayName("Sukses: membuat listing DRAFT tanpa file")
        void success_createsDraft() {
            Listing newListing = Listing.builder().startingPrice(100.0).build();
            when(listingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Listing result = listingService.createListing(newListing, null);

            assertAll("Create draft listing properties",
                () -> assertThat(result.getStatus()).as("Status should be DRAFT").isEqualTo(ListingStatus.DRAFT),
                () -> assertThat(result.getCurrentPrice()).as("Current price should match starting price").isEqualTo(100.0)
            );
        }

        @Test
        @DisplayName("Sukses: membuat listing dengan file gambar")
        void success_withFiles() throws java.io.IOException {
            Listing newListing = Listing.builder().startingPrice(100.0).images(new java.util.ArrayList<>()).build();
            org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(storageService.store(file)).thenReturn("/uploads/test.jpg");
            when(listingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Listing result = listingService.createListing(newListing, new org.springframework.web.multipart.MultipartFile[]{file});

            assertAll("Create listing with files properties",
                () -> assertThat(result.getImages()).as("Images list size should be 1").hasSize(1),
                () -> assertThat(result.getImages().get(0).isPrimary()).as("First image should be primary").isTrue(),
                () -> assertThat(result.getImages().get(0).getImageUrl()).as("Image URL should match").isEqualTo("/uploads/test.jpg")
            );
        }

        @Test
        @DisplayName("Sukses: abaikan file kosong")
        void success_skipsEmptyFiles() {
            Listing newListing = Listing.builder().startingPrice(100.0).images(new java.util.ArrayList<>()).build();
            org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
            when(file.isEmpty()).thenReturn(true);
            when(listingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Listing result = listingService.createListing(newListing, new org.springframework.web.multipart.MultipartFile[]{file});

            assertThat(result.getImages()).isEmpty();
        }
        
        @Test
        @DisplayName("Gagal: IO error saat simpan file")
        void fail_ioError_throwsException() throws java.io.IOException {
            Listing newListing = Listing.builder().startingPrice(100.0).images(new java.util.ArrayList<>()).build();
            org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(storageService.store(file)).thenThrow(new java.io.IOException("Disk full"));

            assertThatThrownBy(() -> listingService.createListing(newListing, new org.springframework.web.multipart.MultipartFile[]{file}))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Gagal menyimpan file");
        }
    }

    @Nested
    @DisplayName("Read Operations")
    class ReadOperations {
        @Test
        @DisplayName("getAllListings")
        void testGetAllListings() {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
            when(listingRepository.findAll(pageable)).thenReturn(org.springframework.data.domain.Page.empty());

            listingService.getAllListings(pageable);

            verify(listingRepository).findAll(pageable);
        }

        @Test
        @DisplayName("getActiveListings")
        void testGetActiveListings() {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
            when(listingRepository.findByStatus(ListingStatus.ACTIVE, pageable)).thenReturn(org.springframework.data.domain.Page.empty());

            listingService.getActiveListings(pageable);

            verify(listingRepository).findByStatus(ListingStatus.ACTIVE, pageable);
        }

        @Test
        @DisplayName("searchAndFilterListings")
        void testSearchAndFilterListings() {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
            listingService.searchAndFilterListings("title", null, 0.0, 100.0, ListingStatus.ACTIVE, pageable);
            verify(listingRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable));
        }
    }

    @Nested
    @DisplayName("updateListing (full)")
    class UpdateListingFull {
        @Test
        @DisplayName("Sukses: update data listing")
        void success_updatesData() {
            when(listingRepository.findById(listingId)).thenReturn(Optional.of(activeListing));
            when(listingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Listing update = Listing.builder()
                    .title(NEW_TITLE)
                    .description("New Desc")
                    .category(new id.ac.ui.cs.advprog.bidmartcatalog.model.Category())
                    .reservePrice(2_000_000.0)
                    .build();

            Listing result = listingService.updateListing(listingId, update);

            assertAll("Update data listing properties",
                () -> assertThat(result.getTitle()).as("Title should be updated").isEqualTo(NEW_TITLE),
                () -> assertThat(result.getDescription()).as("Description should be updated").isEqualTo("New Desc"),
                () -> assertThat(result.getReservePrice()).as("Reserve price should be updated").isEqualTo(2_000_000.0)
            );
        }
    }
}
