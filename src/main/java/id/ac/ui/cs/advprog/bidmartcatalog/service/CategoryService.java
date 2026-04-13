package id.ac.ui.cs.advprog.bidmartcatalog.service;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Category;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Digunakan oleh Controller sebelum memanggil ListingService.searchAndFilterListings
     * Ini mengambil ID kategori yang dicari beserta semua ID sub-kategorinya.
     */
    public List<UUID> getCategoryAndSubCategoryIds(UUID parentCategoryId) {
        List<UUID> ids = new ArrayList<>();
        Category category = categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        collectCategoryIds(category, ids);
        return ids;
    }

    private void collectCategoryIds(Category category, List<UUID> ids) {
        ids.add(category.getId()); // Masukkan ID parent

        // Cek jika ada sub-kategori, rekursif panggil fungsi ini
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            for (Category sub : category.getSubCategories()) {
                collectCategoryIds(sub, ids);
            }
        }
    }
}