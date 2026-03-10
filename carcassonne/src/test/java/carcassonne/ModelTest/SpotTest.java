package carcassonne.ModelTest;

import carcassonne.DataType.TileSide;
import carcassonne.Model.Spot;
import carcassonne.Model.Tile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpotTest {

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
}
