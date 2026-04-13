package id.ac.ui.cs.advprog.bidmartcatalog.repository;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

        // Catatan: Jika ada atribut lain di model Category yang wajib diisi (misal deskripsi),
        // tambahkan juga setter-nya di sini. Contoh:
        // category.setDescription("Kategori untuk barang elektronik");
    }

    @Test
    void testSaveCategory() {
        Category savedCategory = categoryRepository.save(category);

        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getId()); // Memastikan ID di-generate oleh database
        assertEquals("Electronics", savedCategory.getName()); // Memastikan data tersimpan dengan benar
    }

    @Test
    void testFindById() {
        Category savedCategory = categoryRepository.save(category);
        UUID id = savedCategory.getId();

        Optional<Category> foundCategory = categoryRepository.findById(id);

        assertTrue(foundCategory.isPresent());
        assertEquals(savedCategory.getId(), foundCategory.get().getId());
        assertEquals(savedCategory.getName(), foundCategory.get().getName());
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

        assertNotNull(categoryList);
        assertTrue(categoryList.size() >= 2);
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
        assertTrue(deletedCategory.isEmpty());
    }
}