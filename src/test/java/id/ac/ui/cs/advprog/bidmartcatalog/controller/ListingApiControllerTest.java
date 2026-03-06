package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListingApiController.class)
class ListingApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListingService listingService;

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
}