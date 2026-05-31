package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Category;
import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import id.ac.ui.cs.advprog.bidmartcatalog.service.CategoryService;
import id.ac.ui.cs.advprog.bidmartcatalog.dto.ListingRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @DisplayName("POST /api/listings - Success")
    void testCreateListingSuccess() throws Exception {
        Listing createdListing = new Listing();
        createdListing.setId(UUID.randomUUID());
        createdListing.setTitle("New Listing");
        createdListing.setStatus(ListingStatus.DRAFT);
        createdListing.setSellerId("usr-2406");

        when(listingService.createListing(any(Listing.class), any())).thenReturn(createdListing);
        when(categoryService.getCategoryById(any(UUID.class))).thenReturn(new Category());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/api/listings")
                        .header("X-User-Id", "usr-2406")
                        .header("X-User-Role", "SELLER")
                        .param("title", "New Listing")
                        .param("description", "Desc")
                        .param("startingPrice", "10000.0")
                        .param("categoryId", UUID.randomUUID().toString())
                        .param("endTime", "2024-12-31T23:59:59"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("New Listing"));
    }

    @Test
    @DisplayName("POST /api/listings - Forbidden (Not Seller)")
    void testCreateListingForbidden() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/api/listings")
                        .header("X-User-Id", "usr-2406")
                        .header("X-User-Role", "BUYER"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/publish - Success")
    void testPublishListingSuccess() throws Exception {
        sampleListing.setStatus(ListingStatus.DRAFT);
        sampleListing.setSellerId("usr-2406");
        
        Listing publishedListing = new Listing();
        publishedListing.setId(id);
        publishedListing.setStatus(ListingStatus.ACTIVE);
        publishedListing.setSellerId("usr-2406");

        when(listingService.getListingById(id)).thenReturn(sampleListing);
        when(listingService.publishListing(id)).thenReturn(publishedListing);

        mockMvc.perform(patch("/api/listings/{id}/publish", id)
                        .header("X-User-Id", "usr-2406"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/publish - Forbidden (Not Owner)")
    void testPublishListingForbidden() throws Exception {
        sampleListing.setSellerId("usr-2406");
        when(listingService.getListingById(id)).thenReturn(sampleListing);

        mockMvc.perform(patch("/api/listings/{id}/publish", id)
                        .header("X-User-Id", "usr-9999"))
                .andExpect(status().isForbidden());
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
    @DisplayName("PATCH /api/listings/{id}/current-price - Bad Request")
    void testUpdatePriceBadRequest() throws Exception {
        mockMvc.perform(patch("/api/listings/" + id + "/current-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/listings/{id} - 404")
    void testGetListingByIdNotFound() throws Exception {
        when(listingService.getListingById(id)).thenThrow(new RuntimeException("Not Found"));
        mockMvc.perform(get("/api/listings/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/listings/{id}/validate - Valid")
    void testValidateListingValid() throws Exception {
        sampleListing.setStatus(ListingStatus.ACTIVE);
        sampleListing.setEndTime(LocalDateTime.now().plusHours(1));
        when(listingService.getListingById(id)).thenReturn(sampleListing);

        mockMvc.perform(get("/api/listings/{id}/validate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isValid").value(true));
    }

    @Test
    @DisplayName("GET /api/listings/{id}/validate - Not Active")
    void testValidateListingNotActive() throws Exception {
        sampleListing.setStatus(ListingStatus.DRAFT);
        sampleListing.setEndTime(LocalDateTime.now().plusHours(1));
        when(listingService.getListingById(id)).thenReturn(sampleListing);

        mockMvc.perform(get("/api/listings/{id}/validate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isValid").value(false))
                .andExpect(jsonPath("$.reason").value("Listing is not active"));
    }

    @Test
    @DisplayName("GET /api/listings/{id}/validate - Expired")
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
    @DisplayName("POST /api/listings - RuntimeException in service")
    void testCreateListingRuntimeError() throws Exception {
        when(listingService.createListing(any(), any())).thenThrow(new RuntimeException("Error"));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/api/listings")
                        .header("X-User-Id", "usr-1")
                        .header("X-User-Role", "SELLER")
                        .param("title", "Test"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/listings/{id} - IllegalStateException")
    void testUpdateListingIllegalState() throws Exception {
        sampleListing.setSellerId("usr-1");
        when(listingService.getListingById(id)).thenReturn(sampleListing);
        when(listingService.updateListing(eq(id), any())).thenThrow(new IllegalStateException("Locked"));

        mockMvc.perform(put("/api/listings/{id}", id)
                        .header("X-User-Id", "usr-1")
                        .header("X-User-Role", "SELLER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/listings/{id} - Success")
    void testUpdateListingSuccess() throws Exception {
        sampleListing.setSellerId("usr-2406");
        when(listingService.getListingById(id)).thenReturn(sampleListing);
        when(listingService.updateListing(eq(id), any())).thenReturn(sampleListing);

        mockMvc.perform(put("/api/listings/{id}", id)
                        .header("X-User-Id", "usr-2406")
                        .header("X-User-Role", "SELLER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/listings/{id} - Success")
    void testDeleteListingSuccess() throws Exception {
        sampleListing.setSellerId("usr-2406");
        when(listingService.getListingById(id)).thenReturn(sampleListing);

        mockMvc.perform(delete("/api/listings/{id}", id)
                        .header("X-User-Id", "usr-2406")
                        .header("X-User-Role", "SELLER"))
                .andExpect(status().isOk());
        verify(listingService).deleteListing(id);
    }

    @Test
    @DisplayName("GET /api/listings - with filter")
    void testGetListingsWithFilter() throws Exception {
        when(listingService.searchAndFilterListings(any(), any(), any(), any(), any(), any()))
                .thenReturn(org.springframework.data.domain.Page.empty());
        mockMvc.perform(get("/api/listings"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/current-price - Not Found")
    void testUpdatePriceNotFound() throws Exception {
        when(listingService.updateCurrentPrice(any(), any())).thenThrow(new RuntimeException("Not found"));
        mockMvc.perform(patch("/api/listings/" + id + "/current-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPrice\": 100.0}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/current-price - Illegal Argument")
    void testUpdatePriceIllegalArgument() throws Exception {
        when(listingService.updateCurrentPrice(any(), any())).thenThrow(new IllegalArgumentException("Invalid price"));
        mockMvc.perform(patch("/api/listings/" + id + "/current-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPrice\": -100.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid price"));
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/publish - Not Found")
    void testPublishListingNotFound() throws Exception {
        when(listingService.getListingById(id)).thenThrow(new RuntimeException("Not found"));
        mockMvc.perform(patch("/api/listings/{id}/publish", id)
                        .header("X-User-Id", "usr-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/listings/seller/{sellerId}/stats - Success")
    void testGetSellerStats() throws Exception {
        Map<String, Object> stats = Map.of("ACTIVE", 5);
        when(listingService.getSellerStatistics("usr-1")).thenReturn(stats);

        mockMvc.perform(get("/api/listings/seller/usr-1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ACTIVE").value(5));
    }

    @Test
    @DisplayName("PUT /api/listings/{id} - Not Found")
    void testUpdateListingNotFound() throws Exception {
        when(listingService.getListingById(id)).thenThrow(new RuntimeException("Not found"));
        mockMvc.perform(put("/api/listings/" + id, id)
                        .header("X-User-Id", "usr-1")
                        .header("X-User-Role", "SELLER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/listings/{id} - Not Found")
    void testDeleteListingNotFound() throws Exception {
        when(listingService.getListingById(id)).thenThrow(new RuntimeException("Not found"));
        mockMvc.perform(delete("/api/listings/" + id, id)
                        .header("X-User-Id", "usr-1")
                        .header("X-User-Role", "SELLER"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/listings/{id}/validate - Not Found")
    void testValidateListingNotFound() throws Exception {
        when(listingService.getListingById(id)).thenThrow(new RuntimeException("Not found"));
        mockMvc.perform(get("/api/listings/{id}/validate", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/listings/{id} - Forbidden (Not Owner)")
    void testDeleteListingForbiddenOwner() throws Exception {
        sampleListing.setSellerId("owner");
        when(listingService.getListingById(id)).thenReturn(sampleListing);

        mockMvc.perform(delete("/api/listings/" + id, id)
                        .header("X-User-Id", "not-owner")
                        .header("X-User-Role", "SELLER"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/listings/{id} - Forbidden (Not Seller)")
    void testDeleteListingForbiddenRole() throws Exception {
        mockMvc.perform(delete("/api/listings/" + id, id)
                        .header("X-User-Id", "usr-1")
                        .header("X-User-Role", "BUYER"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/listings - Various Filters")
    void testGetAllListingsWithFilters() throws Exception {
        when(listingService.searchAndFilterListings(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(sampleListing)));

        mockMvc.perform(get("/api/listings")
                        .param("keyword", "test")
                        .param("minPrice", "10.0")
                        .param("maxPrice", "100.0")
                        .param("status", "ACTIVE")
                        .param("sellerId", "usr-1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/listings - Empty Result")
    void testGetAllListingsEmpty() throws Exception {
        when(listingService.searchAndFilterListings(any(), any(), any(), any(), any(), any()))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/listings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}
