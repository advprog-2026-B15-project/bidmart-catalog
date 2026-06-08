package id.ac.ui.cs.advprog.bidmartcatalog.repository;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        // Inisialisasi objek Category baru
        category = new Category();

        // Mengisi atribut wajib (not-null) agar Hibernate tidak protes
        category.setName("Electronics");
    }

    @Test
    void testSaveCategory() {
        Category savedCategory = categoryRepository.save(category);

        assertAll("Saved category properties",
                () -> assertNotNull(savedCategory, "Saved category should not be null"),
                () -> assertNotNull(savedCategory.getId(), "Saved category ID should be generated"),
                () -> assertEquals("Electronics", savedCategory.getName(), "Saved category name should match input")
        );
    }

    @Test
    void testFindById() {
        Category savedCategory = categoryRepository.save(category);
        UUID id = savedCategory.getId();

        Optional<Category> foundCategory = categoryRepository.findById(id);

        assertAll("Found category properties",
                () -> assertTrue(foundCategory.isPresent(), "Category should be found by ID"),
                () -> assertEquals(savedCategory.getId(), foundCategory.get().getId(), "Found ID should match saved ID"),
                () -> assertEquals(savedCategory.getName(), foundCategory.get().getName(), "Found name should match saved name")
        );
    }

    @Test
    void testFindAllCategories() {
        // Simpan kategori pertama (dari setUp)
        categoryRepository.save(category);

        // Buat dan simpan kategori kedua
        Category category2 = new Category();
        category2.setName("Fashion"); // Wajib diisi karena not-null
        categoryRepository.save(category2);

        // Ambil semua kategori dari database in-memory
        List<Category> categoryList = categoryRepository.findAll();

        assertAll("Find all categories properties",
                () -> assertNotNull(categoryList, "Category list should not be null"),
                () -> assertTrue(categoryList.size() >= 2, "Category list should contain at least 2 categories")
        );
    }

    @Test
    void testDeleteCategory() {
        // Simpan data dulu
        Category savedCategory = categoryRepository.save(category);
        UUID id = savedCategory.getId();

        // Hapus data
        categoryRepository.deleteById(id);

        // Coba cari lagi datanya
        Optional<Category> deletedCategory = categoryRepository.findById(id);

        // Pastikan datanya sudah tidak ada
        assertTrue(deletedCategory.isEmpty(), "Deleted category should not be found by ID");
    }
}