package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.CategoryRepository;
import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Category;
import java.util.Arrays;
import java.util.List;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @DisplayName("POST /listings/create - Coverage Multipart File")
    void testCreateListing() throws Exception {
        MockMultipartFile file = new MockMultipartFile("imageFiles", "img.jpg", "image/jpeg", "data".getBytes());

        mockMvc.perform(multipart("/listings/create")
                        .file(file)
                        .param("title", "Samsak Tinju"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listings"));

        verify(listingService).createListing(any(), any());
    }

    @Test
    @DisplayName("POST /listings/{id}/publish - Coverage Success")
    void testPublishSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(post("/listings/" + id + "/publish"))
                .andExpect(flash().attribute("message", "Listing published successfully"));
    }

    @Test
    @DisplayName("POST /listings/{id}/publish - Coverage Catch Exception")
    void testPublishFailure() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new RuntimeException()).when(listingService).publishListing(id);

        mockMvc.perform(post("/listings/" + id + "/publish"))
                .andExpect(flash().attribute("error", "Listing cannot be published"));
    }

    @Test
    @DisplayName("GET /listings/{id} - Success detail page")
    void testGetDetail() throws Exception {
        UUID id = UUID.randomUUID();
        Listing listing = new Listing();
        listing.setTitle("Bola Basket");
        when(listingService.getListingById(id)).thenReturn(listing);

        mockMvc.perform(get("/listings/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("listing-detail"))
                .andExpect(model().attribute("listing", listing));
    }

    @Test
    @DisplayName("GET /listings - Memeriksa Paginasi dan Isi Model")
    void testGetListings() throws Exception {
        // 1. Siapkan data mock untuk Page
        Listing listing1 = Listing.builder().title("Sepatu Lari").build();
        Listing listing2 = Listing.builder().title("Bola Voli").build();
        List<Listing> listingList = Arrays.asList(listing1, listing2);
        Page<Listing> listingPage = new PageImpl<>(listingList, PageRequest.of(0, 10), 1);

        // 2. Mock perilaku service
        when(listingService.getAllListings(any(PageRequest.class))).thenReturn(listingPage);

        // 3. Jalankan request dan verifikasi model attribute
        mockMvc.perform(get("/listings")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("listings"))
                .andExpect(model().attribute("listings", listingList))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 1));
    }

    @Test
    @DisplayName("GET /listings/create - Memeriksa Form dan Dropdown Kategori")
    void testCreateListingForm() throws Exception {
        // 1. Mock data kategori untuk dropdown
        Category cat1 = Category.builder().name("Elektronik").build();
        List<Category> categories = List.of(cat1);

        when(categoryRepository.findAll()).thenReturn(categories);

        // 2. Verifikasi form baru dan list kategori dikirim ke view
        mockMvc.perform(get("/listings/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-listing"))
                .andExpect(model().attributeExists("listing"))
                .andExpect(model().attribute("categories", categories));
    }
}

