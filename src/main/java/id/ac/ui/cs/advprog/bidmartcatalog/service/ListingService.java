package id.ac.ui.cs.advprog.bidmartcatalog.service;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.ListingRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ListingService {
    private final ListingRepository repository;

    public ListingService(ListingRepository repository) {
        this.repository = repository;
    }

    public List<Listing> getAllListings() {
        return repository.findAll();
    }
}