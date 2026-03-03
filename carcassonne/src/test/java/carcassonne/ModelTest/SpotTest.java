package carcassonne.ModelTest;

import carcassonne.Model.Spot;

import carcassonne.Model.Tile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpotTest
{
    Spot spot = new Spot(74, 99);

    @DisplayName("Test gets")
    @Test
    public void testGets()
    {
        assertEquals(74, spot.getX(), "Wrong X coordinate");
        assertEquals(99, spot.getY(), "Wrong Y coordinate");

    }

    @DisplayName("Test tile")
    @Test
    public void testTile()
    {
        assertFalse(spot.hasTile(), "Spot shouldn't have tile");
        assertNull(spot.getTile(), "Spot shouldn't have tile");

        Tile tile = new Tile(0,new int[]{1, 2, 3, 4});

        spot.setTile(tile);

        assertTrue(spot.hasTile(), "Spot should have tile");
        assertEquals(tile, spot.getTile(), "Spot didn't return the correct tile");
    }



}
