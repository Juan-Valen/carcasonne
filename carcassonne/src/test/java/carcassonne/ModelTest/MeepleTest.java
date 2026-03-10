package carcassonne.ModelTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import carcassonne.Model.Meeple;

import static org.junit.jupiter.api.Assertions.*;

public class MeepleTest {

    @Test
    @DisplayName("Test Meeple initialization via constructor")
    void testConstructor() {
        Meeple meple = new Meeple(2);
        assertEquals(2, meple.getPlayerIndex(), "Player index should match constructor argument");
        assertEquals(-1, meple.getPosition(), "Initial position should be -1");
    }

    @Test
    @DisplayName("Test setters and getters for Meeple state")
    void testSettersAndGetters() {
        Meeple meple = new Meeple();

        meple.setPlayerIndex(5);
        meple.setPosition(10);

        assertEquals(5, meple.getPlayerIndex());
        assertEquals(10, meple.getPosition());
    }
}
