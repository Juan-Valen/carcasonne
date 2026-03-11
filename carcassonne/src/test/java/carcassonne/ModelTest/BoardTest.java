package carcassonne.ModelTest;

import carcassonne.DataType.TileSide;
import carcassonne.Model.Board;
import carcassonne.Model.Spot;
import carcassonne.Model.Tile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class BoardTest {

    @Test
    @DisplayName("Test initial center spot is available")
    void testInitialState() {
        Board board = new Board();
        List<Spot> freeSpots = board.getFreeSpots();

        assertEquals(1, freeSpots.size(), "Should start with exactly one free spot at center");
        assertTrue(freeSpots.contains(new Spot(72, 72)));
    }

    @Test
    @DisplayName("Test placing a tile updates free spots")
    void testUpdateSpots() {
        Board board = new Board();
        Tile tile = new Tile('C', new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.CITY, TileSide.CITY });

        // Place at center (72, 72)
        board.updateSpots(72, 72, tile);

        assertNotNull(board.getTile(72, 72));
        // After placing, the center is no longer free, but neighbors should be added
        assertFalse(board.getFreeSpots().contains(new Spot(72, 72)));
        assertTrue(board.getFreeSpots().contains(new Spot(71, 72)), "Left neighbor should be a free spot");
    }

    @Test
    @DisplayName("Test min/max spot boundary tracking")
    void testBoundsTracking() {
        Board board = new Board();
        Tile tile = new Tile('F', new TileSide[] { TileSide.FIELD, TileSide.FIELD, TileSide.FIELD, TileSide.FIELD });

        board.updateSpots(72, 72, tile); // Center
        board.updateSpots(72, 73, tile); // One below

        assertEquals(72, board.getMinSpot().getX());
        assertEquals(72, board.getMinSpot().getY());
        assertEquals(72, board.getMaxSpot().getX());
        assertEquals(73, board.getMaxSpot().getY());
    }
}
