package carcassonne.UI;

import carcassonne.Controller.GameController;
import carcassonne.Model.Cell;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.*;

public class GameView extends View {

    // Reference to game controller
    private final GameController controller = GameController.getInstance();

    int gridSize = 145;
    private boolean layoutRetryScheduled = false;

    private double cellPortionOfScreen = 0.1; // Portion of the screen width that the one cell in the grid takes up when
                                              // using dynamic cell size instead of tile image size (0.0 to 1.0)

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

    // Flag to prevent infinite recursion when enforceScrollConstraints modifies
    // scroll values
    private boolean isEnforcingConstraints = false;

    Map<Character, Image> tileIdToImage = new HashMap<>(); // Cache for tile images based on tile ID
    // Initializer block to populate the tile image cache
    {
        tileIdToImage.put('A', loadImage("images/Base_Game_C3_Tile_A.png"));
        tileIdToImage.put('B', loadImage("images/Base_Game_C3_Tile_B.png"));
        tileIdToImage.put('C', loadImage("images/Base_Game_C3_Tile_C.png"));
        tileIdToImage.put('D', loadImage("images/Base_Game_C3_Tile_D.png"));
        tileIdToImage.put('E', loadImage("images/Base_Game_C3_Tile_E.png"));
        tileIdToImage.put('F', loadImage("images/Base_Game_C3_Tile_F.png"));
        tileIdToImage.put('G', loadImage("images/Base_Game_C3_Tile_G.png"));
        tileIdToImage.put('H', loadImage("images/Base_Game_C3_Tile_H.png"));
        tileIdToImage.put('I', loadImage("images/Base_Game_C3_Tile_I.png"));
        tileIdToImage.put('J', loadImage("images/Base_Game_C3_Tile_J.png"));
        tileIdToImage.put('K', loadImage("images/Base_Game_C3_Tile_K.png"));
        tileIdToImage.put('L', loadImage("images/Base_Game_C3_Tile_L.png"));
        tileIdToImage.put('M', loadImage("images/Base_Game_C3_Tile_M.png"));
        tileIdToImage.put('N', loadImage("images/Base_Game_C3_Tile_N.png"));
        tileIdToImage.put('O', loadImage("images/Base_Game_C3_Tile_O.png"));
        tileIdToImage.put('P', loadImage("images/Base_Game_C3_Tile_P.png"));
        tileIdToImage.put('Q', loadImage("images/Base_Game_C3_Tile_Q.png"));
        tileIdToImage.put('R', loadImage("images/Base_Game_C3_Tile_R.png"));
        tileIdToImage.put('S', loadImage("images/Base_Game_C3_Tile_S.png"));
        tileIdToImage.put('T', loadImage("images/Base_Game_C3_Tile_T.png"));
        tileIdToImage.put('U', loadImage("images/Base_Game_C3_Tile_U.png"));
        tileIdToImage.put('V', loadImage("images/Base_Game_C3_Tile_V.png"));
        tileIdToImage.put('W', loadImage("images/Base_Game_C3_Tile_W.png"));
        tileIdToImage.put('X', loadImage("images/Base_Game_C3_Tile_X.png"));
    }

    /**
     * Helper method to load an image from the classpath resources.
     * Uses the proper resource URL loading mechanism.
     */
    private Image loadImage(String resourcePath) {
        try {
            var resource = getClass().getResource("/" + resourcePath);
            if (resource != null) {
                return new Image(resource.toExternalForm());
            } else {
                System.out.println("WARNING: Resource not found: /" + resourcePath);
                return null;
            }
        } catch (Exception e) {
            System.out.println("ERROR loading image /" + resourcePath + ": " + e.getMessage());
            return null;
        }
    }

    @FXML
    public ScrollPane gridScreen;

    @FXML
    public StackPane nextTilePane;

    @Override
    protected void onAfterStageAvailable() {
        initGrid();
        controller.setView(this);
    }

    @FXML
    public void openSecondary() {
        try {
            carcassonne.MainApp.getInstance().showScene("/StartView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void rotateCurrentTile() {
        System.out.println("rotateCurrentTile() called");
        controller.rotateTile();
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

        // If we still don't have a finite width, schedule one retry on the FX thread
        // and return
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

        cellSize = tileIdToImage.get('A').getWidth(); // Use actual tile image width to determine cell size
        System.out.println("Initial cellSize based on tile image: " + cellSize);
        // cellSize = totalWidth * cellPortionOfScreen; // Adjust cell size based on
        // window width

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

                    // Check if scrolling should be enabled after initial render
                    updateScrollingState();

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

    /**
     * Adds listeners for scroll events to update which cells are visible.
     */
    private void addScrollListeners() {
        if (gridScreen == null) {
            return;
        }

        // Initially disable scrolling (no tiles placed yet)
        updateScrollingState();

        // Listen for horizontal scroll
        gridScreen.hvalueProperty().addListener((obs, oldVal, newVal) -> {
            // Skip if we're already enforcing constraints (prevents infinite recursion)
            if (isEnforcingConstraints) {
                return;
            }

            // Enforce constraints if tiles are placed (but don't disable panning)
            if (!controller.getPlacedTiles().isEmpty()) {
                enforceScrollConstraints();
            }
            updateVisibleCells();
        });

        // Listen for vertical scroll
        gridScreen.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            // Skip if we're already enforcing constraints (prevents infinite recursion)
            if (isEnforcingConstraints) {
                return;
            }

            // Enforce constraints if tiles are placed (but don't disable panning)
            if (!controller.getPlacedTiles().isEmpty()) {
                enforceScrollConstraints();
            }
            updateVisibleCells();
        });

        // Listen for viewport size changes (window resize)
        gridScreen.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            updateVisibleCells();
            updateScrollingState(); // Re-check if scrolling should be enabled after resize
        });
    }

    /**
     * Enables or disables scrolling based on whether tiles exceed the viewport.
     * Scrolling (panning) is enabled once tiles are placed.
     * Constraints are enforced separately when tiles fit within viewport.
     */
    public void updateScrollingState() {
        if (gridScreen == null) {
            return;
        }

        // Enable panning once tiles are placed (even if they span entire viewport)
        boolean hasTilesPlaced = !controller.getPlacedTiles().isEmpty();
        gridScreen.setPannable(hasTilesPlaced);
        System.out.println("updateScrollingState() - panning " + (hasTilesPlaced ? "ENABLED" : "DISABLED"));
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
        renderCellRange();
    }

    /**
     * Checks if the placeable tiles exceed the current viewport.
     * Returns true if placeable cells extend beyond ANY viewport edge.
     * Returns false if tiles span across the entire viewport (can't fit them all).
     */
    private boolean shouldEnforceScrollConstraints() {
        if (gridScreen == null || currentGameGrid == null || cellSize <= 0) {
            return false;
        }

        javafx.geometry.Bounds viewportBounds = gridScreen.getViewportBounds();
        if (viewportBounds == null || viewportBounds.getWidth() <= 0 || viewportBounds.getHeight() <= 0) {
            return false;
        }

        // Get the bounds of all placeable cells (including the first tile at center if
        // no tiles placed yet)
        int minPlaceableRow = Integer.MAX_VALUE;
        int maxPlaceableRow = Integer.MIN_VALUE;
        int minPlaceableCol = Integer.MAX_VALUE;
        int maxPlaceableCol = Integer.MIN_VALUE;

        Set<Cell> placeableCells = controller.getPlaceableCells();

        // If no tiles placed yet, the center cell is placeable
        if (controller.getPlacedTiles().isEmpty()) {
            int center = gridSize / 2;
            minPlaceableRow = center;
            maxPlaceableRow = center;
            minPlaceableCol = center;
            maxPlaceableCol = center;
        } else {
            // Check all placeable cells
            for (Cell cell : placeableCells) {
                minPlaceableRow = Math.min(minPlaceableRow, cell.row);
                maxPlaceableRow = Math.max(maxPlaceableRow, cell.row);
                minPlaceableCol = Math.min(minPlaceableCol, cell.col);
                maxPlaceableCol = Math.max(maxPlaceableCol, cell.col);
            }
        }

        // If no placeable cells found, don't enforce constraints
        if (minPlaceableRow == Integer.MAX_VALUE) {
            System.out.println("shouldEnforceScrollConstraints() - no placeable cells found");
            return false;
        }

        // Calculate grid and viewport dimensions
        double gridWidth = currentGameGrid.getPrefWidth();
        double gridHeight = currentGameGrid.getPrefHeight();
        double viewportWidth = viewportBounds.getWidth();
        double viewportHeight = viewportBounds.getHeight();

        // Calculate the current scroll position (in pixels from top-left of grid)
        double scrollOffsetX = gridScreen.getHvalue() * Math.max(0, gridWidth - viewportWidth);
        double scrollOffsetY = gridScreen.getVvalue() * Math.max(0, gridHeight - viewportHeight);

        // Calculate pixel boundaries of the placeable area
        double minPixelX = minPlaceableCol * cellSize;
        double maxPixelX = (maxPlaceableCol + 1) * cellSize;
        double minPixelY = minPlaceableRow * cellSize;
        double maxPixelY = (maxPlaceableRow + 1) * cellSize;

        // Calculate the total span of placeable tiles
        double totalPlaceableWidth = maxPixelX - minPixelX;
        double totalPlaceableHeight = maxPixelY - minPixelY;

        // If placeable tiles span more than the viewport can show, disable constraints
        // (you can't show all tiles at once anyway, so free scrolling is better)
        if (totalPlaceableWidth >= viewportWidth || totalPlaceableHeight >= viewportHeight) {
            System.out.println("shouldEnforceScrollConstraints() - placeable tiles span entire viewport:");
            System.out.println("  Placeable span: " + String.format("%.1f", totalPlaceableWidth) + "x"
                    + String.format("%.1f", totalPlaceableHeight) +
                    " vs viewport: " + String.format("%.1f", viewportWidth) + "x"
                    + String.format("%.1f", viewportHeight));
            System.out.println("  Result: scrolling DISABLED (tiles can't all fit on screen)");
            return false;
        }

        // Calculate the right and bottom edges of the current viewport
        double viewportMaxX = scrollOffsetX + viewportWidth;
        double viewportMaxY = scrollOffsetY + viewportHeight;

        // Check if placeable tiles extend beyond ANY edge of the viewport
        // Tiles exceed if:
        // - ANY placeable tile is left of viewport left edge OR
        // - ANY placeable tile is right of viewport right edge OR
        // - ANY placeable tile is above viewport top edge OR
        // - ANY placeable tile is below viewport bottom edge
        boolean exceedsLeft = minPixelX < scrollOffsetX;
        boolean exceedsRight = maxPixelX > viewportMaxX;
        boolean exceedsTop = minPixelY < scrollOffsetY;
        boolean exceedsBottom = maxPixelY > viewportMaxY;
        boolean tilesExceedViewport = exceedsLeft || exceedsRight || exceedsTop || exceedsBottom;

        System.out.println("shouldEnforceScrollConstraints() - checking if scrolling needed:");
        System.out.println("  Placeable bounds: rows [" + minPlaceableRow + "-" + maxPlaceableRow +
                "] cols [" + minPlaceableCol + "-" + maxPlaceableCol + "]");
        System.out.println(
                "  Placeable pixels: X[" + String.format("%.1f", minPixelX) + "-" + String.format("%.1f", maxPixelX) +
                        "] Y[" + String.format("%.1f", minPixelY) + "-" + String.format("%.1f", maxPixelY) + "]");
        System.out.println("  Viewport pixels: X[" + String.format("%.1f", scrollOffsetX) + "-"
                + String.format("%.1f", viewportMaxX) +
                "] Y[" + String.format("%.1f", scrollOffsetY) + "-" + String.format("%.1f", viewportMaxY) + "]");
        System.out.println("  Exceeds: left=" + exceedsLeft + " right=" + exceedsRight + " top=" + exceedsTop
                + " bottom=" + exceedsBottom);
        System.out.println("  Result: scrolling " + (tilesExceedViewport ? "ENABLED" : "DISABLED"));

        return tilesExceedViewport;
    }

    /**
     * Enforces scroll constraints to prevent scrolling more than one tile beyond
     * the selected tiles.
     * Ensures leftmost/rightmost/topmost/bottommost placeable tiles stay visible
     * with 1-tile buffer.
     * This applies even when tiles span the entire viewport in a direction.
     */
    public void enforceScrollConstraints() {
        // Only enforce constraints if tiles now exceed the viewport
        if (shouldEnforceScrollConstraints() ||
                gridScreen == null ||
                currentGameGrid == null ||
                cellSize <= 0) {
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

        // Get current scroll position in pixels
        double currentScrollPixelX = gridScreen.getHvalue() * maxScrollPixelX;
        double currentScrollPixelY = gridScreen.getVvalue() * maxScrollPixelY;

        // Calculate pixel boundaries for the allowed viewing region (with 1 cell
        // buffer)
        double minPlaceablePixelX = Math.max(0, (minSelectedCol - 1) * cellSize);
        double maxPlaceablePixelX = Math.min(gridWidth, (maxSelectedCol + 2) * cellSize);
        double minPlaceablePixelY = Math.max(0, (minSelectedRow - 1) * cellSize);
        double maxPlaceablePixelY = Math.min(gridHeight, (maxSelectedRow + 2) * cellSize);

        // Calculate the scroll range that keeps all placeable tiles (with buffer)
        // visible
        // The viewport left edge can be at most at minPlaceablePixelX (shows leftmost
        // tile + buffer)
        // The viewport left edge must be at least at (maxPlaceablePixelX -
        // viewportWidth) (shows rightmost tile + buffer)
        double minAllowedScrollX = Math.max(0, maxPlaceablePixelX - viewportWidth);
        double maxAllowedScrollX = Math.min(maxScrollPixelX, minPlaceablePixelX);

        double minAllowedScrollY = Math.max(0, maxPlaceablePixelY - viewportHeight);
        double maxAllowedScrollY = Math.min(maxScrollPixelY, minPlaceablePixelY);

        // If min > max, it means tiles span more than viewport can show
        // In this case, allow scrolling between the extremes to see different parts
        if (minAllowedScrollX > maxAllowedScrollX) {
            // Tiles span wider than viewport - clamp to show edges with buffer
            minAllowedScrollX = Math.max(0, minPlaceablePixelX);
            maxAllowedScrollX = Math.min(maxScrollPixelX, maxPlaceablePixelX - viewportWidth);
        }

        if (minAllowedScrollY > maxAllowedScrollY) {
            // Tiles span taller than viewport - clamp to show edges with buffer
            minAllowedScrollY = Math.max(0, minPlaceablePixelY);
            maxAllowedScrollY = Math.min(maxScrollPixelY, maxPlaceablePixelY - viewportHeight);
        }

        // Clamp current scroll to allowed range
        double newScrollPixelX = Math.max(minAllowedScrollX, Math.min(currentScrollPixelX, maxAllowedScrollX));
        double newScrollPixelY = Math.max(minAllowedScrollY, Math.min(currentScrollPixelY, maxAllowedScrollY));

        // Convert back to scroll values (0 to 1)
        double newHvalue = newScrollPixelX / maxScrollPixelX;
        double newVvalue = newScrollPixelY / maxScrollPixelY;

        // Apply if changed, using flag to prevent infinite recursion
        if (Math.abs(newHvalue - gridScreen.getHvalue()) > 0.001) {
            System.out.println("Enforcing horizontal constraint: " + String.format("%.3f", gridScreen.getHvalue()) +
                    " -> " + String.format("%.3f", newHvalue) +
                    " (allowed range: " + String.format("%.3f", minAllowedScrollX / maxScrollPixelX) +
                    " - " + String.format("%.3f", maxAllowedScrollX / maxScrollPixelX) + ")");
            isEnforcingConstraints = true;
            gridScreen.setHvalue(newHvalue);
            isEnforcingConstraints = false;
        }

        if (Math.abs(newVvalue - gridScreen.getVvalue()) > 0.001) {
            System.out.println("Enforcing vertical constraint: " + String.format("%.3f", gridScreen.getVvalue()) +
                    " -> " + String.format("%.3f", newVvalue) +
                    " (allowed range: " + String.format("%.3f", minAllowedScrollY / maxScrollPixelY) +
                    " - " + String.format("%.3f", maxAllowedScrollY / maxScrollPixelY) + ")");
            isEnforcingConstraints = true;
            gridScreen.setVvalue(newVvalue);
            isEnforcingConstraints = false;
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
    private Pane createPane(int row, int col) {
        Pane newPane = new Pane();
        newPane.setPrefSize(cellSize, cellSize);
        newPane.setMinSize(cellSize, cellSize);
        newPane.setMaxSize(cellSize, cellSize);

        // Get or create persistent state for this newPane
        Cell currentCell = controller.getCellAt(row, col);
        // Check if this newPane has a tile placed (from GameController)
        boolean isTilePlaced = controller.getPlacedTiles().contains(currentCell);
        // Check if this newPane is placeable (from GameController)
        // First tile can be placed at center
        boolean isPlaceable = controller.getPlaceableCells().contains(currentCell)
                || (controller.getPlacedTiles().isEmpty() && row == gridSize / 2 && col == gridSize / 2);

        // Set visual style based on newPane state
        if (isTilePlaced) {
            // Tile is placed - show as occupied (green background)
            newPane.setStyle("-fx-background-color: lightgreen; -fx-border-color: gray;");
            ImageView image = new ImageView(tileIdToImage.get(controller.getCellAt(row, col).tileId));
            image.rotateProperty().set(controller.getCellAt(row, col).rotation * 90); // Rotate based on tile's
                                                                                      // rotation state
            image.setPreserveRatio(true);
            image.setSmooth(true);
            newPane.getChildren().add(image); // Show tile image if placed
        } else if (isPlaceable) {
            // Cell is placeable - show as available (yellow background)
            newPane.setStyle("-fx-background-color: lightyellow; -fx-border-color: gray;");
        } else {
            // Default empty newPane
            newPane.setStyle("-fx-border-color: lightgray;");
        }

        // Track mouse press to detect if this is a drag or a click
        newPane.setOnMousePressed(event -> {
            dragStartX = event.getSceneX();
            dragStartY = event.getSceneY();
        });

        // Only trigger click action if it wasn't a drag
        newPane.setOnMouseClicked(event -> {
            double dragDistance = Math.hypot(
                    event.getSceneX() - dragStartX,
                    event.getSceneY() - dragStartY);

            // Only fire click action if drag distance is less than threshold
            if (dragDistance < DRAG_THRESHOLD) {
                System.out.println("Cell clicked at [" + row + "," + col + "]");

                // Place tile using GameController
                controller.placeTile(row, col);
                System.out.println("Tile placed at [" + row + "," + col + "]");
                // Update visual state immediately to give feedback (will be refreshed again in
                // updateVisibleCells)
                newPane.setStyle("-fx-background-color: lightgreen; -fx-border-color: gray;");
            }
        });

        return newPane;
    }

    /**
     * Refreshes all currently visible cells to update their visual state
     * (e.g., after placing a tile, to update which cells are now placeable).
     */
    public void refreshVisibleCells() {
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
            // Update scrolling state after re-rendering (placeable cells may have changed)
            updateScrollingState();
        }
    }

    public void setCurrentTile(char tileId, int orientation) {
        try {
            if (tileId == '0') {
                return;
            }

            Image tileImage = tileIdToImage.get(tileId);
            if (tileImage != null) {
                ImageView imageView = new ImageView(tileImage);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                imageView.rotateProperty().set(orientation * 90); // Rotate based on current rotation state
                                                                  // nextTilePane.getChildren().clear();
                                                                  // nextTilePane.getChildren().add(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderCellRange(int minX, int maxX, int minY, int maxY) {
        // If the visible range hasn't changed significantly, skip update
        if (lastRenderedMinRow == minX && lastRenderedMaxRow == maxX &&
                lastRenderedMinCol == minY && lastRenderedMaxCol == maxY) {
            return;
        }

        System.out.println("updateVisibleCells() - new range: rows [" + minX + "-" + maxX + "] cols [" + minY
                + "-" + maxY + "]");
        System.out.println("renderCellRange() - rendering: rows [" + minX + "-" + maxX + "] cols [" + minY + "-"
                + maxY + "]");
        System.out.println(
                "renderCellRange() - gridPane has " + currentGameGrid.getChildren().size() + " children before update");

        // REMOVE CELLS
        // Prepare to Prepare to remove cells that are no longer visible
        Set<Cell> newVisibleKeys = new HashSet<>();
        for (int row = minX; row <= maxX; row++) {
            for (int col = minY; col <= maxY; col++) {
                newVisibleKeys.add(new Cell(row, col));
            }
        }
        // Remove cells that are no longer visible from GUI
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

        // ADD NEW CELLS
        // Add new cells that are now visible
        int cellsAdded = 0;
        for (int row = minX; row <= maxX; row++) {
            for (int col = minY; col <= maxY; col++) {
                Cell cellKey = new Cell(row, col);
                if (!visibleCells.containsKey(cellKey)) {
                    try {
                        Pane cellPane = createPane(row, col);
                        visibleCells.put(cellKey, cellPane);
                        currentGameGrid.add(cellPane, col, row);
                        cellsAdded++;
                        if (cellsAdded <= 5) { // Log first few additions
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

        System.out.println("renderCellRange() - added " + cellsAdded + " cells, total visible: " + visibleCells.size()
                + ", gridPane now has " + currentGameGrid.getChildren().size() + " children");
    }

    private void renderCellRange() {
        javafx.geometry.Bounds viewportBounds = gridScreen.getViewportBounds();
        // Get current scroll position
        double gridWidth = currentGameGrid.getPrefWidth();
        double gridHeight = currentGameGrid.getPrefHeight();
        double viewportWidth = viewportBounds.getWidth();
        double viewportHeight = viewportBounds.getHeight();

        // Calculate pixel offset of the top-left corner of the viewport within the grid
        // Scroll value times the maximum scrollable distance (total grid size minus
        // viewport size)

        // Current scroll offset in pixels from the left edge of the grid
        double scrollOffsetX = gridScreen.getHvalue() * Math.max(0, gridWidth - viewportWidth);

        // Current scroll offset in pixels from the top edge of the grid
        double scrollOffsetY = gridScreen.getVvalue() * Math.max(0, gridHeight - viewportHeight);

        // Calculate which rows and columns are visible (with buffer)

        // Pixels from top of grid to top of viewport, divided by cell size gives the
        // index of the topmost visible row
        // We can show partial cells, so we use floor for min and ceil for max to ensure
        // we include any partially visible cells
        int minRow = Math.max(0, (int) Math.floor(scrollOffsetY / cellSize) - RENDER_BUFFER);
        int maxRow = Math.min(gridSize - 1,
                (int) Math.ceil((scrollOffsetY + viewportHeight) / cellSize) + RENDER_BUFFER);
        int minCol = Math.max(0, (int) Math.floor(scrollOffsetX / cellSize) - RENDER_BUFFER);
        int maxCol = Math.min(gridSize - 1,
                (int) Math.ceil((scrollOffsetX + viewportWidth) / cellSize) + RENDER_BUFFER);
        renderCellRange(minRow, maxRow, minCol, maxCol);
    }
}
