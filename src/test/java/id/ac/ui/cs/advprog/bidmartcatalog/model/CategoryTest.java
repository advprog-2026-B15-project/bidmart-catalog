package id.ac.ui.cs.advprog.bidmartcatalog.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private Category category;
    private UUID id;
    private String name;

    @BeforeEach
    void setUp() {
        this.id = UUID.randomUUID();
        this.name = "Electronics";
        this.category = new Category();
        this.category.setId(id);
        this.category.setName(name);
    }

    @Test
    @DisplayName("Test Category Creation with Builder")
    void testCategoryBuilder() {
        Category subCategory = Category.builder()
                .id(UUID.randomUUID())
                .name("Smartphones")
                .parentCategory(category)
                .build();

        assertEquals("Smartphones", subCategory.getName());
        assertEquals(category, subCategory.getParentCategory());
        assertNotNull(subCategory.getId());
    }

    @Test
    @DisplayName("Test Getter and Setter for Category Name")
    void testGetAndSetName() {
        String newName = "Home Appliances";
        category.setName(newName);
        assertEquals(newName, category.getName());
    }

    @Test
    @DisplayName("Test Self-Referencing Hierarchy")
    void testCategoryHierarchy() {
        Category child = new Category();
        child.setName("Laptops");
        child.setParentCategory(category);

        List<Category> subs = new ArrayList<>();
        subs.add(child);
        category.setSubCategories(subs);

        assertNotNull(category.getSubCategories());
        assertEquals(1, category.getSubCategories().size());
        assertEquals(category, category.getSubCategories().get(0).getParentCategory());
    }

    @Test
    @DisplayName("Test NoArgsConstructor and AllArgsConstructor")
    void testConstructors() {
        Category emptyCategory = new Category();
        assertNull(emptyCategory.getName());

        List<Category> subs = new ArrayList<>();
        UUID randomId = UUID.randomUUID();
        Category fullCategory = new Category(randomId, "Books", null, subs);

        assertEquals(randomId, fullCategory.getId());
        assertEquals("Books", fullCategory.getName());
        assertEquals(subs, fullCategory.getSubCategories());
    }
}