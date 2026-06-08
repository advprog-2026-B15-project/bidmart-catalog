package id.ac.ui.cs.advprog.bidmartcatalog.repository;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListingSpecification {

    private ListingSpecification() {
        // Utility class, hide constructor
    }

    public static Specification<Listing> filterListings(
            String keyword,
            List<UUID> categoryIds,
            Double minPrice,
            Double maxPrice,
            ListingStatus status,
            String sellerId) {

        return (root, query, cb) -> {
            applyDistinctIfNeeded(query);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(getSellerPredicate(root, cb, sellerId));
            predicates.add(getStatusPredicate(root, cb, status));
            predicates.add(getKeywordPredicate(root, cb, keyword));
            predicates.add(getCategoryPredicate(root, categoryIds));
            predicates.add(getMinPricePredicate(root, cb, minPrice));
            predicates.add(getMaxPricePredicate(root, cb, maxPrice));

            return cb.and(predicates.stream()
                    .filter(Objects::nonNull)
                    .toArray(Predicate[]::new));
        };
    }

    private static void applyDistinctIfNeeded(CriteriaQuery<?> query) {
        if (query.getResultType() != Long.class && query.getResultType() != long.class) {
            query.distinct(true);
        }
    }

    private static Predicate getSellerPredicate(Root<Listing> root, CriteriaBuilder cb, String sellerId) {
        if (sellerId == null || sellerId.trim().isEmpty()) {
            return null;
        }
        return cb.equal(root.get("sellerId"), sellerId);
    }

    private static Predicate getStatusPredicate(Root<Listing> root, CriteriaBuilder cb, ListingStatus status) {
        if (status == null) {
            return null;
        }
        return cb.equal(root.get("status"), status);
    }

    private static Predicate getKeywordPredicate(Root<Listing> root, CriteriaBuilder cb, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        String searchPattern = "%" + keyword.toLowerCase(java.util.Locale.ROOT) + "%";
        Predicate titleMatch = cb.like(cb.lower(root.get("title")), searchPattern);
        Predicate descMatch = cb.like(cb.lower(root.get("description")), searchPattern);
        return cb.or(titleMatch, descMatch);
    }

    private static Predicate getCategoryPredicate(Root<Listing> root, List<UUID> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }
        return root.get("category").get("id").in(categoryIds);
    }

    private static Predicate getMinPricePredicate(Root<Listing> root, CriteriaBuilder cb, Double minPrice) {
        if (minPrice == null) {
            return null;
        }
        return cb.greaterThanOrEqualTo(root.get("currentPrice"), minPrice);
    }

    private static Predicate getMaxPricePredicate(Root<Listing> root, CriteriaBuilder cb, Double maxPrice) {
        if (maxPrice == null) {
            return null;
        }
        return cb.lessThanOrEqualTo(root.get("currentPrice"), maxPrice);
    }
}