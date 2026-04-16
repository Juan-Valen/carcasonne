package carcassonne.DataTypeTest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import carcassonne.DataType.Color;

class ColorTest {

    @Test

    void testEnumValues() {

        // Verify all expected colors exist

        Color[] colors = Color.values();

        assertEquals(3, colors.length, "Enum should have exactly 3 colors");

        assertEquals(Color.RED, Color.valueOf("RED"));

        assertEquals(Color.GREEN, Color.valueOf("GREEN"));

        assertEquals(Color.BLUE, Color.valueOf("BLUE"));

    }

    @Test

    void testColorProperties() {

        // Test RED

        assertEquals("red", Color.RED.getName());

        assertEquals("#FF0000", Color.RED.getHexCode());

        // Test GREEN

        assertEquals("green", Color.GREEN.getName());

        assertEquals("#00FF00", Color.GREEN.getHexCode());

        // Test BLUE

        assertEquals("blue", Color.BLUE.getName());

        assertEquals("#0000FF", Color.BLUE.getHexCode());

    }

    @Test

    void testSerializable() {

        // Enums are inherently serializable, but we can verify the interface is present

        assertTrue(Color.RED instanceof java.io.Serializable);

    }

}
