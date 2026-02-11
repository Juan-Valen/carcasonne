package carcassonne.UI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class GameView extends View{

    int gridSize = 144;
    private boolean layoutRetryScheduled = false;
    private double cellPortionOfScreen = 0.1; // Portion of the screen width that the one cell in the grid takes up (0.0 to 1.0)

    // Drag detection fields
    private double dragStartX = 0;
    private double dragStartY = 0;
    private static final double DRAG_THRESHOLD = 5.0; // Pixels; if mouse moves more than this, it's a drag


    @FXML
    public ScrollPane gridScreen;

    @Override
    protected void onAfterStageAvailable() {
        System.out.println("GameView.onAfterStageAvailable() called");
        initGrid();
    }

    /**
     * Builds the grid. Called automatically after the view is added to a stage.
     */
    private void initGrid() {
        System.out.println("initGrid() called, gridSize=" + gridSize);
        GridPane gameGrid = new GridPane();

        gameGrid.gridLinesVisibleProperty().setValue(true);

        double totalWidth = Double.NaN; // start unknown
        try {
            // Get width from the scene's window
            if (gridScreen != null && gridScreen.getScene() != null && gridScreen.getScene().getWindow() != null) {
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

        double cellSize = totalWidth * cellPortionOfScreen; // Adjust cell size based on window width

        System.out.println("Calculated cellSize=" + cellSize + " (totalWidth=" + totalWidth + ")");

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Pane cell = new Pane();
                cell.setPrefSize(cellSize, cellSize); // Set preferred size for each cell

                // Track mouse press to detect if this is a drag or a click
                cell.setOnMousePressed(event -> {
                    dragStartX = event.getSceneX();
                    dragStartY = event.getSceneY();
                });

                // Only trigger click action if it wasn't a drag
                cell.setOnMouseClicked(event -> {
                    double dragDistance = Math.hypot(
                        event.getSceneX() - dragStartX,
                        event.getSceneY() - dragStartY
                    );

                    // Only fire click action if drag distance is less than threshold
                    if (dragDistance < DRAG_THRESHOLD) {
                        cell.setStyle("-fx-background-color: lightblue;");
                    }
                });

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
            carcassonne.MainApp.getInstance().showScene("/StartView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
