package id.ac.ui.cs.advprog.bidmartcatalog.repository;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Test Save and Find Category Hierarchy")
    void testSaveAndFindHierarchy() {
        // Membuat kategori induk (Root)
        Category parent = Category.builder()
                .name("Sport Equipment")
                .build();
        Category savedParent = categoryRepository.save(parent);

        // Membuat kategori anak (Child)
        Category child = Category.builder()
                .name("Badminton")
                .parentCategory(savedParent)
                .build();
        categoryRepository.save(child);

        // Verifikasi pengambilan data
        Optional<Category> foundChild = categoryRepository.findAll().stream()
                .filter(c -> c.getName().equals("Badminton"))
                .findFirst();

        assertTrue(foundChild.isPresent());
        assertEquals("Sport Equipment", foundChild.get().getParentCategory().getName());
    }

    @Test
    @DisplayName("Test Delete Parent Cascades to Children")
    void testCascadeDelete() {
        Category parent = Category.builder().name("Outdoor").build();
        Category savedParent = categoryRepository.save(parent);

        Category child = Category.builder()
                .name("Hiking")
                .parentCategory(savedParent)
                .build();
        categoryRepository.save(child);

        // Hapus induk
        categoryRepository.delete(savedParent);
        categoryRepository.flush();

        List<Category> allCategories = categoryRepository.findAll();
        assertTrue(allCategories.isEmpty(), "Anak kategori seharusnya ikut terhapus karena CascadeType.ALL");
    }
}