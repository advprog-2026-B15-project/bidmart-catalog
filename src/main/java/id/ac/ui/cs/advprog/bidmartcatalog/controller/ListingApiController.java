package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Map;
import java.util.UUID;
import id.ac.ui.cs.advprog.bidmartcatalog.service.CategoryService;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/listings") // Prefix /api untuk membedakan dengan rute HTML
public class ListingApiController {

    private final ListingService listingService;
    private final CategoryService categoryService;

    public ListingApiController(ListingService listingService, CategoryService categoryService) {
        this.listingService = listingService;
        this.categoryService = categoryService;
    }

    /**
     * Endpoint 1: Digunakan Modul Lelang untuk mengecek apakah barang valid dan ACTIVE.
     * Method: GET /api/listings/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Listing> getListingById(@PathVariable UUID id) {
        try {
            Listing listing = listingService.getListingById(id);
            return ResponseEntity.ok(listing); // Mengembalikan JSON data listing
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Mengembalikan 404 jika tidak ketemu
        }
    }

    /**
     * Endpoint 2: Digunakan Modul Lelang untuk mengupdate harga saat ada bid baru.
     * Method: PATCH /api/listings/{id}/current-price
     * Body JSON: { "newPrice": 5000000.0 }
     */
    @PatchMapping("/{id}/current-price")
    public ResponseEntity<?> updateListingPrice(@PathVariable UUID id, @RequestBody Map<String, Double> payload) {
        try {
            Double newPrice = payload.get("newPrice");
            if (newPrice == null) {
                return ResponseEntity.badRequest().body("Parameter 'newPrice' wajib diisi");
            }

            Listing updatedListing = listingService.updateCurrentPrice(id, newPrice);
            return ResponseEntity.ok(updatedListing);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Mengembalikan 400 jika harga tidak valid
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint: GET /api/listings
     * Digunakan untuk mengambil katalog publik dalam bentuk JSON (Hanya status ACTIVE)
     */
    @GetMapping
    public ResponseEntity<Page<Listing>> getAllListings(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<UUID> categoryIds = new ArrayList<>();

        // Jika user memfilter berdasarkan kategori, ambil kategori itu DAN sub-kategorinya
        if (categoryId != null) {
            categoryIds = categoryService.getCategoryAndSubCategoryIds(categoryId);
        }

        Page<Listing> searchResults = listingService.searchAndFilterListings(
                title, categoryIds, minPrice, maxPrice,null, PageRequest.of(page, size));

        return ResponseEntity.ok(searchResults);
    }

    /**
     * TAMBAHAN: Endpoint Edit (PUT) dengan penanganan error restriksi
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateListing(@PathVariable UUID id, @RequestBody Listing updateData) {
        try {
            Listing updatedListing = listingService.updateListing(id, updateData);
            return ResponseEntity.ok(updatedListing);
        } catch (IllegalStateException e) {
            // Mengembalikan 403 Forbidden jika sudah ada bid / lelang tutup
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * TAMBAHAN: Endpoint Delete (DELETE) dengan penanganan error restriksi
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable UUID id) {
        try {
            listingService.deleteListing(id);
            return ResponseEntity.ok("Listing berhasil dihapus");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}