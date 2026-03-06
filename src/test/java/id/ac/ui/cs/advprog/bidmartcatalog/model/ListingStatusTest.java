package id.ac.ui.cs.advprog.bidmartcatalog.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListingStatusTest {

    @Test
    @DisplayName("Test Enum Values and Order")
    void testEnumValues() {
        ListingStatus[] statuses = ListingStatus.values();

        assertEquals(3, statuses.length);
        assertEquals(ListingStatus.DRAFT, statuses[0]);
        assertEquals(ListingStatus.ACTIVE, statuses[1]);
        assertEquals(ListingStatus.CLOSED, statuses[2]);
    }

    @Test
    @DisplayName("Test Enum ValueOf")
    void testEnumValueOf() {
        assertEquals(ListingStatus.DRAFT, ListingStatus.valueOf("DRAFT"));
        assertEquals(ListingStatus.ACTIVE, ListingStatus.valueOf("ACTIVE"));
        assertEquals(ListingStatus.CLOSED, ListingStatus.valueOf("CLOSED"));
    }

    @Test
    @DisplayName("Test Enum Name and Consistency")
    void testEnumName() {
        assertEquals("DRAFT", ListingStatus.DRAFT.name());
        assertEquals("ACTIVE", ListingStatus.ACTIVE.name());
        assertEquals("CLOSED", ListingStatus.CLOSED.name());
    }
}