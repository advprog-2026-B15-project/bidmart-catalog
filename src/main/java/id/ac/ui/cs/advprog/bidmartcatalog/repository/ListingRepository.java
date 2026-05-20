package id.ac.ui.cs.advprog.bidmartcatalog.repository;

import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, UUID> , JpaSpecificationExecutor<Listing> {

    List<Listing> findBySellerId(String sellerId);
    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);

    @Query("SELECT COUNT(l) FROM Listing l WHERE l.sellerId = :sellerId")
    long countBySellerId(@Param("sellerId") String sellerId);

    @Query("SELECT COUNT(l) FROM Listing l WHERE l.sellerId = :sellerId AND l.status = :status")
    long countBySellerIdAndStatus(@Param("sellerId") String sellerId, @Param("status") ListingStatus status);

}