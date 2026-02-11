package carcassonne.UI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

public class View {

    @FXML
    protected Parent rootContainer;

    protected boolean initialized = false;
    protected double portionOfScreen = 0.8;

    @FXML
    protected void initialize() {
        System.out.println("View.initialize() called");
        // Listen for when this view is added to a scene, then set up stage bindings
        rootContainer.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null && newScene.getWindow() != null) {
                setupStageBindings(newScene.getWindow());
            } else if (newScene != null) {
                // Window not yet available, listen for it
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null && !initialized) {
                        setupStageBindings(newWindow);
                    }
                });
            }
        });
    }

    protected void setupStageBindings(Window window) {
        if (initialized) {
            return; // Already initialized
        }
        initialized = true;

        System.out.println("View.setupStageBindings() called, binding rootContainer to screen size with portionOfScreen=" + portionOfScreen);

        // Bind to screen size, not stage size, to prevent progressive shrinking when switching views
        if (window instanceof Stage stage) {
            Screen screen = Screen.getPrimary();
            Rectangle2D screenBounds = screen.getVisualBounds();

            if(rootContainer instanceof Region region) {
                double screenWidth = screenBounds.getWidth();
                double screenHeight = screenBounds.getHeight();

                region.setPrefWidth(screenWidth * portionOfScreen);
                region.setPrefHeight(screenHeight * portionOfScreen);

                System.out.println("Bound rootContainer to screen size: " + screenWidth + "x" + screenHeight);
            }
        }

        // Call onAfterStageAvailable on the FX thread to ensure layout is ready
        Platform.runLater(this::onAfterStageAvailable);
    }

    protected void onAfterStageAvailable() {
        System.out.println("View.onAfterStageAvailable() called - subclasses can override this");
        // Subclasses can override this to perform additional setup after the stage is available
    }

}



