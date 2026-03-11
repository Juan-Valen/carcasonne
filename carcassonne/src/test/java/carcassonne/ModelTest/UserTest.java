package carcassonne.ModelTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import carcassonne.Model.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    @DisplayName("Test User parameterized constructor")
    void testParameterizedConstructor() {
        User user = new User(1, "PlayerOne");

        assertEquals(1, user.getId(), "ID should match constructor value");
        assertEquals("PlayerOne", user.getUsername(), "Username should match constructor value");
    }

    @Test
    @DisplayName("Test User default constructor and setters")
    void testSetters() {
        User user = new User();

        user.setId(42);
        user.setUsername("CarcassonneMaster");

        assertEquals(42, user.getId());
        assertEquals("CarcassonneMaster", user.getUsername());
    }

    @Test
    @DisplayName("Test null username handling")
    void testNullUsername() {
        User user = new User();
        user.setUsername(null);

        assertNull(user.getUsername(), "Username should be able to hold null if required by game logic");
    }
}
