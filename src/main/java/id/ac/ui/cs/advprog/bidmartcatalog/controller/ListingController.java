package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import id.ac.ui.cs.advprog.bidmartcatalog.service.ListingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ListingController {
    private final ListingService service;

    public ListingController(ListingService service) {
        this.service = service;
    }

    @GetMapping("/listing/list")
    public String showCatalog(Model model) {
        model.addAttribute("listings", service.getAllListings());
        return "listingList";
    }
}