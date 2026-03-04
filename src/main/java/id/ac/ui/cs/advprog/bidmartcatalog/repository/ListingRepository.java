package id.ac.ui.cs.advprog.bidmartcatalog.repository;

import id.ac.ui.cs.advprog.bidmart.bidmartcatalog.model.Listing;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ListingRepository {
    private final List<Listing> listings = new ArrayList<>();

    public void save(Listing listing) {
        listings.add(listing);
    }

    public List<Listing> findAll() {
        return listings;
    }
}