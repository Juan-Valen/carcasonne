package carcassonne.ModelTest;

import carcassonne.DataType.TileSide;
import carcassonne.Model.Board;
import carcassonne.Model.Spot;
import carcassonne.Model.Tile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    @DisplayName("Test initial center spot is available")
    void testInitialState() {
        List<Spot> freeSpots = board.getFreeSpots();

        assertEquals(1, freeSpots.size(), "Should start with exactly one free spot at center");
        assertTrue(freeSpots.contains(new Spot(72, 72)));
    }

    @Test
    @DisplayName("Test placing a tile updates free spots")
    void testUpdateSpots() {
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
        Tile tile = new Tile('F', new TileSide[] { TileSide.FIELD, TileSide.FIELD, TileSide.FIELD, TileSide.FIELD });

        board.updateSpots(72, 72, tile); // Center
        board.updateSpots(72, 73, tile); // One below

        assertEquals(72, board.getMinSpot().getX());
        assertEquals(72, board.getMinSpot().getY());
        assertEquals(72, board.getMaxSpot().getX());
        assertEquals(73, board.getMaxSpot().getY());
    }

    @Test
    void testGetTileBoundariesException() {
        // Test negative boundaries
        assertThrows(IllegalArgumentException.class, () -> board.getTile(-1, 72));
        assertThrows(IllegalArgumentException.class, () -> board.getTile(72, -1));
        assertThrows(IllegalArgumentException.class, () -> board.getTile(0, 145));
        assertThrows(IllegalArgumentException.class, () -> board.getTile(0, 145));
        assertThrows(IllegalArgumentException.class, () -> board.getTile(145, 72));
        assertThrows(IllegalArgumentException.class, () -> board.getTile(145, 72));

        // Test upper boundaries (Array size is 144, so 144 is actually out of bounds)
        assertThrows(Exception.class, () -> board.getTile(144, 72));

    }

    @Test
    void testGetVisibleTileReturnsCorrectTile() {
        // Arrange
        Tile testTile = new Tile('C', null);
        int testX = 75;
        int testY = 80;

        // Use updateSpots to place the tile in the internal array
        board.updateSpots(testX, testY, testTile);
        // Act
        Tile retrievedTile = board.getVisibleTile(testX, testY);

        // Assert
        assertNotNull(retrievedTile, "Retrieved tile should not be null");
        assertEquals(testTile, retrievedTile, "The retrieved tile should be the same instance as the one placed");

    }

    @Test
    void testGetVisibleTileReturnsNullForEmptySpot() {
        // Coordinates (10, 10) should be empty on a new board
        Tile retrievedTile = board.getVisibleTile(10, 10);

        // Assert
        assertNull(retrievedTile, "An empty spot should return null");
    }

    @Test
    void testGetVisibleTileCoordinateOrder() {
        // This test ensures the method correctly interprets [y][x] mapping
        Tile specificTile = new Tile('B', null);
        int x = 10;
        int y = 20;
        board.updateSpots(x, y, specificTile);
        // Act & Assert
        // If the indices were swapped internally (e.g., [x][y] instead of [y][x]),
        // this would likely return null or throw an error.
        assertEquals(specificTile, board.getVisibleTile(x, y),
                "getVisibleTile should correctly map x to the second index and y to the first");
    }

    @Test
    void testNeighborMatchingLogic() {
        // 1. Place a base tile that has a ROAD on the RIGHT (side 1)
        TileSide[] sides = { TileSide.FIELD, TileSide.ROAD, TileSide.FIELD, TileSide.FIELD };
        Tile baseTile = new Tile('A', sides);
        board.updateSpots(72, 72, baseTile);

        // 2. Create a new tile that has a ROAD on its LEFT (side 3)
        TileSide[] matchingSides = { TileSide.FIELD, TileSide.FIELD, TileSide.FIELD, TileSide.ROAD };
        Tile matchingTile = new Tile('A', matchingSides);

        // 3. Create a new tile that has a CITY on its LEFT (side 3) - SHOULD FAIL
        TileSide[] nonMatchingSides = { TileSide.FIELD, TileSide.FIELD, TileSide.FIELD, TileSide.CITY };
        Tile badTile = new Tile('B', nonMatchingSides);
        // Test Success Case: Road matches Road
        board.updateAvailableSpots(matchingTile);
        assertTrue(board.getAvailableSpots(0).stream().anyMatch(s -> s.getX() == 73 && s.getY() == 72),
                "Should allow matching Road next to Road");
        // Test Failure Case: City does not match Road
        board.updateAvailableSpots(badTile);
        assertTrue(board.getAvailableSpots(0).stream().noneMatch(s -> s.getX() == 73 && s.getY() == 72),
                "Should NOT allow City next to Road");
    }

    @Test
    void testFreeSpotDiscovery() {
        board.updateSpots(72, 72, new Tile('A', null));
        // At start, center (72,72) was the only free spot.
        // Now it should be removed, and 4 neighbors added.
        List<Spot> free = board.getFreeSpots();
        assertFalse(free.contains(new Spot(72, 72)), "The placed spot should no longer be 'free'");
        assertEquals(4, free.size(), "Should have found 4 neighbors");
        board.updateSpots(0, 0, new Tile('A', null));

        assertTrue(board.getFreeSpots().stream().noneMatch(s -> s.getX() < 0 || s.getY() < 0));

    }

    @Test

    void testHasAvailableSpotsLogic() {
        // Initially false if no calculations done or no matches possible
        // Note: your constructor adds a center spot, so logic depends on initial state
        assertTrue(board.hasAvailableSpots(), "Initial center spot should be available");

        // If we passed a null tile to updateAvailableSpots, it should return early
        board.updateAvailableSpots(null);
        // (Verification depends on if clearAvailableSpots was called before the null
        // check)

    }
}
