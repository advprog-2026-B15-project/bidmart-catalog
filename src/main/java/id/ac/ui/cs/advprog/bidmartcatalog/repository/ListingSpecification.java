package id.ac.ui.cs.advprog.bidmartcatalog.repository;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListingSpecification {

    public static Specification<Listing> filterListings(
            String title,
            List<UUID> categoryIds,
            Double minPrice,
            Double maxPrice) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filter Pencarian Judul (Case Insensitive)
            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            // 2. Filter Kategori (Hierarki: Mencari di parent maupun child)
            if (categoryIds != null && !categoryIds.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categoryIds));
            }

            // 3. Filter Harga
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("currentPrice"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("currentPrice"), maxPrice));
            }

            // 4. Filter Waktu (Hanya lelang yang masih berlangsung dan status ACTIVE)
            predicates.add(cb.greaterThan(root.get("endTime"), LocalDateTime.now()));
            predicates.add(cb.equal(root.get("status"), ListingStatus.ACTIVE));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}