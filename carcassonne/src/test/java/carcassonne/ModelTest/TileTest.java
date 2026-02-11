package carcassonne.ModelTest;

import carcassonne.Model.Tile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class TileTest {
    Tile tile = new Tile(0, new int[]{1, 2, 3, 4});

    @DisplayName("Test getSideType")
    @Test
    public void testGetSideType()
    {
        assertEquals(1, tile.getSideType(0), "Wrong side for 0");
        assertEquals(3, tile.getSideType(2), "Wrong side for 2");
        assertEquals(2, tile.getSideType(5), "Wrong side for 5");
    }

    public void testRotateTile()
    {
        assertEquals(1, tile.getSideType(0), "Wrong side for flip 0");
        tile.rotateTile(1);
        assertEquals(2, tile.getSideType(0), "Wrong side for flip 1");
        tile.rotateTile(5);
        assertEquals(3, tile.getSideType(0), "Wrong side for flip 5");
    }

}
