package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.CategoryRepository;
import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import id.ac.ui.cs.advprog.bidmartcatalog.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Category;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import java.util.Arrays;
import java.util.List;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ListingController.class)
class ListingControllerTest {

    private static final String LISTINGS_PATH = "/listings";
    private static final String ERROR_ATTR = "error";
    private static final String EDIT_PATH = "/edit";
    private static final String REDIRECT_LISTINGS = "redirect:/listings";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListingService listingService;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @DisplayName("POST /listings/create - Coverage Multipart File")
    void testCreateListing() throws Exception {
        MockMultipartFile file = new MockMultipartFile("imageFiles", "img.jpg", "image/jpeg", "data".getBytes());

        ResultActions result = mockMvc.perform(multipart(LISTINGS_PATH + "/create")
                        .file(file)
                        .param("title", "Samsak Tinju"));
        
        assertAll("Verify create listing success",
            () -> result.andExpect(status().is3xxRedirection()),
            () -> result.andExpect(redirectedUrl(LISTINGS_PATH)),
            () -> verify(listingService).createListing(any(), any())
        );
    }

    @Test
    @DisplayName("POST /listings/{id}/publish - Coverage Success")
    void testPublishSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        ResultActions result = mockMvc.perform(post(LISTINGS_PATH + "/" + id + "/publish"));
        
        assertAll("Verify publish success",
            () -> result.andExpect(flash().attribute("message", "Listing published successfully"))
        );
    }

    @Test
    @DisplayName("POST /listings/{id}/publish - Coverage Catch Exception")
    void testPublishFailure() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new RuntimeException()).when(listingService).publishListing(id);

        ResultActions result = mockMvc.perform(post(LISTINGS_PATH + "/" + id + "/publish"));
        
        assertAll("Verify publish failure",
            () -> result.andExpect(flash().attribute(ERROR_ATTR, "Listing cannot be published"))
        );
    }

    @Test
    @DisplayName("GET /listings/{id} - Success detail page")
    void testGetDetail() throws Exception {
        UUID id = UUID.randomUUID();
        Listing listing = new Listing();
        listing.setTitle("Bola Basket");
        when(listingService.getListingById(id)).thenReturn(listing);

        ResultActions result = mockMvc.perform(get(LISTINGS_PATH + "/" + id));
        
        assertAll("Verify get detail success",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(view().name("listing-detail")),
            () -> result.andExpect(model().attribute("listing", listing))
        );
    }

    @Test
    @DisplayName("GET /listings - Memeriksa Paginasi dan Isi Model")
    void testGetListings() throws Exception {
        // 1. Siapkan data mock untuk Page
        id.ac.ui.cs.advprog.bidmartcatalog.dto.ListingResponseDTO.CategoryResponseDTO categoryDTO = 
            id.ac.ui.cs.advprog.bidmartcatalog.dto.ListingResponseDTO.CategoryResponseDTO.builder().name("Sports").build();
            
        id.ac.ui.cs.advprog.bidmartcatalog.dto.ListingResponseDTO dto1 = id.ac.ui.cs.advprog.bidmartcatalog.dto.ListingResponseDTO.builder()
                .title("Sepatu Lari")
                .category(categoryDTO)
                .imageUrls(List.of())
                .status(ListingStatus.ACTIVE)
                .build();
        id.ac.ui.cs.advprog.bidmartcatalog.dto.ListingResponseDTO dto2 = id.ac.ui.cs.advprog.bidmartcatalog.dto.ListingResponseDTO.builder()
                .title("Bola Voli")
                .category(categoryDTO)
                .imageUrls(List.of())
                .status(ListingStatus.ACTIVE)
                .build();
        List<id.ac.ui.cs.advprog.bidmartcatalog.dto.ListingResponseDTO> dtoList = Arrays.asList(dto1, dto2);
        Page<id.ac.ui.cs.advprog.bidmartcatalog.dto.ListingResponseDTO> listingPage = new PageImpl<>(dtoList, PageRequest.of(0, 10), 2);

        // 2. Mock perilaku service
        when(listingService.searchAndFilterListings(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(listingPage);

        // 3. Jalankan request dan verifikasi model attribute
        ResultActions result = mockMvc.perform(get(LISTINGS_PATH)
                        .param("page", "0")
                        .param("size", "10"));
        
        assertAll("Verify get listings success",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(view().name("listings")),
            () -> result.andExpect(model().attribute("listings", dtoList)),
            () -> result.andExpect(model().attribute("currentPage", 0)),
            () -> result.andExpect(model().attribute("totalPages", 1))
        );
    }

    @Test
    @DisplayName("GET /listings/create - Memeriksa Form dan Dropdown Kategori")
    void testCreateListingForm() throws Exception {
        // 1. Mock data kategori untuk dropdown
        Category cat1 = Category.builder().name("Elektronik").build();
        List<Category> categories = List.of(cat1);

        when(categoryRepository.findByParentCategoryIsNull()).thenReturn(categories);

        // 2. Verifikasi form baru dan list kategori dikirim ke view
        ResultActions result = mockMvc.perform(get(LISTINGS_PATH + "/create"));
        
        assertAll("Verify create listing form success",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(view().name("create-listing")),
            () -> result.andExpect(model().attributeExists("listing")),
            () -> result.andExpect(model().attribute("categories", categories))
        );
    }

    @Test
    @DisplayName("POST /listings/{id}/delete - Success")
    void testDeleteListingSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        ResultActions result = mockMvc.perform(post(LISTINGS_PATH + "/" + id + "/delete"));
        
        assertAll("Verify delete listing success",
            () -> result.andExpect(status().is3xxRedirection()),
            () -> result.andExpect(redirectedUrl(LISTINGS_PATH)),
            () -> verify(listingService).deleteListing(id)
        );
    }

    @Test
    @DisplayName("POST /listings/{id}/delete - Failure")
    void testDeleteListingFailure() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new IllegalStateException("Failure")).when(listingService).deleteListing(id);
        ResultActions result = mockMvc.perform(post(LISTINGS_PATH + "/" + id + "/delete"));
        
        assertAll("Verify delete listing failure",
            () -> result.andExpect(status().is3xxRedirection()),
            () -> result.andExpect(flash().attribute(ERROR_ATTR, "Failure"))
        );
    }

    @Test
    @DisplayName("GET /listings/{id}/edit - Success")
    void testEditListingForm() throws Exception {
        UUID id = UUID.randomUUID();
        Listing listing = new Listing();
        listing.setStatus(ListingStatus.DRAFT);
        when(listingService.getListingById(id)).thenReturn(listing);
        when(categoryRepository.findByParentCategoryIsNull()).thenReturn(List.of());

        ResultActions result = mockMvc.perform(get(LISTINGS_PATH + "/" + id + EDIT_PATH));
        
        assertAll("Verify edit listing form success",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(view().name("edit-listing")),
            () -> result.andExpect(model().attribute("listing", listing))
        );
    }

    @Test
    @DisplayName("GET /listings/{id}/edit - Forbidden (Has Bids)")
    void testEditListingFormForbidden() throws Exception {
        UUID id = UUID.randomUUID();
        Listing listing = new Listing();
        listing.setBidCount(1);
        when(listingService.getListingById(id)).thenReturn(listing);

        ResultActions result = mockMvc.perform(get(LISTINGS_PATH + "/" + id + EDIT_PATH));
        
        assertAll("Verify edit listing form forbidden",
            () -> result.andExpect(status().is3xxRedirection()),
            () -> result.andExpect(redirectedUrl(LISTINGS_PATH + "/" + id)),
            () -> result.andExpect(flash().attribute(ERROR_ATTR, "Tidak bisa mengedit: Sudah ada penawaran!"))
        );
    }

    @Test
    @DisplayName("GET /listings/{id}/edit - Exception")
    void testEditListingFormException() throws Exception {
        UUID id = UUID.randomUUID();
        when(listingService.getListingById(id)).thenThrow(new RuntimeException("Error"));

        ResultActions result = mockMvc.perform(get(LISTINGS_PATH + "/" + id + EDIT_PATH));
        
        assertAll("Verify edit listing form exception",
            () -> result.andExpect(status().is3xxRedirection()),
            () -> result.andExpect(redirectedUrl(LISTINGS_PATH))
        );
    }

    @Test
    @DisplayName("POST /listings/{id}/edit - Success")
    void testUpdateListingSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        ResultActions result = mockMvc.perform(post(LISTINGS_PATH + "/" + id + EDIT_PATH)
                        .param("title", "Updated"));
        
        assertAll("Verify update listing success",
            () -> result.andExpect(status().is3xxRedirection()),
            () -> result.andExpect(redirectedUrl(LISTINGS_PATH + "/" + id)),
            () -> verify(listingService).updateListing(eq(id), any())
        );
    }

    @Test
    @DisplayName("POST /listings/{id}/edit - Failure")
    void testUpdateListingFailure() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new IllegalStateException("Err")).when(listingService).updateListing(eq(id), any());
        ResultActions result = mockMvc.perform(post(LISTINGS_PATH + "/" + id + EDIT_PATH)
                        .param("title", "Updated"));
        
        assertAll("Verify update listing failure",
            () -> result.andExpect(status().is3xxRedirection()),
            () -> result.andExpect(flash().attribute(ERROR_ATTR, "Err"))
        );
    }
}
