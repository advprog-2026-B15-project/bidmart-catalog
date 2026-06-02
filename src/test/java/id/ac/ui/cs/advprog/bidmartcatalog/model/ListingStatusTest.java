package id.ac.ui.cs.advprog.bidmartcatalog.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ListingStatusTest {

    @Test
    @DisplayName("Test Enum Values and Order")
    void testEnumValues() {
        ListingStatus[] statuses = ListingStatus.values();

        assertAll(
                () -> assertEquals(3, statuses.length, "ListingStatus should have exactly 3 values"),
                () -> assertEquals(ListingStatus.DRAFT, statuses[0], "First status should be DRAFT"),
                () -> assertEquals(ListingStatus.ACTIVE, statuses[1], "Second status should be ACTIVE"),
                () -> assertEquals(ListingStatus.CLOSED, statuses[2], "Third status should be CLOSED")
        );
    }

    @Test
    @DisplayName("Test Enum ValueOf")
    void testEnumValueOf() {
        assertAll(
                () -> assertEquals(ListingStatus.DRAFT, ListingStatus.valueOf("DRAFT"), "valueOf 'DRAFT' should return DRAFT"),
                () -> assertEquals(ListingStatus.ACTIVE, ListingStatus.valueOf("ACTIVE"), "valueOf 'ACTIVE' should return ACTIVE"),
                () -> assertEquals(ListingStatus.CLOSED, ListingStatus.valueOf("CLOSED"), "valueOf 'CLOSED' should return CLOSED")
        );
    }

    @Test
    @DisplayName("Test Enum Name and Consistency")
    void testEnumName() {
        assertAll(
                () -> assertEquals("DRAFT", ListingStatus.DRAFT.name(), "name() of DRAFT should be 'DRAFT'"),
                () -> assertEquals("ACTIVE", ListingStatus.ACTIVE.name(), "name() of ACTIVE should be 'ACTIVE'"),
                () -> assertEquals("CLOSED", ListingStatus.CLOSED.name(), "name() of CLOSED should be 'CLOSED'")
        );
    }
}