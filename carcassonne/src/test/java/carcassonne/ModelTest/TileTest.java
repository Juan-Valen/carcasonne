package carcassonne.ModelTest;

import carcassonne.DataType.TileSide;
import carcassonne.Model.Tile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TileTest {

    @Test
    @DisplayName("Verify correct orientation and side mapping")
    void testRotationAndSideMapping() {
        // Order: North, East, South, West
        TileSide[] sides = { TileSide.CITY, TileSide.ROAD, TileSide.FIELD, TileSide.RIVER };
        Tile tile = new Tile('C', sides, 0);

        // Initial check
        assertEquals(TileSide.CITY, tile.getSideType(0));
        assertEquals(TileSide.ROAD, tile.getSideType(1));

        // Rotate 90 degrees clockwise
        tile.rotateTile();
        assertEquals(3, tile.getOrientation());

        // After 90° rotation, side 0 (North) should now be the original side 3 (West)
        // (0 + 3) % 4 = 3
        assertEquals(TileSide.RIVER, tile.getSideType(0));
        assertEquals(TileSide.CITY, tile.getSideType(1));
    }

    @Test
    @DisplayName("Verify bonus point logic based on type")
    void testBonusPointLogic() {
        TileSide[] sides = { TileSide.FIELD, TileSide.FIELD, TileSide.FIELD, TileSide.FIELD };

        Tile bonusTile = new Tile('C', sides);
        assertTrue(bonusTile.getBonusPoint(), "Type 'C' should have bonus points");

        Tile noBonusTile = new Tile('X', sides);
        assertFalse(noBonusTile.getBonusPoint(), "Type 'X' should not have bonus points");
    }

    @Test
    @DisplayName("Verify side index out of bounds")
    void testOutOfBounds() {
        TileSide[] sides = { TileSide.FIELD, TileSide.FIELD, TileSide.FIELD, TileSide.FIELD };
        Tile tile = new Tile('F', sides);

        assertThrows(IndexOutOfBoundsException.class, () -> tile.getSideType(4));
    }
}
