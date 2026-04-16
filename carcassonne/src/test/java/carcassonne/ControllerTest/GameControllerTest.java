package carcassonne.ControllerTest;

import carcassonne.Controller.GameController;
import carcassonne.Model.*;
import carcassonne.View.GameView;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private GameController controller;

    private GameView fakeView;

    @BeforeEach

    void setUp() throws Exception {

        // 1. Reset Singleton Instance via Reflection

        Field instanceField = GameController.class.getDeclaredField("instance");

        instanceField.setAccessible(true);

        instanceField.set(null, null);

        controller = GameController.getInstance();

        // 2. Initialize our Fake View

        fakeView = new GameView();

        controller.setView(fakeView);

    }

    @Test

    void testSingletonUniqueness() {

        GameController secondInstance = GameController.getInstance();

        assertSame(controller, secondInstance, "Both instances should be the same");

    }

    @Test
    void testUserManagement() {
        User user = new User(1, "TestUser");
        controller.setCurrentUser(user);

        assertEquals("TestUser", controller.getCurrentUser().getUsername());
    }

    @Test
    void testMaxPlayers() {
        controller.setMaxPlayer(2);

        assertEquals(2, controller.getMaxPlayers());
    }

    @Test
    void testPlayers() {
        controller.setMaxPlayer(2);

        assertEquals(2, controller.getPlayers().length);
    }
}
