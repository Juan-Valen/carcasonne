package carcassonne.DataTypeTest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import carcassonne.DataType.TileSide;

class TileSideTest {

    @Test

    void testEnumIds() {

        // Assert that each enum constant has the correct assigned ID

        assertEquals(0, TileSide.FIELD.getId(), "FIELD should have ID 0");

        assertEquals(1, TileSide.ROAD.getId(), "ROAD should have ID 1");

        assertEquals(2, TileSide.CITY.getId(), "CITY should have ID 2");

        assertEquals(3, TileSide.RIVER.getId(), "RIVER should have ID 3");

    }

    @Test

    void testEnumValuesLength() {

        // Ensures no unexpected types were added or removed

        assertEquals(4, TileSide.values().length, "TileSide should have exactly 4 types");

    }

    @Test

    void testValueOrdering() {

        // Useful if your logic relies on ordinal values or specific iteration order

        TileSide[] sides = TileSide.values();

        assertSame(TileSide.FIELD, sides[0]);

        assertSame(TileSide.ROAD, sides[1]);

        assertSame(TileSide.CITY, sides[2]);

        assertSame(TileSide.RIVER, sides[3]);

    }

    @Test

    void testFromString() {

        // Verifies the standard Enum valueOf functionality

        assertEquals(TileSide.CITY, TileSide.valueOf("CITY"));

        assertThrows(IllegalArgumentException.class, () -> {

            TileSide.valueOf("FOREST");

        }, "Should throw exception for invalid side type");

    }

}
