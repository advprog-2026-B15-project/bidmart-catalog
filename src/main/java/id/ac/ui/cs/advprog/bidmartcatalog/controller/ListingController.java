package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import id.ac.ui.cs.advprog.bidmartcatalog.service.CategoryService;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/listings")
public class ListingController {

    private final ListingService listingService;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService; // Tambahan untuk logika rekursif kategori

    // Perbarui Constructor
    public ListingController(ListingService listingService, CategoryRepository categoryRepository, CategoryService categoryService) {
        this.listingService = listingService;
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String getListings(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) ListingStatus status, // <-- TAMBAHAN: Tangkap status dari URL
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        List<UUID> categoryIds = new ArrayList<>();

        if (categoryId != null) {
            categoryIds = categoryService.getCategoryAndSubCategoryIds(categoryId);
        }

        // Masukkan status ke dalam pemanggilan Service
        Page<Listing> listings = listingService.searchAndFilterListings(
                title, categoryIds, minPrice, maxPrice, status, PageRequest.of(page, size));

        model.addAttribute("listings", listings.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listings.getTotalPages());

        model.addAttribute("title", title);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status); // <-- TAMBAHAN: Kembalikan ke HTML

        model.addAttribute("categories", categoryRepository.findByParentCategoryIsNull());

        return "listings";
    }

    @GetMapping("/create")
    public String createListingForm(Model model) {
        model.addAttribute("listing", new Listing());
        // PERBAIKAN: Hanya kirim kategori utama (Root) agar rapi, tidak muncul semua secara flat
        model.addAttribute("categories", categoryRepository.findByParentCategoryIsNull());
        return "create-listing";
    }

    @PostMapping("/create")
    public String createListing(@ModelAttribute Listing listing,
                                @RequestParam(value = "imageFiles", required = false) MultipartFile[] files) {
        listingService.createListing(listing, files);
        return "redirect:/listings";
    }

    @PostMapping("/{id}/publish")
    public String publishListing(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            listingService.publishListing(id);
            redirectAttributes.addFlashAttribute("message", "Listing published successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Listing cannot be published");
        }
        return "redirect:/listings";
    }

    @GetMapping("/{id}")
    public String getListingDetail(@PathVariable UUID id, Model model) {
        Listing listing = listingService.getListingById(id);
        model.addAttribute("listing", listing);
        return "listing-detail";
    }

    @GetMapping("/{id}/edit")
    public String editListingForm(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Listing listing = listingService.getListingById(id);

            if (listing.getBidCount() != null && listing.getBidCount() > 0) {
                redirectAttributes.addFlashAttribute("error", "Tidak bisa mengedit: Sudah ada penawaran!");
                return "redirect:/listings/" + id;
            }

            model.addAttribute("listing", listing);
            // PERBAIKAN: Hanya kirim kategori utama
            model.addAttribute("categories", categoryRepository.findByParentCategoryIsNull());
            return "edit-listing";
        } catch (RuntimeException e) {
            return "redirect:/listings";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateListing(@PathVariable UUID id, @ModelAttribute Listing listingUpdate, RedirectAttributes redirectAttributes) {
        try {
            listingService.updateListing(id, listingUpdate);
            redirectAttributes.addFlashAttribute("message", "Listing berhasil diupdate.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/listings/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteListing(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            listingService.deleteListing(id);
            redirectAttributes.addFlashAttribute("message", "Listing berhasil dihapus.");
            return "redirect:/listings";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/listings/" + id;
        }
    }
}