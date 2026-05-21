package id.ac.ui.cs.advprog.bidmartcatalog.repository;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Listing;
import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ListingSpecificationTest {

    @Test
    @DisplayName("Test filterListings specification logic")
    void testFilterListings() {
        Root<Listing> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<Object> statusPath = mock(Path.class);
        Path<Object> titlePath = mock(Path.class);
        Path<Object> descPath = mock(Path.class);
        Path<Object> categoryPath = mock(Path.class);
        Path<Object> idPath = mock(Path.class);
        Path<Object> pricePath = mock(Path.class);

        when(root.get("status")).thenReturn(statusPath);
        when(root.get("title")).thenReturn(titlePath);
        when(root.get("description")).thenReturn(descPath);
        when(root.get("category")).thenReturn(categoryPath);
        when(categoryPath.get("id")).thenReturn(idPath);
        when(root.get("currentPrice")).thenReturn(pricePath);

        Specification<Listing> spec = ListingSpecification.filterListings(
                "keyword", 
                List.of(UUID.randomUUID()), 
                10.0, 
                100.0, 
                ListingStatus.ACTIVE
        );

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(spec);
    }
    
    @Test
    @DisplayName("Test filterListings with null parameters")
    void testFilterListingsNulls() {
        Root<Listing> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        
        Specification<Listing> spec = ListingSpecification.filterListings(null, null, null, null, null);
        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(spec);
    }
}
