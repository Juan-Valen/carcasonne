package carcassonne.ModelTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import carcassonne.Model.AvailableSpots;
import carcassonne.Model.Spot;

import static org.junit.jupiter.api.Assertions.*;

public class AvailableSpotsTest {

    @Test
    @DisplayName("Test initialization and direction retrieval")
    void testInitialization() {
        AvailableSpots availableSpots = new AvailableSpots("North");
        assertEquals("North", availableSpots.getDirection());
        assertTrue(availableSpots.getSpots().isEmpty(), "Spot list should be empty initially");
    }

    @Test
    @DisplayName("Test adding and retrieving spots")
    void testAddingSpots() {
        AvailableSpots availableSpots = new AvailableSpots("East");
        Spot spot1 = new Spot(1, 0);
        Spot spot2 = new Spot(2, 0);

        availableSpots.add(spot1);
        availableSpots.add(spot2);

        assertEquals(2, availableSpots.getSpots().size());
        assertTrue(availableSpots.getSpots().contains(spot1));
        assertEquals(spot1, availableSpots.getSpots().get(0));
    }

    @Test
    @DisplayName("Test clear functionality")
    void testClear() {
        AvailableSpots availableSpots = new AvailableSpots("South");
        availableSpots.add(new Spot(0, 1));
        availableSpots.add(new Spot(0, 2));

        assertFalse(availableSpots.getSpots().isEmpty());
        availableSpots.clear();
        assertTrue(availableSpots.getSpots().isEmpty(), "Spot list should be empty after clear()");
    }
}
