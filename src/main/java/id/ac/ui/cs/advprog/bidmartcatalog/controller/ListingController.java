package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.UUID;

@Controller
@RequestMapping("/listings")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    /**
     * Show all listings (with pagination)
     */
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

    /**
     * Show create listing form
     */
    @GetMapping("/create")
    public String createListingForm(Model model) {
        model.addAttribute("listing", new Listing());
        return "create-listing";
    }

    /**
     * Handle create listing
     */
    @PostMapping("/create")
    public String createListing(@ModelAttribute Listing listing) {
        listingService.createListing(listing);
        return "redirect:/listings";
    }

    /**
     * Publish listing
     */
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
}