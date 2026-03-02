package carcassonne.UI;

import carcassonne.controller.GameController;
import carcassonne.controller.GameController.Cell;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Data model for a single cell in the grid.
 * Stores the selection state independently of the visual representation.
 */
class CellData {
    public boolean isSelected = false;

    public CellData() {
    }

    public CellData(boolean isSelected) {
        this.isSelected = isSelected;
    }
}

public class GameView extends View {

    // Reference to game controller
    private final GameController gameController = GameController.getInstance();

    int gridSize = 144;
    private boolean layoutRetryScheduled = false;
    private double cellPortionOfScreen = 0.1; // Portion of the screen width that the one cell in the grid takes up (0.0 to 1.0)

    // Drag detection fields
    private double dragStartX = 0;
    private double dragStartY = 0;
    private static final double DRAG_THRESHOLD = 5.0; // Pixels; if mouse moves more than this, it's a drag

    // Multiple cell selection tracking
    private Set<Pane> selectedCells = new HashSet<>(); // All currently selected cells
    private GridPane currentGameGrid = null; // Reference to current grid

    // Viewport-based rendering (virtual scrolling)
    private double cellSize = 0;
    private Map<Cell, Pane> visibleCells = new HashMap<>(); // Map of Cell to Pane for currently visible cells
    private int lastRenderedMinRow = -1;
    private int lastRenderedMaxRow = -1;
    private int lastRenderedMinCol = -1;
    private int lastRenderedMaxCol = -1;
    private static final int RENDER_BUFFER = 2; // Extra cells to render outside viewport for smoother scrolling

    // Cell state persistence (survives when cell goes off-screen)
    private Map<Cell, CellData> allCellStates = new HashMap<>(); // Persistent state for all cells (Cell -> CellData)

    // Scroll constraints based on selected tiles
    private int minSelectedRow = Integer.MAX_VALUE;
    private int maxSelectedRow = Integer.MIN_VALUE;
    private int minSelectedCol = Integer.MAX_VALUE;
    private int maxSelectedCol = Integer.MIN_VALUE;

    @FXML
    public ScrollPane gridScreen;

    @Override
    protected void onAfterStageAvailable() {
        System.out.println("GameView.onAfterStageAvailable() called");
        initGrid();
    }

    /**
     * Builds the grid. Called automatically after the view is added to a stage.
     * Uses viewport-based rendering to only create cells visible on screen.
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

        cellSize = totalWidth * cellPortionOfScreen; // Adjust cell size based on window width

        System.out.println("Calculated cellSize=" + cellSize + " (totalWidth=" + totalWidth + ")");

        // Initialize all column and row constraints upfront
        for (int i = 0; i < gridSize; i++) {
            gameGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
            gameGrid.getRowConstraints().add(new RowConstraints(cellSize));
        }

        // Set a reasonable pref size for the grid so ScrollPane can compute viewport
        gameGrid.setPrefWidth(gridSize * cellSize);
        gameGrid.setPrefHeight(gridSize * cellSize);

        // Store grid reference for later use
        this.currentGameGrid = gameGrid;

        if (gridScreen != null) {

            gridScreen.setContent(gameGrid);

            // Center the ScrollPane after the content/layout is applied
            Platform.runLater(() -> {
                try {
                    System.out.println("Setting ScrollPane center");
                    gridScreen.setHvalue(0.5);
                    gridScreen.setVvalue(0.5);
                    System.out.println("Grid content set: " + (gridScreen.getContent() != null));

                    // Initial render of visible cells
                    updateVisibleCells();

                    // Listen for scroll changes to update visible cells
                    addScrollListeners();
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

    /**
     * Gets the set of all currently selected cells.
     *
     * @return a Set containing all selected Pane cells
     */
    public Set<Pane> getSelectedCells() {
        return new HashSet<>(selectedCells);
    }

    /**
     * Gets the number of currently selected cells.
     *
     * @return the count of selected cells
     */
    public int getSelectedCellCount() {
        return selectedCells.size();
    }

    /**
     * Checks if a cell is currently selected.
     *
     * @param cell the Pane to check
     * @return true if the cell is selected, false otherwise
     */
    public boolean isCellSelected(Pane cell) {
        return selectedCells.contains(cell);
    }

    /**
     * Clears all selected cells (removes light blue background).
     */
    public void clearSelection() {
        for (Pane cell : selectedCells) {
            cell.setStyle("");
        }
        selectedCells.clear();
    }

    /**
     * Selects a specific cell programmatically.
     *
     * @param cell the Pane to select
     */
    public void selectCell(Pane cell) {
        if (cell != null && !selectedCells.contains(cell)) {
            selectedCells.add(cell);
            cell.setStyle("-fx-background-color: lightblue;");
        }
    }

    /**
     * Deselects a specific cell programmatically.
     *
     * @param cell the Pane to deselect
     */
    public void deselectCell(Pane cell) {
        if (cell != null && selectedCells.contains(cell)) {
            selectedCells.remove(cell);
            cell.setStyle("");
        }
    }

    /**
     * Adds listeners for scroll events to update which cells are visible.
     */
    private void addScrollListeners() {
        if (gridScreen == null) {
            return;
        }

        // Listen for horizontal scroll
        gridScreen.hvalueProperty().addListener((obs, oldVal, newVal) -> {
            // Check if scroll constraints should be enforced
            if (minSelectedRow != Integer.MAX_VALUE) {
                enforceScrollConstraints();
            }
            updateVisibleCells();
        });

        // Listen for vertical scroll
        gridScreen.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            // Check if scroll constraints should be enforced
            if (minSelectedRow != Integer.MAX_VALUE) {
                enforceScrollConstraints();
            }
            updateVisibleCells();
        });

        // Listen for viewport size changes (window resize)
        gridScreen.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            updateVisibleCells();
        });
    }

    /**
     * Updates which cells are visible based on the current scroll position.
     * Only creates Pane objects for cells that are in the visible viewport
     * (plus a buffer for smoother scrolling).
     */
    private void updateVisibleCells() {
        if (gridScreen == null || currentGameGrid == null || cellSize <= 0) {
            System.out.println("updateVisibleCells() - early return: gridScreen=" + (gridScreen != null) +
                " currentGameGrid=" + (currentGameGrid != null) + " cellSize=" + cellSize);
            return;
        }

        javafx.geometry.Bounds viewportBounds = gridScreen.getViewportBounds();
        if (viewportBounds == null || viewportBounds.getWidth() <= 0 || viewportBounds.getHeight() <= 0) {
            System.out.println("updateVisibleCells() - invalid viewport: " + viewportBounds);

            // Fallback: render center cells if viewport not ready
            if (visibleCells.isEmpty() && lastRenderedMinRow == -1) {
                System.out.println("updateVisibleCells() - rendering fallback cells");
                renderCellRange(70, 75, 70, 75);
                Platform.runLater(() -> {
                    System.out.println("updateVisibleCells() - retrying");
                    updateVisibleCells();
                });
            }
            return;
        }

        // Get current scroll position
        double gridWidth = currentGameGrid.getPrefWidth();
        double gridHeight = currentGameGrid.getPrefHeight();
        double viewportWidth = viewportBounds.getWidth();
        double viewportHeight = viewportBounds.getHeight();

        // Calculate pixel offset of the top-left corner of the viewport within the grid
        // Scroll value times the maximum scrollable distance (total grid size minus viewport size)

        // Current scroll offset in pixels from the left edge of the grid
        double scrollOffsetX = gridScreen.getHvalue() * Math.max(0, gridWidth - viewportWidth);

        // Current scroll offset in pixels from the top edge of the grid
        double scrollOffsetY = gridScreen.getVvalue() * Math.max(0, gridHeight - viewportHeight);

        // Calculate which rows and columns are visible (with buffer)

        // Pixels from top of grid to top of viewport, divided by cell size gives the index of the topmost visible row
        // We can show partial cells, so we use floor for min and ceil for max to ensure we include any partially visible cells
        int minRow = Math.max(0, (int) Math.floor(scrollOffsetY / cellSize) - RENDER_BUFFER);
        int maxRow = Math.min(gridSize - 1, (int) Math.ceil((scrollOffsetY + viewportHeight) / cellSize) + RENDER_BUFFER);
        int minCol = Math.max(0, (int) Math.floor(scrollOffsetX / cellSize) - RENDER_BUFFER);
        int maxCol = Math.min(gridSize - 1, (int) Math.ceil((scrollOffsetX + viewportWidth) / cellSize) + RENDER_BUFFER);

        // If the visible range hasn't changed significantly, skip update
        if (lastRenderedMinRow == minRow && lastRenderedMaxRow == maxRow &&
                lastRenderedMinCol == minCol && lastRenderedMaxCol == maxCol) {
            return;
        }

        System.out.println("updateVisibleCells() - new range: rows [" + minRow + "-" + maxRow + "] cols [" + minCol + "-" + maxCol + "]");
        renderCellRange(minRow, maxRow, minCol, maxCol);
    }

    /**
     * Helper method to render cells in a specific range and remove cells outside that range.
     */
    private void renderCellRange(int minRow, int maxRow, int minCol, int maxCol) {
        System.out.println("renderCellRange() - rendering: rows [" + minRow + "-" + maxRow + "] cols [" + minCol + "-" + maxCol + "]");
        System.out.println("renderCellRange() - gridPane has " + currentGameGrid.getChildren().size() + " children before update");

        // Remove cells that are no longer visible
        Set<Cell> newVisibleKeys = new HashSet<>();
        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minCol; col <= maxCol; col++) {
                newVisibleKeys.add(new Cell(row, col));
            }
        }

        visibleCells.entrySet().removeIf(entry -> {
            if (!newVisibleKeys.contains(entry.getKey())) {
                try {
                    currentGameGrid.getChildren().remove(entry.getValue());
                } catch (Exception e) {
                    System.out.println("Error removing cell: " + e.getMessage());
                }
                return true;
            }
            return false;
        });

        // Add new cells that are now visible
        int cellsAdded = 0;
        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minCol; col <= maxCol; col++) {
                Cell cellKey = new Cell(row, col);
                if (!visibleCells.containsKey(cellKey)) {
                    try {
                        Pane cellPane = createCell(row, col);
                        visibleCells.put(cellKey, cellPane);
                        currentGameGrid.add(cellPane, col, row);
                        cellsAdded++;
                        if (cellsAdded <= 5) {  // Log first few additions
                            System.out.println("  Added cell at [" + row + "," + col + "]");
                        }
                    } catch (Exception e) {
                        System.out.println("Error adding cell at [" + row + "," + col + "]: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        // Update tracking
        lastRenderedMinRow = minRow;
        lastRenderedMaxRow = maxRow;
        lastRenderedMinCol = minCol;
        lastRenderedMaxCol = maxCol;

        System.out.println("renderCellRange() - added " + cellsAdded + " cells, total visible: " + visibleCells.size() + ", gridPane now has " + currentGameGrid.getChildren().size() + " children");
    }

    /**
     * Recalculates the scroll constraints based on currently selected tiles.
     * Finds the minimum and maximum row/column of all selected cells.
     */
    private void updateScrollConstraints() {
        minSelectedRow = Integer.MAX_VALUE;
        maxSelectedRow = Integer.MIN_VALUE;
        minSelectedCol = Integer.MAX_VALUE;
        maxSelectedCol = Integer.MIN_VALUE;

        // Find the bounds of all selected cells
        for (Map.Entry<Cell, CellData> entry : allCellStates.entrySet()) {
            CellData data = entry.getValue();
            if (data.isSelected) {
                Cell cell = entry.getKey();
                minSelectedRow = Math.min(minSelectedRow, cell.row);
                maxSelectedRow = Math.max(maxSelectedRow, cell.row);
                minSelectedCol = Math.min(minSelectedCol, cell.col);
                maxSelectedCol = Math.max(maxSelectedCol, cell.col);
            }
        }

        if (minSelectedRow == Integer.MAX_VALUE) {
            System.out.println("updateScrollConstraints() - no cells selected, constraints disabled");
        } else {
            System.out.println("updateScrollConstraints() - Selected tile bounds: rows [" + minSelectedRow + "-" + maxSelectedRow +
                "] cols [" + minSelectedCol + "-" + maxSelectedCol + "]");
        }
    }

    /**
     * Enforces scroll constraints to prevent scrolling more than one tile beyond the selected tiles.
     * Clamps the scroll position to allow viewing only one tile beyond the edge of selected tiles.
     */
    private void enforceScrollConstraints() {
        if (gridScreen == null || currentGameGrid == null || cellSize <= 0) {
            return;
        }

        // If no cells are selected, allow free scrolling
        if (minSelectedRow == Integer.MAX_VALUE) {
            return;
        }

        javafx.geometry.Bounds viewportBounds = gridScreen.getViewportBounds();
        if (viewportBounds == null || viewportBounds.getWidth() <= 0 || viewportBounds.getHeight() <= 0) {
            return;
        }

        double gridWidth = currentGameGrid.getPrefWidth();
        double gridHeight = currentGameGrid.getPrefHeight();
        double viewportWidth = viewportBounds.getWidth();
        double viewportHeight = viewportBounds.getHeight();

        // Calculate the actual scrollable range
        double maxScrollPixelX = gridWidth - viewportWidth;
        double maxScrollPixelY = gridHeight - viewportHeight;

        if (maxScrollPixelX <= 0 || maxScrollPixelY <= 0) {
            // Grid fits in viewport, no scrolling needed
            return;
        }

        // Calculate pixel boundaries for the allowed viewing region
        // We can show one cell before minSelected and one cell after maxSelected

        // LEFT boundary: Can scroll to show one cell before minSelectedCol
        // This means the left edge of viewport can be at (minSelectedCol - 1) * cellSize
        double minPixelX = Math.max(0, (minSelectedCol - 1) * cellSize);

        // RIGHT boundary: Can scroll to show one cell after maxSelectedCol
        // The RIGHT edge of viewport should not go beyond (maxSelectedCol + 2) * cellSize
        // So the LEFT edge of viewport cannot be further than (maxSelectedCol + 2) * cellSize - viewportWidth
        double maxPixelX = Math.min(maxScrollPixelX, (maxSelectedCol + 2) * cellSize - viewportWidth);

        // TOP boundary: Can scroll to show one cell before minSelectedRow
        double minPixelY = Math.max(0, (minSelectedRow - 1) * cellSize);

        // BOTTOM boundary: Can scroll to show one cell after maxSelectedRow
        // The BOTTOM edge of viewport should not go beyond (maxSelectedRow + 2) * cellSize
        // So the TOP edge of viewport cannot be further than (maxSelectedRow + 2) * cellSize - viewportHeight
        double maxPixelY = Math.min(maxScrollPixelY, (maxSelectedRow + 2) * cellSize - viewportHeight);

        // Convert pixel values to scroll values (0 to 1)
        double minHvalue = minPixelX / maxScrollPixelX;
        double maxHvalue = maxPixelX / maxScrollPixelX;
        double minVvalue = minPixelY / maxScrollPixelY;
        double maxVvalue = maxPixelY / maxScrollPixelY;

        // Get current scroll position
        double currentHvalue = gridScreen.getHvalue();
        double currentVvalue = gridScreen.getVvalue();

        // Clamp to allowed range
        double constrainedHvalue = Math.max(minHvalue, Math.min(currentHvalue, maxHvalue));
        double constrainedVvalue = Math.max(minVvalue, Math.min(currentVvalue, maxVvalue));

        System.out.println("Scroll constraint check:");
        System.out.println("  Current: H=" + String.format("%.3f", currentHvalue) + " V=" + String.format("%.3f", currentVvalue));
        System.out.println("  Allowed: H[" + String.format("%.3f", minHvalue) + "-" + String.format("%.3f", maxHvalue) + "]" +
                         " V[" + String.format("%.3f", minVvalue) + "-" + String.format("%.3f", maxVvalue) + "]");
        System.out.println("  Selected cells: rows [" + minSelectedRow + "-" + maxSelectedRow + "] cols [" + minSelectedCol + "-" + maxSelectedCol + "]");
        System.out.println("  Pixel constraints: X[" + String.format("%.1f", minPixelX) + "-" + String.format("%.1f", maxPixelX) + "]" +
                         " Y[" + String.format("%.1f", minPixelY) + "-" + String.format("%.1f", maxPixelY) + "]");

        // Apply constraints by setting clamped values
        if (constrainedHvalue != currentHvalue) {
            System.out.println("  Clamping H from " + String.format("%.3f", currentHvalue) + " to " + String.format("%.3f", constrainedHvalue));
            gridScreen.setHvalue(constrainedHvalue);
        }
        if (constrainedVvalue != currentVvalue) {
            System.out.println("  Clamping V from " + String.format("%.3f", currentVvalue) + " to " + String.format("%.3f", constrainedVvalue));
            gridScreen.setVvalue(constrainedVvalue);
        }
    }

    /**
     * Creates a single cell with all its event handlers.
     * Restores the selection state from persistent storage.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     * @return a Pane representing one grid cell
     */
    private Pane createCell(int row, int col) {
        Pane cell = new Pane();
        cell.setPrefSize(cellSize, cellSize);
        cell.setMinSize(cellSize, cellSize);
        cell.setMaxSize(cellSize, cellSize);

        // Get or create persistent state for this cell
        Cell cellKey = new Cell(row, col);
        CellData cellData = allCellStates.computeIfAbsent(cellKey, k -> new CellData());

        // Check if this cell has a tile placed (from GameController)
        boolean isTilePlaced = gameController.getPlacedTiles().contains(cellKey);

        // Check if this cell is placeable (from GameController)
        boolean isPlaceable = gameController.getPlaceableCells().contains(cellKey);

        // Set visual style based on cell state
        if (isTilePlaced) {
            // Tile is placed - show as occupied (green background)
            cell.setStyle("-fx-background-color: lightgreen; -fx-border-color: gray;");
            cellData.isSelected = true;
            selectedCells.add(cell);
        } else if (isPlaceable) {
            // Cell is placeable - show as available (yellow background)
            cell.setStyle("-fx-background-color: lightyellow; -fx-border-color: gray;");
        } else if (cellData.isSelected) {
            // Restore selection state from persistent storage (light blue)
            cell.setStyle("-fx-background-color: lightblue;");
            selectedCells.add(cell);
        } else {
            // Default empty cell
            cell.setStyle("-fx-border-color: lightgray;");
        }

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
                System.out.println("Cell clicked at [" + row + "," + col + "]");

                // Check if tile is already placed
                if (gameController.getPlacedTiles().contains(cellKey)) {
                    System.out.println("Tile already placed at [" + row + "," + col + "]");
                    return;
                }

                // Check if this is a valid placement location
                if (!isPlaceable && !gameController.getPlacedTiles().isEmpty()) {
                    System.out.println("Cannot place tile at [" + row + "," + col + "] - not adjacent to existing tiles");
                    return;
                }

                // Place tile using GameController
                gameController.placeTile(row, col);
                System.out.println("Tile placed at [" + row + "," + col + "]");

                // Update visual state
                selectedCells.add(cell);
                cell.setStyle("-fx-background-color: lightgreen; -fx-border-color: gray;");
                cellData.isSelected = true;

                // Refresh all visible cells to update placeable highlights
                refreshVisibleCells();

                // Update scroll constraints based on new selection
                updateScrollConstraints();
                enforceScrollConstraints();
            }
        });

        return cell;
    }

    /**
     * Refreshes all currently visible cells to update their visual state
     * (e.g., after placing a tile, to update which cells are now placeable).
     */
    private void refreshVisibleCells() {
        // Clear current visible cells and re-render the same range
        if (lastRenderedMinRow != -1) {
            // Store current range
            int minRow = lastRenderedMinRow;
            int maxRow = lastRenderedMaxRow;
            int minCol = lastRenderedMinCol;
            int maxCol = lastRenderedMaxCol;

            // Clear visible cells
            for (Pane pane : visibleCells.values()) {
                currentGameGrid.getChildren().remove(pane);
            }
            visibleCells.clear();

            // Re-render the same range
            renderCellRange(minRow, maxRow, minCol, maxCol);
        }
    }
}
