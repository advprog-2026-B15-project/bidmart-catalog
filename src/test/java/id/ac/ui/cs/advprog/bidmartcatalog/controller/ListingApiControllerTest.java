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
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingImage;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ListingApiController.class)
class ListingApiControllerTest {

    private static final String ACTIVE = "ACTIVE";
    private static final String USR_2406 = "usr-2406";
    private static final String SELLER = "SELLER";
    private static final String API_LISTINGS = "/api/listings";
    private static final String X_USER_ID = "X-User-Id";
    private static final String X_USER_ROLE = "X-User-Role";
    private static final String CURRENT_PRICE_PATH = "/current-price";
    private static final String VALIDATE_PATH = "/{id}/validate";
    private static final String USR_1 = "usr-1";
    private static final String NOT_FOUND_MSG = "Not found";
    private static final String MOCK_MVC_MSG = "MockMvc should be injected";
    private static final String NEW_LISTING = "New Listing";
    private static final String TEST_TITLE = "Test";
    private static final String BUYER_ROLE = "BUYER";

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

        ResultActions result = mockMvc.perform(get(API_LISTINGS + "/" + id));
        
        assertAll("Verify get listing by ID success",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(jsonPath("$.title").value("Raket Yonex")),
            () -> result.andExpect(jsonPath("$.status").value(ACTIVE))
        );
    }

    @Test
    @DisplayName("POST /api/listings - Success")
    void testCreateListingSuccess() throws Exception {
        Listing createdListing = new Listing();
        createdListing.setId(UUID.randomUUID());
        createdListing.setTitle(NEW_LISTING);
        createdListing.setStatus(ListingStatus.DRAFT);
        createdListing.setSellerId(USR_2406);

        when(listingService.createListing(any(Listing.class), any())).thenReturn(createdListing);
        when(categoryService.getCategoryById(any(UUID.class))).thenReturn(new Category());

        ResultActions result = mockMvc.perform(multipart(API_LISTINGS)
                        .header(X_USER_ID, USR_2406)
                        .header(X_USER_ROLE, SELLER)
                        .param("title", NEW_LISTING)
                        .param("description", "Desc")
                        .param("startingPrice", "10000.0")
                        .param("categoryId", UUID.randomUUID().toString())
                        .param("endTime", "2024-12-31T23:59:59"));
        
        assertAll("Verify create listing success",
            () -> result.andExpect(status().isCreated()),
            () -> result.andExpect(jsonPath("$.id").exists()),
            () -> result.andExpect(jsonPath("$.title").value(NEW_LISTING))
        );
    }

    @Test
    @DisplayName("POST /api/listings - Forbidden (Not Seller)")
    void testCreateListingForbidden() throws Exception {
        ResultActions result = mockMvc.perform(multipart(API_LISTINGS)
                        .header(X_USER_ID, USR_2406)
                        .header(X_USER_ROLE, BUYER_ROLE));
        
        assertAll("Verify create listing forbidden",
            () -> result.andExpect(status().isForbidden())
        );
    }

    @Test
    @DisplayName("GET /api/listings/{id} - Success with Category and Images")
    void testGetListingByIdWithDetails() throws Exception {
        Category cat = new Category();
        cat.setId(UUID.randomUUID());
        cat.setName("Electronics");
        sampleListing.setCategory(cat);
        
        ListingImage img = new ListingImage();
        img.setImageUrl("http://example.com/img.jpg");
        sampleListing.getImages().add(img);

        when(listingService.getListingById(id)).thenReturn(sampleListing);

        ResultActions result = mockMvc.perform(get(API_LISTINGS + "/" + id));
        
        assertAll("Verify get listing with details success",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(jsonPath("$.categoryName").value("Electronics")),
            () -> result.andExpect(jsonPath("$.imageUrls[0]").value("http://example.com/img.jpg"))
        );
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/publish - Success (Verified)")
    void testPublishListingSuccessVerified() throws Exception {
        sampleListing.setStatus(ListingStatus.DRAFT);
        sampleListing.setSellerId(USR_2406);
        
        Listing publishedListing = new Listing();
        publishedListing.setId(id);
        publishedListing.setStatus(ListingStatus.ACTIVE);
        publishedListing.setSellerId(USR_2406);

        when(listingService.getListingById(id)).thenReturn(sampleListing);
        when(listingService.publishListing(id)).thenReturn(publishedListing);

        ResultActions result = mockMvc.perform(patch(API_LISTINGS + "/{id}/publish", id)
                        .header(X_USER_ID, USR_2406));
        
        assertAll("Verify publish listing success",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(jsonPath("$.status").value(ACTIVE))
        );
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/publish - Forbidden (Not Owner)")
    void testPublishListingForbidden() throws Exception {
        sampleListing.setSellerId(USR_2406);
        when(listingService.getListingById(id)).thenReturn(sampleListing);

        ResultActions result = mockMvc.perform(patch(API_LISTINGS + "/{id}/publish", id)
                        .header(X_USER_ID, "usr-9999"));
        
        assertAll("Verify publish listing forbidden",
            () -> result.andExpect(status().isForbidden())
        );
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/current-price - Success")
    void testUpdatePriceSuccess() throws Exception {
        Double newPrice = 600000.0;
        sampleListing.setCurrentPrice(newPrice);
        when(listingService.updateCurrentPrice(eq(id), eq(newPrice))).thenReturn(sampleListing);

        String jsonPayload = "{\"newPrice\": 600000.0}";

        ResultActions result = mockMvc.perform(patch(API_LISTINGS + "/" + id + CURRENT_PRICE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload));
        
        assertAll("Verify update price success",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(jsonPath("$.currentPrice").value(600000.0))
        );
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/current-price - Bad Request")
    void testUpdatePriceBadRequest() throws Exception {
        ResultActions result = mockMvc.perform(patch(API_LISTINGS + "/" + id + CURRENT_PRICE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"));
        
        assertAll("Verify update price bad request",
            () -> result.andExpect(status().isBadRequest())
        );
    }

    @Test
    @DisplayName("GET /api/listings/{id} - 404")
    void testGetListingByIdNotFound() throws Exception {
        when(listingService.getListingById(id)).thenThrow(new RuntimeException("Not Found"));
        ResultActions result = mockMvc.perform(get(API_LISTINGS + "/" + id));
        
        assertAll("Verify get listing by ID not found",
            () -> result.andExpect(status().isNotFound())
        );
    }

    @Test
    @DisplayName("GET /api/listings/{id}/validate - Valid")
    void testValidateListingValid() throws Exception {
        sampleListing.setStatus(ListingStatus.ACTIVE);
        sampleListing.setEndTime(LocalDateTime.now().plusHours(1));
        when(listingService.getListingById(id)).thenReturn(sampleListing);

        ResultActions result = mockMvc.perform(get(API_LISTINGS + VALIDATE_PATH, id));
        
        assertAll("Verify validate listing valid",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(jsonPath("$.isValid").value(true))
        );
    }

    @Test
    @DisplayName("GET /api/listings/{id}/validate - Not Active")
    void testValidateListingNotActive() throws Exception {
        sampleListing.setStatus(ListingStatus.DRAFT);
        sampleListing.setEndTime(LocalDateTime.now().plusHours(1));
        when(listingService.getListingById(id)).thenReturn(sampleListing);

        ResultActions result = mockMvc.perform(get(API_LISTINGS + VALIDATE_PATH, id));
        
        assertAll("Verify validate listing not active",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(jsonPath("$.isValid").value(false)),
            () -> result.andExpect(jsonPath("$.reason").value("Listing is not active"))
        );
    }

    @Test
    @DisplayName("GET /api/listings/{id}/validate - Expired")
    void testValidateListingExpired() throws Exception {
        sampleListing.setStatus(ListingStatus.ACTIVE);
        sampleListing.setEndTime(LocalDateTime.now().minusHours(1));
        when(listingService.getListingById(id)).thenReturn(sampleListing);

        ResultActions result = mockMvc.perform(get(API_LISTINGS + VALIDATE_PATH, id));
        
        assertAll("Verify validate listing expired",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(jsonPath("$.isValid").value(false)),
            () -> result.andExpect(jsonPath("$.reason").value("Auction time has ended"))
        );
    }

    @Test
    @DisplayName("POST /api/listings - RuntimeException in service")
    void testCreateListingRuntimeError() throws Exception {
        when(listingService.createListing(any(), any())).thenThrow(new RuntimeException("Error"));
        ResultActions result = mockMvc.perform(multipart(API_LISTINGS)
                        .header(X_USER_ID, USR_1)
                        .header(X_USER_ROLE, SELLER)
                        .param("title", TEST_TITLE));
        
        assertAll("Verify create listing runtime error",
            () -> result.andExpect(status().isBadRequest())
        );
    }

    @Test
    @DisplayName("PUT /api/listings/{id} - IllegalStateException")
    void testUpdateListingIllegalState() throws Exception {
        sampleListing.setSellerId(USR_1);
        when(listingService.getListingById(id)).thenReturn(sampleListing);
        when(listingService.updateListing(eq(id), any())).thenThrow(new IllegalStateException("Locked"));

        ResultActions result = mockMvc.perform(put(API_LISTINGS + "/{id}", id)
                        .header(X_USER_ID, USR_1)
                        .header(X_USER_ROLE, SELLER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + TEST_TITLE + "\"}"));
        
        assertAll("Verify update listing illegal state",
            () -> result.andExpect(status().isForbidden())
        );
    }

    @Test
    @DisplayName("PUT /api/listings/{id} - Success")
    void testUpdateListingSuccess() throws Exception {
        sampleListing.setSellerId(USR_2406);
        when(listingService.getListingById(id)).thenReturn(sampleListing);
        when(listingService.updateListing(eq(id), any())).thenReturn(sampleListing);

        ResultActions result = mockMvc.perform(put(API_LISTINGS + "/{id}", id)
                        .header(X_USER_ID, USR_2406)
                        .header(X_USER_ROLE, SELLER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated\"}"));
        
        assertAll("Verify update listing success",
            () -> result.andExpect(status().isOk())
        );
    }

    @Test
    @DisplayName("DELETE /api/listings/{id} - Success")
    void testDeleteListingSuccess() throws Exception {
        sampleListing.setSellerId(USR_2406);
        when(listingService.getListingById(id)).thenReturn(sampleListing);

        ResultActions result = mockMvc.perform(delete(API_LISTINGS + "/{id}", id)
                        .header(X_USER_ID, USR_2406)
                        .header(X_USER_ROLE, SELLER));
        
        assertAll("Verify delete listing success",
            () -> result.andExpect(status().isOk()),
            () -> verify(listingService).deleteListing(id)
        );
    }

    @Test
    @DisplayName("GET /api/listings - with filter")
    void testGetListingsWithFilter() throws Exception {
        when(listingService.searchAndFilterListings(any(), any(), any(), any(), any(), any()))
                .thenReturn(org.springframework.data.domain.Page.empty());
        ResultActions result = mockMvc.perform(get(API_LISTINGS));
        
        assertAll("Verify get listings with filter",
            () -> result.andExpect(status().isOk())
        );
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/current-price - Not Found")
    void testUpdatePriceNotFound() throws Exception {
        when(listingService.updateCurrentPrice(any(), any())).thenThrow(new RuntimeException(NOT_FOUND_MSG));
        ResultActions result = mockMvc.perform(patch(API_LISTINGS + "/" + id + CURRENT_PRICE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPrice\": 100.0}"));
        
        assertAll("Verify update price not found",
            () -> result.andExpect(status().isNotFound())
        );
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/current-price - Illegal Argument")
    void testUpdatePriceIllegalArgument() throws Exception {
        when(listingService.updateCurrentPrice(any(), any())).thenThrow(new IllegalArgumentException("Invalid price"));
        ResultActions result = mockMvc.perform(patch(API_LISTINGS + "/" + id + CURRENT_PRICE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPrice\": -100.0}"));
        
        assertAll("Verify update price illegal argument",
            () -> result.andExpect(status().isBadRequest()),
            () -> result.andExpect(content().string("Invalid price"))
        );
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/publish - Not Found")
    void testPublishListingNotFound() throws Exception {
        when(listingService.getListingById(id)).thenThrow(new RuntimeException(NOT_FOUND_MSG));
        ResultActions result = mockMvc.perform(patch(API_LISTINGS + "/{id}/publish", id)
                        .header(X_USER_ID, USR_1));
        
        assertAll("Verify publish listing not found",
            () -> result.andExpect(status().isNotFound())
        );
    }

    @Test
    @DisplayName("GET /api/listings/seller/{sellerId}/stats - Success")
    void testGetSellerStats() throws Exception {
        Map<String, Object> stats = Map.of(ACTIVE, 5);
        when(listingService.getSellerStatistics(USR_1)).thenReturn(stats);

        ResultActions result = mockMvc.perform(get(API_LISTINGS + "/seller/" + USR_1 + "/stats"));
        
        assertAll("Verify get seller stats success",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(jsonPath("$.ACTIVE").value(5))
        );
    }

    @Test
    @DisplayName("PUT /api/listings/{id} - Not Found")
    void testUpdateListingNotFound() throws Exception {
        when(listingService.getListingById(id)).thenThrow(new RuntimeException(NOT_FOUND_MSG));
        ResultActions result = mockMvc.perform(put(API_LISTINGS + "/" + id, id)
                        .header(X_USER_ID, USR_1)
                        .header(X_USER_ROLE, SELLER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + TEST_TITLE + "\"}"));
        
        assertAll("Verify update listing not found",
            () -> result.andExpect(status().isNotFound())
        );
    }

    @Test
    @DisplayName("DELETE /api/listings/{id} - Not Found")
    void testDeleteListingNotFound() throws Exception {
        when(listingService.getListingById(id)).thenThrow(new RuntimeException(NOT_FOUND_MSG));
        ResultActions result = mockMvc.perform(delete(API_LISTINGS + "/" + id, id)
                        .header(X_USER_ID, USR_1)
                        .header(X_USER_ROLE, SELLER));
        
        assertAll("Verify delete listing not found",
            () -> result.andExpect(status().isNotFound())
        );
    }

    @Test
    @DisplayName("GET /api/listings/{id}/validate - Not Found")
    void testValidateListingNotFound() throws Exception {
        when(listingService.getListingById(id)).thenThrow(new RuntimeException(NOT_FOUND_MSG));
        ResultActions result = mockMvc.perform(get(API_LISTINGS + VALIDATE_PATH, id));
        
        assertAll("Verify validate listing not found",
            () -> result.andExpect(status().isNotFound())
        );
    }

    @Test
    @DisplayName("DELETE /api/listings/{id} - Forbidden (Not Owner)")
    void testDeleteListingForbiddenOwner() throws Exception {
        sampleListing.setSellerId("owner");
        when(listingService.getListingById(id)).thenReturn(sampleListing);

        ResultActions result = mockMvc.perform(delete(API_LISTINGS + "/" + id, id)
                        .header(X_USER_ID, "not-owner")
                        .header(X_USER_ROLE, SELLER));
        
        assertAll("Verify delete listing forbidden owner",
            () -> result.andExpect(status().isForbidden())
        );
    }

    @Test
    @DisplayName("DELETE /api/listings/{id} - Forbidden (Not Seller)")
    void testDeleteListingForbiddenRole() throws Exception {
        ResultActions result = mockMvc.perform(delete(API_LISTINGS + "/" + id, id)
                        .header(X_USER_ID, USR_1)
                        .header(X_USER_ROLE, BUYER_ROLE));
        
        assertAll("Verify delete listing forbidden role",
            () -> result.andExpect(status().isForbidden())
        );
    }

    @Test
    @DisplayName("GET /api/listings - Forbidden (Non-SELLER accessing non-ACTIVE)")
    void testGetAllListingsForbidden() throws Exception {
        ResultActions result = mockMvc.perform(get(API_LISTINGS)
                        .param("status", "DRAFT")
                        .header(X_USER_ROLE, BUYER_ROLE));
        
        assertAll("Verify get all listings forbidden",
            () -> result.andExpect(status().isForbidden())
        );
    }

    @Test
    @DisplayName("GET /api/listings - Various Filters (Keywords)")
    void testGetAllListingsWithFiltersKeywords() throws Exception {
        when(listingService.searchAndFilterListings(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(sampleListing)));

        ResultActions result = mockMvc.perform(get(API_LISTINGS)
                        .param("keyword", "test")
                        .param("minPrice", "10.0")
                        .param("maxPrice", "100.0")
                        .param("status", ACTIVE)
                        .param("sellerId", USR_1));
        
        assertAll("Verify get all listings with filters",
            () -> result.andExpect(status().isOk())
        );
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/current-price - Not Found (Trigger Catch)")
    void testUpdatePriceNotFoundCatch() throws Exception {
        when(listingService.updateCurrentPrice(any(), any())).thenThrow(new RuntimeException("Not Found"));
        ResultActions result = mockMvc.perform(patch(API_LISTINGS + "/" + id + CURRENT_PRICE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPrice\": 100.0}"));
        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/publish - Forbidden (Catch branch)")
    void testPublishListingForbiddenBranch() throws Exception {
        sampleListing.setSellerId("other");
        when(listingService.getListingById(id)).thenReturn(sampleListing);
        ResultActions result = mockMvc.perform(patch(API_LISTINGS + "/" + id + "/publish")
                        .header(X_USER_ID, "me"));
        result.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/listings/{id}/validate - Logic Branches")
    void testValidateListingLogic() throws Exception {
        // Case: Expired
        sampleListing.setStatus(ListingStatus.ACTIVE);
        sampleListing.setEndTime(LocalDateTime.now().minusDays(1));
        when(listingService.getListingById(id)).thenReturn(sampleListing);
        mockMvc.perform(get(API_LISTINGS + VALIDATE_PATH, id))
                .andExpect(jsonPath("$.isValid").value(false))
                .andExpect(jsonPath("$.reason").value("Auction time has ended"));

        // Case: Inactive
        sampleListing.setStatus(ListingStatus.DRAFT);
        sampleListing.setEndTime(LocalDateTime.now().plusDays(1));
        mockMvc.perform(get(API_LISTINGS + VALIDATE_PATH, id))
                .andExpect(jsonPath("$.isValid").value(false))
                .andExpect(jsonPath("$.reason").value("Listing is not active"));
    }

    @Test
    @DisplayName("DELETE /api/listings/{id} - IllegalStateException (Catch)")
    void testDeleteListingIllegalStateCatch() throws Exception {
        sampleListing.setSellerId(USR_2406);
        when(listingService.getListingById(id)).thenReturn(sampleListing);
        doThrow(new IllegalStateException("Cannot delete")).when(listingService).deleteListing(id);

        mockMvc.perform(delete(API_LISTINGS + "/" + id)
                        .header(X_USER_ID, USR_2406)
                        .header(X_USER_ROLE, SELLER))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Cannot delete"));
    }

    @Test
    @DisplayName("PUT /api/listings/{id} - Logic and Map branch")
    void testUpdateListingLogic() throws Exception {
        sampleListing.setSellerId(USR_2406);
        when(listingService.getListingById(id)).thenReturn(sampleListing);
        
        ListingRequestDTO dto = new ListingRequestDTO();
        dto.setTitle("New Title");
        dto.setCategoryId(UUID.randomUUID());
        
        when(categoryService.getCategoryById(any())).thenReturn(new Category());
        when(listingService.updateListing(eq(id), any())).thenReturn(sampleListing);

        mockMvc.perform(put(API_LISTINGS + "/" + id)
                        .header(X_USER_ID, USR_2406)
                        .header(X_USER_ROLE, SELLER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Title\", \"categoryId\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/listings - Category Tree branch")
    void testGetAllListingsCategoryTree() throws Exception {
        UUID catId = UUID.randomUUID();
        when(categoryService.getCategoryAndSubCategoryIds(catId)).thenReturn(List.of(catId));
        when(listingService.searchAndFilterListings(any(), any(), any(), any(), any(), any()))
                .thenReturn(Page.empty());

        mockMvc.perform(get(API_LISTINGS).param("categoryId", catId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/publish - RuntimeException (404 branch)")
    void testPublishListingNotFoundBranch() throws Exception {
        sampleListing.setSellerId(USR_2406);
        when(listingService.getListingById(id)).thenReturn(sampleListing);
        when(listingService.publishListing(id)).thenThrow(new RuntimeException("not found error"));

        mockMvc.perform(patch(API_LISTINGS + "/{id}/publish", id)
                        .header(X_USER_ID, USR_2406))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/listings/{id}/publish - RuntimeException (500 branch)")
    void testPublishListingInternalErrorBranch() throws Exception {
        sampleListing.setSellerId(USR_2406);
        when(listingService.getListingById(id)).thenReturn(sampleListing);
        when(listingService.publishListing(id)).thenThrow(new RuntimeException("generic server error"));

        mockMvc.perform(patch(API_LISTINGS + "/{id}/publish", id)
                        .header(X_USER_ID, USR_2406))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("PUT /api/listings/{id} - RuntimeException (500 branch)")
    void testUpdateListingInternalErrorBranch() throws Exception {
        sampleListing.setSellerId(USR_2406);
        when(listingService.getListingById(id)).thenReturn(sampleListing);
        when(listingService.updateListing(eq(id), any())).thenThrow(new RuntimeException("generic error"));

        mockMvc.perform(put(API_LISTINGS + "/{id}", id)
                        .header(X_USER_ID, USR_2406)
                        .header(X_USER_ROLE, SELLER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Err\"}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("DELETE /api/listings/{id} - RuntimeException (500 branch)")
    void testDeleteListingInternalErrorBranch() throws Exception {
        sampleListing.setSellerId(USR_2406);
        when(listingService.getListingById(id)).thenReturn(sampleListing);
        doThrow(new RuntimeException("generic error")).when(listingService).deleteListing(id);

        mockMvc.perform(delete(API_LISTINGS + "/" + id)
                        .header(X_USER_ID, USR_2406)
                        .header(X_USER_ROLE, SELLER))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("GET /api/listings/{id}/validate - RuntimeException (500 branch)")
    void testValidateListingInternalErrorBranch() throws Exception {
        when(listingService.getListingById(id)).thenThrow(new RuntimeException("generic error"));

        mockMvc.perform(get(API_LISTINGS + VALIDATE_PATH, id))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/listings - Null CategoryID branch")
    void testCreateListingNullCategoryId() throws Exception {
        Listing created = new Listing();
        created.setId(UUID.randomUUID());
        created.setTitle("Null Cat");
        when(listingService.createListing(any(), any())).thenReturn(created);

        mockMvc.perform(multipart(API_LISTINGS)
                        .header(X_USER_ID, USR_2406)
                        .header(X_USER_ROLE, SELLER)
                        .param("title", "Null Cat"))
                .andExpect(status().isCreated());
    }
}
