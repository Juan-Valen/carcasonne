package carcassonne.UI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameView {

    int gridSize = 144;
    private Stage primaryStage;
    private boolean layoutRetryScheduled = false;

    @FXML
    public ScrollPane gridScreen;

    // Called by FXMLLoader after @FXML injections
    @FXML
    public void initialize() {
        System.out.println("GameView.initialize() called");

        // Defer heavy work until the scene is shown or until MainApp calls initGrid()
        Platform.runLater(() -> {
            System.out.println("Platform.runLater in initialize(): gridScreen=" + (gridScreen != null));
            if (gridScreen != null && gridScreen.getContent() == null) {
                // If the primary stage was already set by the launcher, use it; otherwise initGrid will still work.
                initGrid();
            }
        });
    }

    // Optional setter from the application so controller can access stage metrics
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    // Public entry to (re)build the grid; safe to call after the scene is shown
    public void initGrid() {
        System.out.println("initGrid() called, gridSize=" + gridSize + ", primaryStage=" + (primaryStage != null));
        GridPane gameGrid = new GridPane();

        gameGrid.gridLinesVisibleProperty().setValue(true);

        double totalWidth = Double.NaN; // start unknown
        try {
            if (primaryStage != null) {
                totalWidth = primaryStage.getWidth();
            }
            if (Double.isNaN(totalWidth) && gridScreen != null && gridScreen.getScene() != null && gridScreen.getScene().getWindow() != null) {
                totalWidth = gridScreen.getScene().getWindow().getWidth();
            }
            // Also try ScrollPane viewport width if available
            if (Double.isNaN(totalWidth) && gridScreen != null && gridScreen.getViewportBounds() != null) {
                totalWidth = gridScreen.getViewportBounds().getWidth();
            }
        } catch (Exception ignored) {
        }

        // If we still don't have a finite width, schedule one retry on the FX thread and return
        if (!Double.isFinite(totalWidth) || totalWidth <= 0) {
            System.out.println("Stage/viewport width not ready yet (" + totalWidth + "); scheduling a retry");
            if (!layoutRetryScheduled) {
                layoutRetryScheduled = true;
                Platform.runLater(() -> {
                    layoutRetryScheduled = false;
                    initGrid();
                });
            }
            return;
        }

        double cellSize = totalWidth / 4.0; // Adjust cell size based on window width

        System.out.println("Calculated cellSize=" + cellSize + " (totalWidth=" + totalWidth + ")");

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Pane cell = new Pane();
                cell.setPrefSize(cellSize, cellSize); // Set preferred size for each cell
                // Make cells visible during debugging: light border and white background
                cell.setStyle("-fx-border-color: lightgray; -fx-background-color: white;");
                gameGrid.add(cell, col, row);
            }
        }

        // set a reasonable pref size for the grid so ScrollPane can compute viewport
        gameGrid.setPrefWidth(gridSize * cellSize);
        gameGrid.setPrefHeight(gridSize * cellSize);

        if (gridScreen != null) {

            gridScreen.setContent(gameGrid);

            // Center the ScrollPane after the content/layout is applied
            Platform.runLater(() -> {
                try {
                    System.out.println("Setting ScrollPane center");
                    gridScreen.setHvalue(0.5);
                    gridScreen.setVvalue(0.5);
                    System.out.println("Grid content set: " + (gridScreen.getContent() != null));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else {
            System.out.println("Cannot set content because gridScreen is null");
        }
    }

    @FXML
    public void openSecondary() {
        try {
            carcassonne.MainApp.getInstance().showScene("/SecondaryView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
