package carcassonne.ModelTest;

import carcassonne.Model.Board;
import carcassonne.Model.Spot;
import carcassonne.Model.Tile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    Board board = new Board();

    @DisplayName("Test getSpot")
    @ParameterizedTest(name = "The spot should have x={0} & y={1}")
    @CsvSource({ "0,0", "144,144", "10,76", "122,44" })
    public void testGetSpot(int x, int y) {
        Tile tile = board.getTile(x, y);
        assertEquals(null, tile, "tile should be null");
    }

    @DisplayName("Test get invalid spot")
    @ParameterizedTest(name = "The spot x={0} & y={1} shouldn't be accessed")
    @CsvSource({ "-1,0", "145,144", "10,-7", "122,149" })
    public void testGetInvalidSpot() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> board.getTile(-1, -1));
        assertEquals("Coordinates can't exceed board size", exception.getMessage());
    }

    @DisplayName("Test UpdateSpot")
    @Test
    public void testUpdateSpot() {
        List<Spot> spots = board.getFreeSpots();
        assertEquals(1, spots.size(), "List should only contain the middle spot");
        assertEquals(new Spot(72, 72), spots.getFirst());
    }

}
