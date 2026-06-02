package id.ac.ui.cs.advprog.bidmartcatalog.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertAll("Category builder properties",
            () -> assertEquals("Smartphones", subCategory.getName(), "Category name should match"),
            () -> assertEquals(category, subCategory.getParentCategory(), "Parent category should match"),
            () -> assertNotNull(subCategory.getId(), "Category ID should not be null")
        );
    }

    @Test
    @DisplayName("Test Getter and Setter for Category Name")
    void testGetAndSetName() {
        String newName = "Home Appliances";
        category.setName(newName);
        assertEquals(newName, category.getName(), "Category name should match updated value");
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

        assertAll("Category hierarchy properties",
            () -> assertNotNull(category.getSubCategories(), "Subcategories list should not be null"),
            () -> assertEquals(1, category.getSubCategories().size(), "Subcategories list size should be 1"),
            () -> assertEquals(category, category.getSubCategories().get(0).getParentCategory(), "Child parent should match")
        );
    }

    @Test
    @DisplayName("Test NoArgsConstructor and AllArgsConstructor")
    void testConstructors() {
        Category emptyCategory = new Category();

        List<Category> subs = new ArrayList<>();
        UUID randomId = UUID.randomUUID();
        Category fullCategory = new Category(randomId, "Books", null, subs);

        assertAll("Constructor properties",
            () -> assertNull(emptyCategory.getName(), "Name should be null for no-args constructor"),
            () -> assertEquals(randomId, fullCategory.getId(), "ID should match"),
            () -> assertEquals("Books", fullCategory.getName(), "Name should match"),
            () -> assertEquals(subs, fullCategory.getSubCategories(), "Subcategories should match")
        );
    }

    @Test
    @DisplayName("Test addSubCategory helper method")
    void testAddSubCategory() {
        Category parent = new Category();
        Category sub = new Category();
        parent.addSubCategory(sub);

        assertAll("addSubCategory properties",
            () -> assertTrue(parent.getSubCategories().contains(sub), "Parent should contain the subcategory"),
            () -> assertEquals(parent, sub.getParentCategory(), "Subcategory parent should match")
        );
    }
}