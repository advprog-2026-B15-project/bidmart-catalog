package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import id.ac.ui.cs.advprog.bidmartcatalog.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
// Import untuk List dan UUID
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Import untuk Mockito (verify dan times)
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListingApiController.class)
class ListingApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListingService listingService;

    @MockitoBean
    private CategoryService categoryService;

    private Listing sampleListing;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        sampleListing = new Listing();
        sampleListing.setId(id);
        sampleListing.setTitle("Raket Yonex");
        sampleListing.setCurrentPrice(500000.0);
        sampleListing.setStatus(ListingStatus.ACTIVE);
    }

    @Test
    @DisplayName("GET /api/listings/{id} - Success")
    void testGetListingByIdSuccess() throws Exception {
        when(listingService.getListingById(id)).thenReturn(sampleListing);

        mockMvc.perform(get("/api/listings/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Raket Yonex"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/current-price - Success")
    void testUpdatePriceSuccess() throws Exception {
        Double newPrice = 600000.0;
        sampleListing.setCurrentPrice(newPrice);

        when(listingService.updateCurrentPrice(eq(id), eq(newPrice))).thenReturn(sampleListing);

        String jsonPayload = "{\"newPrice\": 600000.0}";

        mockMvc.perform(patch("/api/listings/" + id + "/current-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPrice").value(600000.0));
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/current-price - Bad Request (Missing Price)")
    void testUpdatePriceBadRequest() throws Exception {
        mockMvc.perform(patch("/api/listings/" + id + "/current-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Parameter 'newPrice' wajib diisi"));
    }

    @Test
    @DisplayName("GET /api/listings/{id} - Return 404 when RuntimeException occurs")
    void testGetListingByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        // Simulasi Service melempar RuntimeException (misal: "Listing not found")
        when(listingService.getListingById(id)).thenThrow(new RuntimeException("Not Found"));

        mockMvc.perform(get("/api/listings/" + id))
                .andExpect(status().isNotFound()); // Verifikasi return 404
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/current-price - Return 400 when IllegalArgumentException occurs")
    void testUpdatePriceIllegalArgument() throws Exception {
        UUID id = UUID.randomUUID();
        Double lowerPrice = 50000.0;
        String jsonPayload = "{\"newPrice\": 50000.0}";

        // Simulasi harga baru lebih rendah dari harga saat ini
        when(listingService.updateCurrentPrice(eq(id), eq(lowerPrice)))
                .thenThrow(new IllegalArgumentException("Penawaran baru harus lebih tinggi"));

        mockMvc.perform(patch("/api/listings/" + id + "/current-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest()) // Verifikasi return 400
                .andExpect(content().string("Penawaran baru harus lebih tinggi"));
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/current-price - Return 404 on RuntimeException")
    void testUpdatePriceRuntimeError() throws Exception {
        UUID id = UUID.randomUUID();
        String jsonPayload = "{\"newPrice\": 1000000.0}";

        when(listingService.updateCurrentPrice(any(), any()))
                .thenThrow(new RuntimeException("Database error or not found"));

        mockMvc.perform(patch("/api/listings/" + id + "/current-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isNotFound()); // Verifikasi return 404
    }

    @Test
    @DisplayName("GET /api/listings/{id}/validate - Valid Listing")
    void testValidateListingValid() throws Exception {
        sampleListing.setStatus(ListingStatus.ACTIVE);
        sampleListing.setEndTime(LocalDateTime.now().plusHours(1));

        when(listingService.getListingById(id)).thenReturn(sampleListing);

        mockMvc.perform(get("/api/listings/{id}/validate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isValid").value(true));
    }

    @Test
    @DisplayName("GET /api/listings/{id}/validate - Expired Listing")
    void testValidateListingExpired() throws Exception {
        sampleListing.setStatus(ListingStatus.ACTIVE);
        sampleListing.setEndTime(LocalDateTime.now().minusHours(1));

        when(listingService.getListingById(id)).thenReturn(sampleListing);

        mockMvc.perform(get("/api/listings/{id}/validate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isValid").value(false))
                .andExpect(jsonPath("$.reason").value("Auction time has ended"));
    }

    @Test
    @DisplayName("PUT /api/listings/{id} - Success")
    void testUpdateListingSuccess() throws Exception {
        when(listingService.updateListing(eq(id), any(Listing.class))).thenReturn(sampleListing);

        mockMvc.perform(put("/api/listings/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Title\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("DELETE /api/listings/{id} - Success")
    void testDeleteListingSuccess() throws Exception {
        mockMvc.perform(delete("/api/listings/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Listing berhasil dihapus"));

        verify(listingService, times(1)).deleteListing(id);
    }

    @Test
    @DisplayName("GET /api/listings/seller/{sellerId}/stats - Success")
    void testGetSellerStatsSuccess() throws Exception {
        Map<String, Object> stats = Map.of("totalListings", 5L);
        when(listingService.getSellerStatistics("seller-1")).thenReturn(stats);

        mockMvc.perform(get("/api/listings/seller/{sellerId}/stats", "seller-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalListings").value(5));
    }
}