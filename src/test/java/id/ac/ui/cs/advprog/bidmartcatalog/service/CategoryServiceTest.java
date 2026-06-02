package id.ac.ui.cs.advprog.bidmartcatalog.service;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Category;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category sampleCategory;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        sampleCategory = Category.builder()
                .id(categoryId)
                .name("Elektronik")
                .build();
    }

    @Test
    @DisplayName("getCategoryAndSubCategoryIds - Sukses Tanpa Sub")
    void testGetCategoryIdsNoSub() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategory));
        List<UUID> result = categoryService.getCategoryAndSubCategoryIds(categoryId);
        
        assertAll("Verify category IDs without subcategories",
            () -> assertThat(result).as("Result list size should be 1").hasSize(1),
            () -> assertThat(result.get(0)).as("Result list should contain category ID").isEqualTo(categoryId)
        );
    }

    @Test
    @DisplayName("getCategoryAndSubCategoryIds - Sukses Dengan Sub (Rekursif)")
    void testGetCategoryIdsWithSub() {
        UUID subId = UUID.randomUUID();
        Category subCategory = Category.builder().id(subId).name("Laptop").build();
        sampleCategory.setSubCategories(List.of(subCategory));

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategory));
        List<UUID> result = categoryService.getCategoryAndSubCategoryIds(categoryId);

        assertAll("Verify category IDs with subcategories",
            () -> assertThat(result).as("Result list size should be 2").hasSize(2),
            () -> assertThat(result).as("Result list should contain both IDs").contains(categoryId, subId)
        );
    }

    @Test
    @DisplayName("getCategoryAndSubCategoryIds - Gagal (Not Found)")
    void testGetCategoryIdsNotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> categoryService.getCategoryAndSubCategoryIds(categoryId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category not found");
    }
}
