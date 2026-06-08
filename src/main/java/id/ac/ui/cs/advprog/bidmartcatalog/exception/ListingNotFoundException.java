package id.ac.ui.cs.advprog.bidmartcatalog.exception;

import java.util.UUID;

public class ListingNotFoundException extends RuntimeException {
    public ListingNotFoundException(UUID id) {
        super("Listing not found with id: " + id);
    }

    public ListingNotFoundException(String message) {
        super(message);
    }
}
