package carcassonne.ModelTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import carcassonne.Model.Game;
import carcassonne.Model.Tile;
import carcassonne.Model.Player;
import carcassonne.Model.Board;
import carcassonne.Model.Spot;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game();
        game.setMaxPlayer(2);
    }

    @Test
    @DisplayName("Test initial game setup and deck size")
    @Disabled
    void testInitialState() {
        assertNotNull(game.getCurrentTile(), "Deck should be initialized with tiles");
        assertEquals(0, game.getActivePlayer(), "Player 0 should start first");

        int[] meples = game.getPlayersMeepleCount();
        assertEquals(5, meples[0]);
        assertEquals(5, meples[1]);
    }

    @Test
    @DisplayName("Test tile rotation")
    void testRotateTile() {
        Tile tile = game.getCurrentTile();
        int initialOrientation = tile.getOrientation();

        game.rotateTile(true);

        // Your rotateTile() adds 3, which is 270 deg (or -90 deg)
        int expected = (initialOrientation + 3) % 4;
        assertEquals(expected, tile.getOrientation());
    }

    @Test
    @DisplayName("Test player turn switching after placing a tile")
    void testTurnSwitching() {
        // We place a tile at the center (72, 72)
        // Using null for Pane as it's not strictly required for the logic
        game.placeTile(72, 72, null);

        assertEquals(1, game.getActivePlayer(), "Turn should switch to Player 1");

        game.placeTile(72, 73, null);
        assertEquals(0, game.getActivePlayer(), "Turn should switch back to Player 0");
    }

    @Test
    @DisplayName("Test Meeple placement and retrieval")
    void testMeeplePlacement() {
        // Place a meeple on the current tile at position 0 (e.g., North)
        game.placeMeeple(0);

        Tile tile = game.getCurrentTile();
        assertNotNull(tile.getMeeple());
        assertEquals(0, tile.getMeeple().getPosition());

        // Remove the meeple
        game.placeMeeple(-1);
        assertNull(tile.getMeeple(), "Meeple should be removed from tile");
    }

    @Test
    void testGetPlayersMeepleCount() {
        int[] counts = game.getPlayersMeepleCount();

        // Assert
        assertEquals(2, counts.length);
        assertEquals(7, counts[0]);
        assertEquals(7, counts[1]);
    }

    @Test
    void testGetPlayersScores() {
        Player[] players = game.getPlayers();
        players[0].addPoints(10);
        players[1].addPoints(25);

        int[] scores = game.getPlayersScores();

        // Assert
        assertEquals(10, scores[0]);

        assertEquals(25, scores[1]);

    }

    @Test
    void testHasPlacedTilesLogic() {
        assertFalse(game.hasPlacedTiles(), "Should be false when only the starting spot exists");
        game.placeTile(72, 72, null);
        assertTrue(game.hasPlacedTiles(), "Should be true after the first tile is placed");
    }

    @Test
    void testEndGameLogic() {
        assertFalse(game.endGame(), "Game should not end while deck has tiles");
    }

    @Test
    void testGetMinMaxDelegation() {
        // Verify the game correctly fetches the bounding box from the board
        Board b = game.getBoard();
        Spot min = game.getMin();
        Spot max = game.getMax();

        assertNotNull(min);
        assertNotNull(max);

        assertEquals(b.getMinSpot(), min, "Game should delegate getMin to Board");
        assertEquals(b.getMaxSpot(), max, "Game should delegate getMax to Board");

    }

    @Test
    void testNullPlayerHandling() {
        Player[] players = game.getPlayers();
        players[1] = null;

        int[] scores = game.getPlayersScores();
        int[] meeples = game.getPlayersMeepleCount();

        assertEquals(0, scores[1], "Null player should result in 0 score");
        assertEquals(0, meeples[1], "Null player should result in 0 meeples");

    }
}
