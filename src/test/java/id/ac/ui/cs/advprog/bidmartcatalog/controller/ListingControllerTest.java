package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Category;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.CategoryRepository;
import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListingController.class)
class ListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListingService listingService;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("GET /listings - Should return listings view")
    void testGetListings() throws Exception {
        when(listingService.getAllListings(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/listings"))
                .andExpect(status().isOk())
                .andExpect(view().name("listings"))
                .andExpect(model().attributeExists("listings", "currentPage", "totalPages"));
    }

    @Test
    @DisplayName("GET /listings/create - Should return create form with categories")
    void testCreateListingForm() throws Exception {
        List<Category> categories = Collections.singletonList(new Category());
        when(categoryRepository.findAll()).thenReturn(categories);

        mockMvc.perform(get("/listings/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-listing"))
                .andExpect(model().attributeExists("listing", "categories"));
    }

    @Test
    @DisplayName("POST /listings/{id}/publish - Success")
    void testPublishListingSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/listings/" + id + "/publish"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listings"))
                .andExpect(flash().attribute("message", "Listing published successfully"));
    }
}