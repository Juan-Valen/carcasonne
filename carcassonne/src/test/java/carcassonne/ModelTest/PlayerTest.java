package carcassonne.ModelTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import carcassonne.Model.Meeple;
import carcassonne.Model.Player;
import carcassonne.Model.User;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    @DisplayName("Test Player point tracking and validation")
    void testPoints() {
        User user = new User(1, "TestUser");
        Player player = new Player(user);

        player.addPoints(10);
        assertEquals(10, player.getPoints());

        assertThrows(IllegalArgumentException.class, () -> {
            player.addPoints(-5);
        }, "Should throw exception when adding negative points");
    }

    @Test
    @DisplayName("Test Meeple initialization and placement")
    void testMeepleManagement() {
        User user = new User(1, "TestUser");
        // Initialize with 5 meeples
        Player player = new Player(user, 5);

        assertEquals(7, player.getMeepleCount());

        // Place one meple
        Meeple placed = player.placeMeeple();
        assertNotNull(placed);
        assertEquals(6, player.getMeepleCount());

        // Exhaust supply
        player.placeMeeple();
        player.placeMeeple();
        player.placeMeeple();
        player.placeMeeple();
        player.placeMeeple();
        player.placeMeeple();

        assertEquals(0, player.getMeepleCount());
        assertNull(player.placeMeeple(), "Should return null when no meeples are left");
    }

    @Test
    @DisplayName("Test adding a Meeple manually")
    void testAddMeeple() {
        Player player = new Player(new User(1, "TestUser"));
        player.addMeeple(new Meeple(1));

        assertEquals(1, player.getMeepleCount());
    }

    @Test
    void testGetUser() {
        Player player = new Player(new User(1, "TestUser"));
        assertEquals("TestUser", player.getUser().getUsername());
    }
}
