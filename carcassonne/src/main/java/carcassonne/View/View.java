package carcassonne.View;

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
        System.out.println("View.initialize() called for " + this.getClass().getSimpleName());
        System.out.println("  rootContainer = " + rootContainer);

        if (rootContainer != null) {
            System.out.println("  rootContainer.getScene() = " + rootContainer.getScene());

            // Try immediate binding if scene is already available
            if (rootContainer.getScene() != null && rootContainer.getScene().getWindow() != null) {
                System.out.println("  Scene already available! Calling setupStageBindings immediately");
                setupStageBindings(rootContainer.getScene().getWindow());
                return;
            }

            // Listen for when this view is added to a scene, then set up stage bindings
            rootContainer.sceneProperty().addListener((observable, oldScene, newScene) -> {
                System.out.println("View.initialize() - sceneProperty changed: oldScene=" + (oldScene != null)
                        + " newScene=" + (newScene != null));
                if (newScene != null && newScene.getWindow() != null) {
                    System.out.println("  Scene has window, calling setupStageBindings");
                    setupStageBindings(newScene.getWindow());
                } else if (newScene != null) {
                    System.out.println("  Scene available but window not yet, listening for window");
                    // Window not yet available, listen for it
                    newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                        System.out.println("  windowProperty changed: newWindow=" + (newWindow != null));
                        if (newWindow != null && !initialized) {
                            setupStageBindings(newWindow);
                        }
                    });
                }
            });
        } else {
            System.out.println("  ERROR: rootContainer is null!");
        }
    }

    protected void setupStageBindings(Window window) {
        System.out.println("View.setupStageBindings() called for " + this.getClass().getSimpleName());

        if (initialized) {
            System.out.println("  Already initialized, returning early");
            return; // Already initialized
        }
        initialized = true;
        System.out.println("  Set initialized = true");

        System.out.println("View.setupStageBindings() - binding rootContainer to screen size with portionOfScreen="
                + portionOfScreen);

        // Bind to screen size, not stage size, to prevent progressive shrinking when
        // switching views
        if (window instanceof Stage stage) {
            Screen screen = Screen.getPrimary();
            Rectangle2D screenBounds = screen.getVisualBounds();

            if (rootContainer instanceof Region region) {
                double screenWidth = screenBounds.getWidth();
                double screenHeight = screenBounds.getHeight();

                region.setPrefWidth(screenWidth * portionOfScreen);
                region.setPrefHeight(screenHeight * portionOfScreen);

                System.out.println("  Bound rootContainer to screen size: " + screenWidth + "x" + screenHeight);
            }
        }

        // Call onAfterStageAvailable on the FX thread to ensure layout is ready
        System.out.println("  Scheduling onAfterStageAvailable via Platform.runLater");
        Platform.runLater(() -> {
            System.out.println("  Platform.runLater executing - calling onAfterStageAvailable()");
            this.onAfterStageAvailable();
        });
    }

    protected void onAfterStageAvailable() {
        System.out.println("View.onAfterStageAvailable() called - subclasses can override this");
        // Subclasses can override this to perform additional setup after the stage is
        // available
    }

    public void onViewShow() {
        System.out.println("View.onViewShow() called for " + this.getClass().getSimpleName());
        // Subclasses can override this to perform actions when the view is shown
    }
}
