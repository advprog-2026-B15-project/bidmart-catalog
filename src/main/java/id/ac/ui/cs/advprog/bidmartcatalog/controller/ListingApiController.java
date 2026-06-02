package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Category;
import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import id.ac.ui.cs.advprog.bidmartcatalog.service.CategoryService;
import id.ac.ui.cs.advprog.bidmartcatalog.dto.ListingRequestDTO;
import id.ac.ui.cs.advprog.bidmartcatalog.dto.ListingResponseDTO;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingImage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/listings") // Prefix /api untuk membedakan dengan rute HTML
@Tag(name = "Listing Catalog API", description = "Endpoints untuk mengelola dan mencari katalog lelang")
public class ListingApiController {

    private final ListingService listingService;
    private final CategoryService categoryService;

    public ListingApiController(ListingService listingService, CategoryService categoryService) {
        this.listingService = listingService;
        this.categoryService = categoryService;
    }

    private ListingResponseDTO convertToDTO(Listing listing) {
        return ListingResponseDTO.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .categoryId(listing.getCategory() != null ? listing.getCategory().getId() : null)
                .categoryName(listing.getCategory() != null ? listing.getCategory().getName() : null)
                .imageUrls(listing.getImages().stream().map(ListingImage::getImageUrl).collect(Collectors.toList()))
                .sellerId(listing.getSellerId())
                .startingPrice(listing.getStartingPrice())
                .currentPrice(listing.getCurrentPrice())
                .reservePrice(listing.getReservePrice())
                .endTime(listing.getEndTime())
                .status(listing.getStatus())
                .bidCount(listing.getBidCount())
                .createdAt(listing.getCreatedAt())
                .updatedAt(listing.getUpdatedAt())
                .build();
    }

    /**
     * Endpoint: POST /api/listings
     * Membuat listing lelang baru berstatus DRAFT.
     */
    @Operation(summary = "Buat Listing Baru", description = "Membuat listing lelang baru dengan status DRAFT (Mendukung Unggah Gambar)")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createListing(
            @RequestHeader(value = "X-User-Id", required = true) String sellerId,
            @RequestHeader(value = "X-User-Role", required = true) String role,
            @ModelAttribute ListingRequestDTO request,
            @RequestParam(value = "imageFiles", required = false) org.springframework.web.multipart.MultipartFile[] files) {

        if (!"SELLER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Hanya SELLER yang dapat membuat listing.");
        }

        try {
            Listing listing = new Listing();
            listing.setTitle(request.getTitle());
            listing.setDescription(request.getDescription());
            listing.setStartingPrice(request.getStartingPrice());
            listing.setReservePrice(request.getReservePrice());
            listing.setEndTime(request.getEndTime());
            listing.setSellerId(sellerId);
            
            if (request.getCategoryId() != null) {
                Category category = categoryService.getCategoryById(request.getCategoryId());
                listing.setCategory(category);
            }

            Listing createdListing = listingService.createListing(listing, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(createdListing));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint 1: Digunakan Modul Lelang untuk mengecek apakah barang valid dan ACTIVE.
     * Method: GET /api/listings/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ListingResponseDTO> getListingById(@PathVariable UUID id) {
        try {
            Listing listing = listingService.getListingById(id);
            return ResponseEntity.ok(convertToDTO(listing)); 
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Mengembalikan 404 jika tidak ketemu
        }
    }

    /**
     * Endpoint 2: Digunakan Modul Lelang untuk mengupdate harga saat ada bid baru.
     * Method: PATCH /api/listings/{id}/current-price
     * Body JSON: { "newPrice": 5000000.0 }
     */
    @Operation(summary = "Update Harga Terkini", description = "Memperbarui current price saat ada penawaran valid yang masuk")
    @PatchMapping("/{id}/current-price")
    public ResponseEntity<?> updateListingPrice(@PathVariable UUID id, @RequestBody Map<String, Double> payload) {
        try {
            Double newPrice = payload.get("newPrice");
            if (newPrice == null) {
                return ResponseEntity.badRequest().body("Parameter 'newPrice' wajib diisi");
            }

            Listing updatedListing = listingService.updateCurrentPrice(id, newPrice);
            return ResponseEntity.ok(convertToDTO(updatedListing));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Mengembalikan 400 jika harga tidak valid
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint: PATCH /api/listings/{id}/publish
     * Mengubah status DRAFT menjadi ACTIVE.
     */
    @Operation(summary = "Publish Listing", description = "Mengubah status listing DRAFT menjadi ACTIVE")
    @PatchMapping("/{id}/publish")
    public ResponseEntity<?> publishListing(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = true) String sellerId) {
        
        try {
            Listing existingListing = listingService.getListingById(id);
            if (!existingListing.getSellerId().equals(sellerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak: bukan pemilik listing.");
            }
            
            Listing publishedListing = listingService.publishListing(id);
            return ResponseEntity.ok(convertToDTO(publishedListing));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint: GET /api/listings
     * Digunakan untuk mengambil katalog publik dalam bentuk JSON (Hanya status ACTIVE)
     */
    @Operation(summary = "Cari Katalog Lelang", description = "Mengambil daftar barang lelang dengan filter pencarian dan hierarki kategori")
    @GetMapping
    public ResponseEntity<Page<ListingResponseDTO>> getAllListings(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) ListingStatus status,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Default ke ACTIVE jika tidak ditentukan (Katalog Publik)
        ListingStatus filterStatus = (status != null) ? status : ListingStatus.ACTIVE;

        // Security check: Hanya SELLER atau admin (internal) yang boleh melihat DRAFT
        if (filterStatus != ListingStatus.ACTIVE && !"SELLER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<UUID> categoryIds = new ArrayList<>();

        // Jika user memfilter berdasarkan kategori, ambil kategori itu DAN sub-kategorinya
        if (categoryId != null) {
            categoryIds = categoryService.getCategoryAndSubCategoryIds(categoryId);
        }

        Page<Listing> searchResults = listingService.searchAndFilterListings(
                title, categoryIds, minPrice, maxPrice, filterStatus, PageRequest.of(page, size));

        Page<ListingResponseDTO> dtoPage = searchResults.map(this::convertToDTO);

        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Endpoint Edit (PUT) dengan penanganan error restriksi
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateListing(
            @PathVariable UUID id, 
            @RequestHeader(value = "X-User-Id", required = true) String sellerId,
            @RequestHeader(value = "X-User-Role", required = true) String role,
            @RequestBody ListingRequestDTO updateData) {
        
        if (!"SELLER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Hanya SELLER yang dapat mengedit listing.");
        }

        try {
            Listing existingListing = listingService.getListingById(id);
            if (!existingListing.getSellerId().equals(sellerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak: bukan pemilik listing.");
            }

            Listing listingUpdates = new Listing();
            listingUpdates.setTitle(updateData.getTitle());
            listingUpdates.setDescription(updateData.getDescription());
            if (updateData.getCategoryId() != null) {
                listingUpdates.setCategory(categoryService.getCategoryById(updateData.getCategoryId()));
            }

            Listing updatedListing = listingService.updateListing(id, listingUpdates);
            return ResponseEntity.ok(convertToDTO(updatedListing));
        } catch (IllegalStateException e) {
            // Mengembalikan 403 Forbidden jika sudah ada bid / lelang tutup
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint Delete (DELETE) dengan penanganan error restriksi
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteListing(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = true) String sellerId,
            @RequestHeader(value = "X-User-Role", required = true) String role) {
        
        if (!"SELLER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Hanya SELLER yang dapat menghapus listing.");
        }

        try {
            Listing existingListing = listingService.getListingById(id);
            if (!existingListing.getSellerId().equals(sellerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak: bukan pemilik listing.");
            }

            listingService.deleteListing(id);
            return ResponseEntity.ok("Listing berhasil dihapus");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Validasi Internal Lelang", description = "Mengecek apakah listing valid untuk menerima penawaran baru (digunakan oleh Modul Auction)")
    @GetMapping("/{id}/validate")
    public ResponseEntity<Map<String, Object>> validateListingForBid(@PathVariable UUID id) {
        try {
            Listing listing = listingService.getListingById(id);
            Map<String, Object> response = new HashMap<>();

            boolean isActive = listing.getStatus() == ListingStatus.ACTIVE;
            boolean isTimeValid = listing.getEndTime().isAfter(LocalDateTime.now());

            // Barang valid jika statusnya ACTIVE dan waktunya belum lewat
            boolean isValid = isActive && isTimeValid;

            response.put("listingId", listing.getId());
            response.put("isValid", isValid);
            response.put("currentPrice", listing.getCurrentPrice());
            response.put("endTime", listing.getEndTime());

            if (!isValid) {
                response.put("reason", !isActive ? "Listing is not active" : "Auction time has ended");
            }

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint Statistik Seller (Milestone 100)
     * Method: GET /api/listings/seller/{sellerId}/stats
     */
    @Operation(summary = "Statistik Performa Penjual", description = "Mengambil statistik jumlah listing berdasarkan status untuk penjual tertentu")
    @GetMapping("/seller/{sellerId}/stats")
    public ResponseEntity<Map<String, Object>> getSellerStats(@PathVariable String sellerId) {
        Map<String, Object> stats = listingService.getSellerStatistics(sellerId);
        return ResponseEntity.ok(stats);
    }
}
