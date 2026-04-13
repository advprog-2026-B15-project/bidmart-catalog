package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.CategoryRepository; // Tambahkan import ini
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Controller
@RequestMapping("/listings")
public class ListingController {

    private final ListingService listingService;
    private final CategoryRepository categoryRepository; // Tambahkan dependency ini

    // Perbarui Constructor untuk injeksi kedua dependency
    public ListingController(ListingService listingService, CategoryRepository categoryRepository) {
        this.listingService = listingService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String getListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Listing> listings = listingService.getAllListings(PageRequest.of(page, size));
        model.addAttribute("listings", listings.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listings.getTotalPages());
        return "listings";
    }

    @GetMapping("/create")
    public String createListingForm(Model model) {
        model.addAttribute("listing", new Listing());
        // Ambil semua kategori dari database dan kirim ke view HTML
        model.addAttribute("categories", categoryRepository.findAll());
        return "create-listing";
    }

    @PostMapping("/create")
    public String createListing(@ModelAttribute Listing listing,
                                @RequestParam(value = "imageFiles", required = false) MultipartFile[] files) {
        // Panggil service yang baru diupdate
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

    /**
     * Tampilkan Halaman Detail Listing
     */
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

            // Cek di awal apakah boleh diedit (bisa juga pakai method di entity: isEditable())
            if (listing.getBidCount() != null && listing.getBidCount() > 0) {
                redirectAttributes.addFlashAttribute("error", "Tidak bisa mengedit: Sudah ada penawaran!");
                return "redirect:/listings/" + id;
            }

            model.addAttribute("listing", listing);
            model.addAttribute("categories", categoryRepository.findAll());
            return "edit-listing"; // Pastikan Anda membuat file edit-listing.html
        } catch (RuntimeException e) {
            return "redirect:/listings";
        }
    }

    /**
     * TAMBAHAN: Menangkap submit dari form Edit
     */
    @PostMapping("/{id}/edit")
    public String updateListing(@PathVariable UUID id, @ModelAttribute Listing listingUpdate, RedirectAttributes redirectAttributes) {
        try {
            listingService.updateListing(id, listingUpdate);
            redirectAttributes.addFlashAttribute("message", "Listing berhasil diupdate.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage()); // Menangkap pesan error dari Service
        }
        return "redirect:/listings/" + id;
    }

    /**
     * TAMBAHAN: Menghapus listing (via form POST karena HTML forms belum support DELETE secara native tanpa JS)
     */
    @PostMapping("/{id}/delete")
    public String deleteListing(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            listingService.deleteListing(id);
            redirectAttributes.addFlashAttribute("message", "Listing berhasil dihapus.");
            return "redirect:/listings";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage()); // Menampilkan error restriksi
            return "redirect:/listings/" + id;
        }
    }


}