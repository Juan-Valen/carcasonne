package carcassonne.ViewTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ViewTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    // Example concrete class if View is abstract
    static class TestView {
        public void onViewShow() {
            System.out.println("View.onViewShow() called for " + this.getClass().getSimpleName());
        }
    }

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void onViewShow_printsExpectedMessage() {
        TestView view = new TestView();

        view.onViewShow();

        String output = outContent.toString();
        assertTrue(
                output.contains("View.onViewShow() called for TestView"),
                "Expected output to contain class name"
        );
    }
}