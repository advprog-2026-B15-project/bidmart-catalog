package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Category;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryApiController.class)
class CategoryApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Gunakan MockitoBean sebagai pengganti MockBean
    @MockitoBean
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("GET /api/categories - Harus mengembalikan daftar kategori dalam format JSON")
    void testGetAllCategories() throws Exception {
        // 1. Persiapkan data simulasi (Mock Data)
        Category cat1 = Category.builder().name("Sepatu Bola").build();
        Category cat2 = Category.builder().name("Jersey").build();
        List<Category> categories = Arrays.asList(cat1, cat2);

        // 2. Tentukan perilaku Mock
        when(categoryRepository.findAll()).thenReturn(categories);

        // 3. Jalankan request dan verifikasi hasilnya
        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2)) // Jumlah elemen
                .andExpect(jsonPath("$[0].name").value("Sepatu Bola"))
                .andExpect(jsonPath("$[1].name").value("Jersey"));
    }

}