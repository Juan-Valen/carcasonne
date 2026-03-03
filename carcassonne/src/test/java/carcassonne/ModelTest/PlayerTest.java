package carcassonne.ModelTest;

import carcassonne.DataType.Color;
import carcassonne.Model.Player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest
{
    Player player = new Player(Color.RED, "user", 0);

    @DisplayName("Test gets")
    @Test
    public void testGets()
    {
        assertEquals(Color.RED, player.getColor(), "Wrong color");
        assertEquals("user", player.getUser(), "Wrong user");
        assertEquals(0, player.getPoints(), "Wrong amount of points");
    }

    @DisplayName("Test addPoints")
    @ParameterizedTest(name = "Adds {0} and {1} and expects there sum")
    @CsvSource({ "100, 250", "500, 700", "255, 145" })
    public void testAddPoints(int input1, int input2)
    {
        player.addPoints(input1);
        assertEquals(input1, player.getPoints(), "Incorrect number of points");
        player.addPoints(input2);
        assertEquals(input1 + input2, player.getPoints(), "Incorrect number of points");
    }

}
