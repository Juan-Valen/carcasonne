package carcassonne.ModelTest;

import carcassonne.DataType.TileSide;
import carcassonne.Model.Spot;
import carcassonne.Model.Tile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpotTest {

    @Test
    @DisplayName("Test coordinate assignment")
    void testCoordinates() {
        Spot spot = new Spot(5, 10);
        assertEquals(5, spot.getX());
        assertEquals(10, spot.getY());

        spot.setX(1);
        spot.setY(2);
        assertEquals(1, spot.getX());
        assertEquals(2, spot.getY());
    }

    @Test
    @DisplayName("Test Tile association and hasTile check")
    void testTileAssociation() {
        Spot spot = new Spot(0, 0);
        assertFalse(spot.hasTile(), "Spot should be empty initially");

        Tile tile = new Tile('C', new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.CITY, TileSide.CITY });
        spot.setTile(tile);

        assertTrue(spot.hasTile(), "Spot should contain a tile");
        assertEquals(tile, spot.getTile());
    }

    @Test
    @DisplayName("Test equality based on coordinates")
    void testEquals() {
        Spot spot1 = new Spot(10, 20);
        Spot spot2 = new Spot(10, 20);
        Spot spot3 = new Spot(5, 5);

        // Spot objects with same coordinates should be equal even if tile states differ
        assertEquals(spot1, spot2, "Spots with same coordinates should be equal");
        assertNotEquals(spot1, spot3, "Spots with different coordinates should not be equal");

        // Verify cross-type safety for equals
        assertNotEquals(spot1, "Some String", "Spot should not be equal to a different object type");
    }

    @Test

    void testConstructorAndGetters() {

        Tile mockTile = new Tile('A', null); // Assuming Tile has a default constructor

        Spot spot = new Spot(5, -3, mockTile);

        assertEquals(5, spot.getX());

        assertEquals(-3, spot.getY());

        assertEquals(mockTile, spot.getTile());

    }

    @Test

    void testHashCodeConsistency() {

        Tile tile1 = new Tile('A', null);

        Spot spot1 = new Spot(1, 1, tile1);

        Spot spot2 = new Spot(1, 1, null); // Different tile, same coordinates

        // HashCode should be based on X and Y, not the Tile object

        assertEquals(spot1.hashCode(), spot2.hashCode(),

                "Spots with same coordinates must have the same hashCode");

    }

    @Test

    void testHashCodeUniqueness() {

        Spot spot1 = new Spot(1, 2, null);

        Spot spot2 = new Spot(2, 1, null);

        assertNotEquals(spot1.hashCode(), spot2.hashCode(),

                "Different coordinates should produce different hashCodes");

    }

    @Test
    void testEqualsSpots() {
        Tile tileA = new Tile('A', null);

        Tile tileB = new Tile('B', null);

        Spot spot1 = new Spot(10, 10, tileA);

        Spot spot2 = new Spot(10, 10, tileB);

        Spot spot3 = new Spot(10, 11, tileA);

        // Verification of equality logic (assuming you have or will implement equals())

        assertEquals(spot1, spot2, "Spots at same coordinates should be equal");

        assertNotEquals(spot1, spot3, "Spots at different coordinates should not be equal");

        assertNotEquals(null, spot1, "Spot should not equal null");

    }

    @Test

    void testCoordinateStorage() {

        // Test edge cases like zero and large numbers

        Spot spot = new Spot(0, 0, null);

        assertEquals(0, spot.getX());

        assertEquals(0, spot.getY());

        Spot largeSpot = new Spot(Integer.MAX_VALUE, Integer.MIN_VALUE, null);

        assertEquals(Integer.MAX_VALUE, largeSpot.getX());

        assertEquals(Integer.MIN_VALUE, largeSpot.getY());

    }
}
