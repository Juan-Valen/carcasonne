package carcassonne.ModelTest;

import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;
import carcassonne.Model.GameInfo;

class GameInfoTest {

    @Test
    void testFieldStorage() {
        GameInfo info = new GameInfo();
        int testId = 101;
        boolean isOnline = true;
        Date testDate = new Date(System.currentTimeMillis());

        info.id = testId;
        info.online = isOnline;
        info.updatedDate = testDate;

        // Assert
        assertEquals(testId, info.id, "ID should match the assigned value");
        assertTrue(info.online, "Online status should be true");
        assertEquals(testDate, info.updatedDate, "Date should match the assigned value");
    }

    @Test
    void testDefaultValues() {
        GameInfo info = new GameInfo();

        // Check standard Java defaults for primitive/object fields
        assertEquals(0, info.id, "Default ID should be 0");
        assertFalse(info.online, "Default online status should be false");
        assertNull(info.updatedDate, "Default date should be null");
    }

    @Test
    void testNullDateHandling() {
        GameInfo info = new GameInfo();

        info.updatedDate = null;
        // Assert
        assertNull(info.updatedDate, "The class should allow setting the date to null");

    }

}
