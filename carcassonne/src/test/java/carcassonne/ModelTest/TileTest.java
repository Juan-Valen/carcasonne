package carcassonne.ModelTest;

import carcassonne.Model.Tile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class TileTest {
    Tile tile = new Tile(0, new int[]{1, 2, 3, 4});

    @ParameterizedTest(name = "The tile side {0} is {1}")
    @CsvSource({ "0, 1", "2, 3", "3, 4" })
    public void testGetSideType(int input, int expectedOutput)
    {
        assertEquals(expectedOutput, tile.getSideType(input), "Wrong side for " + expectedOutput);
    }

    @ParameterizedTest(name = "The tile side {0} should throw exception")
    @CsvSource({ "-1", "5", "10000" })
    public void testSideOutOfRange(int input)
    {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> tile.getSideType(input));
        assertEquals("Side must be in range 0 to 3", exception.getMessage());
    }

    @DisplayName("Test tile rotation")
    @Test
    public void testRotateTile()
    {
        tile.rotateTile(true);
        assertEquals(2,tile.getSideType(0), "Wrong rotation");
        tile.rotateTile(false);
        tile.rotateTile(false);
        assertEquals(4,tile.getSideType(0), "Wrong rotation");
    }

}
