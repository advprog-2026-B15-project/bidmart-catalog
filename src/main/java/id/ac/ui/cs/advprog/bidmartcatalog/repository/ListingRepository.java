package id.ac.ui.cs.advprog.bidmartcatalog.repository;

import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, UUID> {

    List<Listing> findBySellerId(String sellerId);

    List<Listing> findByStatus(ListingStatus status);

}