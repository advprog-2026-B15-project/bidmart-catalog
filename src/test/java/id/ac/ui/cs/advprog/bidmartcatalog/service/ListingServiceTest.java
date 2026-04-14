package id.ac.ui.cs.advprog.bidmartcatalog.service;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @InjectMocks
    private ListingService listingService;

    private Listing listing;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        listing = new Listing();
        listing.setId(id);
        listing.setStartingPrice(100000.0);
        listing.setCurrentPrice(100000.0);
        listing.setStatus(ListingStatus.DRAFT);
    }

    @Test
    @DisplayName("Test Publish Listing - Success from DRAFT to ACTIVE")
    void testPublishListingSuccess() {
        when(listingRepository.findById(id)).thenReturn(Optional.of(listing));
        when(listingRepository.save(any(Listing.class))).thenReturn(listing);

        Listing result = listingService.publishListing(id);

        assertEquals(ListingStatus.ACTIVE, result.getStatus());
        verify(listingRepository, times(1)).save(listing);
    }

    @Test
    @DisplayName("Test Update Price - Should throw Exception if new price is lower")
    void testUpdatePriceFailure() {
        when(listingRepository.findById(id)).thenReturn(Optional.of(listing));

        assertThrows(IllegalArgumentException.class, () -> {
            listingService.updateCurrentPrice(id, 50000.0); // Lebih rendah dari 100k
        });

        verify(listingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test Get Listing - Should throw Exception if ID not found")
    void testGetListingNotFound() {
        when(listingRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            listingService.getListingById(UUID.randomUUID());
        });
    }

    @Test
    @DisplayName("Test Create Listing - Dengan Multi-Gambar (Coverage Full Loop & I/O)")
    void testCreateListingWithImages() {
        // Persiapkan Listing dan Mock Files
        listing.setImages(new ArrayList<>());
        MockMultipartFile file1 = new MockMultipartFile("imageFiles", "bola.jpg", "image/jpeg", "konten bola".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("imageFiles", "bola2.jpg", "image/jpeg", "konten bola 2".getBytes());
        MultipartFile[] files = {file1, file2};

        when(listingRepository.save(any(Listing.class))).thenAnswer(i -> i.getArguments()[0]);

        Listing result = listingService.createListing(listing, files);

        assertAll(
                () -> assertEquals(ListingStatus.DRAFT, result.getStatus()),
                () -> assertEquals(2, result.getImages().size(), "Harusnya ada 2 gambar tersimpan"),
                () -> assertTrue(result.getImages().get(0).isPrimary(), "Gambar pertama harus primary"),
                () -> assertFalse(result.getImages().get(1).isPrimary(), "Gambar kedua tidak boleh primary"),
                () -> assertNotNull(result.getCreatedAt())
        );
    }

    @Test
    @DisplayName("Test Create Listing - Dengan File Kosong (Coverage Continue)")
    void testCreateListingWithEmptyFile() {
        listing.setImages(new ArrayList<>());
        MockMultipartFile emptyFile = new MockMultipartFile("imageFiles", "", "image/jpeg", new byte[0]);
        MultipartFile[] files = {emptyFile};

        when(listingRepository.save(any(Listing.class))).thenAnswer(i -> i.getArguments()[0]);

        Listing result = listingService.createListing(listing, files);

        assertEquals(0, result.getImages().size(), "File kosong harusnya di-skip");
        verify(listingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Test Publish Listing - Return jika sudah ACTIVE (Coverage Status Check)")
    void testPublishListingAlreadyActive() {
        listing.setStatus(ListingStatus.ACTIVE);
        when(listingRepository.findById(id)).thenReturn(Optional.of(listing));

        Listing result = listingService.publishListing(id);

        assertEquals(ListingStatus.ACTIVE, result.getStatus());
        // Verifikasi save tidak dipanggil karena tidak ada perubahan status
        verify(listingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test Get Active Listings - Memastikan Filter Status (Coverage Pagination)")
    void testGetActiveListings() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Listing> page = new PageImpl<>(List.of(listing));

        when(listingRepository.findByStatus(ListingStatus.ACTIVE, pageable)).thenReturn(page);

        Page<Listing> result = listingService.getActiveListings(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(listingRepository).findByStatus(ListingStatus.ACTIVE, pageable);
    }

    @Test
    @DisplayName("Test Get All Listings - Paginasi Dasar")
    void testGetAllListings() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Listing> page = new PageImpl<>(List.of(listing));

        when(listingRepository.findAll(pageable)).thenReturn(page);

        Page<Listing> result = listingService.getAllListings(pageable);

        assertEquals(1, result.getTotalElements());
        verify(listingRepository).findAll(pageable);
    }
}