package carcassonne.UI;

import carcassonne.controller.GameController;
import carcassonne.controller.GameController.Cell;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.*;

/**
 * Data model for a single cell in the grid.
 * Stores the selection state independently of the visual representation.
 */
class CellData {
    public boolean isSelected = false;

    public Character tileId = null; // ID of the tile placed in this cell, null if no tile

    public CellData() {
    }

    public CellData(boolean isSelected) {
        this.isSelected = isSelected;
    }
}

public class GameView extends View {

    // Reference to game controller
    private final GameController gameController = GameController.getInstance();

    int gridSize = gameController.getGridSize();
    private boolean layoutRetryScheduled = false;

    private double cellPortionOfScreen = 0.1; // Portion of the screen width that the one cell in the grid takes up when using dynamic cell size instead of tile image size (0.0 to 1.0)

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

    // Scroll constraints based on selected tiles
    private int minSelectedRow = Integer.MAX_VALUE;
    private int maxSelectedRow = Integer.MIN_VALUE;
    private int minSelectedCol = Integer.MAX_VALUE;
    private int maxSelectedCol = Integer.MIN_VALUE;

    // Flag to prevent infinite recursion when enforceScrollConstraints modifies scroll values
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

    {
//        tileIdToImage.put('A', new Image("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_A.png"));
//        tileIdToImage.put('B', new Image("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_B.png"));
//        tileIdToImage.put('C', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_C.png")));
//        tileIdToImage.put('D', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_D.png")));
//        tileIdToImage.put('E', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_E.png")));
//        tileIdToImage.put('F', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_F.png")));
//        tileIdToImage.put('G', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_G.png")));
//        tileIdToImage.put('H', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_H.png")));
//        tileIdToImage.put('I', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_I.png")));
//        tileIdToImage.put('J', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_J.png")));
//        tileIdToImage.put('K', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_K.png")));
//        tileIdToImage.put('L', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_L.png")));
//        tileIdToImage.put('M', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_M.png")));
//        tileIdToImage.put('N', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_N.png")));
//        tileIdToImage.put('O', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_O.png")));
//        tileIdToImage.put('P', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_P.png")));
//        tileIdToImage.put('Q', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_Q.png")));
//        tileIdToImage.put('R', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_R.png")));
//        tileIdToImage.put('S', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_S.png")));
//        tileIdToImage.put('T', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_T.png")));
//        tileIdToImage.put('U', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_U.png")));
//        tileIdToImage.put('V', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_V.png")));
//        tileIdToImage.put('W', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_W.png")));
//        tileIdToImage.put('X', new Image(("C:\\Users\\Omistaja\\IdeaProjects\\carcasonne\\carcassonne\\src\\main\\resources\\images\\Base_Game_C3_Tile_X.png")));
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

    @FXML
    public VBox playerUiBox;

    @Override
    protected void onAfterStageAvailable() {
        displayCurrentPlacingTile();
        initGrid();
    }

    @Override
    protected void initialize() {
        super.initialize();
        System.out.println("GameView.initialize() called");
    }

    @Override
    public void onViewShow() {
        super.onViewShow();
        Platform.runLater(() -> {
            this.renderPlayerInfoBoxes(gameController.getCurrentPlayerCount(), playerUiBox);
        });
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
        gameController.rotateTile();
        displayCurrentPlacingTile();
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

        cellSize = tileIdToImage.get('A').getWidth(); // Use actual tile image width to determine cell size
        System.out.println("Initial cellSize based on tile image: " + cellSize);
//        cellSize = totalWidth * cellPortionOfScreen; // Adjust cell size based on window width

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
            if (!gameController.getPlacedTiles().isEmpty()) {
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
            if (!gameController.getPlacedTiles().isEmpty()) {
                enforceScrollConstraints();
            }
            updateVisibleCells();
        });

        // Listen for viewport size changes (window resize)
        gridScreen.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            updateVisibleCells();
            updateScrollingState();  // Re-check if scrolling should be enabled after resize
        });
    }

    /**
     * Enables or disables scrolling based on whether tiles exceed the viewport.
     * Scrolling (panning) is enabled once tiles are placed.
     * Constraints are enforced separately when tiles fit within viewport.
     */
    private void updateScrollingState() {
        if (gridScreen == null) {
            return;
        }

        // Enable panning once tiles are placed (even if they span entire viewport)
        boolean hasTilesPlaced = !gameController.getPlacedTiles().isEmpty();
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
     * Recalculates the scroll constraints based on currently placed tiles.
     * Only enforces constraints if the placed tiles extend beyond the viewport.
     */
    private void updateScrollConstraints() {
        minSelectedRow = Integer.MAX_VALUE;
        maxSelectedRow = Integer.MIN_VALUE;
        minSelectedCol = Integer.MAX_VALUE;
        maxSelectedCol = Integer.MIN_VALUE;

        // Find the bounds of all placed cells
        for (Cell cell : gameController.getPlacedTiles()) {
            if (cell.placed) {
                minSelectedRow = Math.min(minSelectedRow, cell.row);
                maxSelectedRow = Math.max(maxSelectedRow, cell.row);
                minSelectedCol = Math.min(minSelectedCol, cell.col);
                maxSelectedCol = Math.max(maxSelectedCol, cell.col);
            }
        }

        if (minSelectedRow == Integer.MAX_VALUE) {
            System.out.println("updateScrollConstraints() - no tiles placed, constraints disabled");
        } else {
            System.out.println("updateScrollConstraints() - Placed tile bounds: rows [" + minSelectedRow + "-" + maxSelectedRow +
                "] cols [" + minSelectedCol + "-" + maxSelectedCol + "]");
        }
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

        // Get the bounds of all placeable cells (including the first tile at center if no tiles placed yet)
        int minPlaceableRow = Integer.MAX_VALUE;
        int maxPlaceableRow = Integer.MIN_VALUE;
        int minPlaceableCol = Integer.MAX_VALUE;
        int maxPlaceableCol = Integer.MIN_VALUE;

        Set<Cell> placeableCells = gameController.getPlaceableCells();

        // If no tiles placed yet, the center cell is placeable
        if (gameController.getPlacedTiles().isEmpty()) {
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
            System.out.println("  Placeable span: " + String.format("%.1f", totalPlaceableWidth) + "x" + String.format("%.1f", totalPlaceableHeight) +
                             " vs viewport: " + String.format("%.1f", viewportWidth) + "x" + String.format("%.1f", viewportHeight));
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
        System.out.println("  Placeable pixels: X[" + String.format("%.1f", minPixelX) + "-" + String.format("%.1f", maxPixelX) +
                         "] Y[" + String.format("%.1f", minPixelY) + "-" + String.format("%.1f", maxPixelY) + "]");
        System.out.println("  Viewport pixels: X[" + String.format("%.1f", scrollOffsetX) + "-" + String.format("%.1f", viewportMaxX) +
                         "] Y[" + String.format("%.1f", scrollOffsetY) + "-" + String.format("%.1f", viewportMaxY) + "]");
        System.out.println("  Exceeds: left=" + exceedsLeft + " right=" + exceedsRight + " top=" + exceedsTop + " bottom=" + exceedsBottom);
        System.out.println("  Result: scrolling " + (tilesExceedViewport ? "ENABLED" : "DISABLED"));

        return tilesExceedViewport;
    }


    /**
     * Enforces scroll constraints to prevent scrolling more than one tile beyond the selected tiles.
     * Ensures leftmost/rightmost/topmost/bottommost placeable tiles stay visible with 1-tile buffer.
     * This applies even when tiles span the entire viewport in a direction.
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

        // Get current scroll position in pixels
        double currentScrollPixelX = gridScreen.getHvalue() * maxScrollPixelX;
        double currentScrollPixelY = gridScreen.getVvalue() * maxScrollPixelY;

        // Calculate pixel boundaries for the allowed viewing region (with 1 cell buffer)
        double minPlaceablePixelX = Math.max(0, (minSelectedCol - 1) * cellSize);
        double maxPlaceablePixelX = Math.min(gridWidth, (maxSelectedCol + 2) * cellSize);
        double minPlaceablePixelY = Math.max(0, (minSelectedRow - 1) * cellSize);
        double maxPlaceablePixelY = Math.min(gridHeight, (maxSelectedRow + 2) * cellSize);

        // Calculate the scroll range that keeps all placeable tiles (with buffer) visible
        // The viewport left edge can be at most at minPlaceablePixelX (shows leftmost tile + buffer)
        // The viewport left edge must be at least at (maxPlaceablePixelX - viewportWidth) (shows rightmost tile + buffer)
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
     * @return a StackPane representing one grid cell
     */
    private Pane createCell(int row, int col) {
        StackPane newPane = new StackPane();
        newPane.setPrefSize(cellSize, cellSize);
        newPane.setMinSize(cellSize, cellSize);
        newPane.setMaxSize(cellSize, cellSize);

        // Get or create persistent state for this newPane
        Cell currentCell = gameController.getCellAt(row, col);

        // Check if this newPane has a tile placed (from GameController)
        boolean isTilePlaced = gameController.getPlacedTiles().contains(currentCell);

        // Check if this newPane is placeable (from GameController)
        boolean isPlaceable = gameController.getPlaceableCells().contains(currentCell) || (gameController.getPlacedTiles().isEmpty() && row == gridSize / 2 && col == gridSize / 2); // First tile can be placed at center

        // Set visual style based on newPane state
        if (isTilePlaced) {
            // Tile is placed - show as occupied (green background)
            newPane.setStyle("-fx-background-color: lightgreen; -fx-border-color: gray;");
            ImageView image = new ImageView(tileIdToImage.get(gameController.getCellAt(row, col).tileId));
            image.rotateProperty().set(gameController.getCellAt(row, col).rotation*90); // Rotate based on tile's rotation state
            image.setPreserveRatio(true);
            image.setSmooth(true);
            newPane.getChildren().add(image); // Show tile image if placed

            int meeplePosition = gameController.getCellAt(row, col).meeple;
            if (meeplePosition != -1) {
                int playerNumber = gameController.getCellAt(row, col).player;
                displayMeepleOnCell(newPane, meeplePosition, playerNumber);
            }

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
                    event.getSceneY() - dragStartY
            );

            // Only fire click action if drag distance is less than threshold
            if (dragDistance < DRAG_THRESHOLD) {
                System.out.println("Cell clicked at [" + row + "," + col + "]");

                // Check if tile is already placed
                if (gameController.getPlacedTiles().contains(currentCell)) {
                    System.out.println("Tile already placed at [" + row + "," + col + "]");
                    return;
                }

                // Check if this is a valid placement location
                if (!isPlaceable && !gameController.getPlacedTiles().isEmpty()) {
                    System.out.println("Cannot place tile at [" + row + "," + col + "] - not adjacent to existing tiles");
                    return;
                }

                if (gameController.getPlacedTiles().isEmpty()) {
                    if (row != gridSize / 2 || col != gridSize / 2) {
                        System.out.println("First tile must be placed at the center [" + (gridSize / 2) + "," + (gridSize / 2) + "]");
                        return;
                    }
                }

                // Update visual state immediately to give feedback (will be refreshed again in updateVisibleCells)
                newPane.setStyle("-fx-background-color: lightgreen; -fx-border-color: gray;");

                // Place tile using GameController
                gameController.placeTile(row, col);
                System.out.println("Tile placed at [" + row + "," + col + "]");

                // display the next tile image
                displayCurrentPlacingTile();

                // redraw player info boxes to update scores and current player
                renderPlayerInfoBoxes(gameController.getCurrentPlayerCount(), playerUiBox);

                // Refresh all visible cells to update placeable highlights
                refreshVisibleCells();

                // Update scroll constraints based on new tile placement
                updateScrollConstraints();

                // Enable scrolling if tiles now exceed the viewport
                updateScrollingState();

                // Only enforce constraints if tiles now exceed the viewport
                if (shouldEnforceScrollConstraints()) {
                    enforceScrollConstraints();
                }
            }
        });

        return newPane;
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

            // Update scrolling state after re-rendering (placeable cells may have changed)
            updateScrollingState();
        }
    }

    private void displayCurrentPlacingTile() {
        if (nextTilePane == null) {
            return;
        }

        try {
            Character currentTileId = gameController.getCurrentTileId();
            if (currentTileId == null) {
                return;
            }

            Image tileImage = tileIdToImage.get(currentTileId);
            if (tileImage != null) {
                ImageView imageView = new ImageView(tileImage);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.rotateProperty().set(gameController.getCurrentRotation()*90); // Rotate based on current rotation state

                nextTilePane.getChildren().clear();
                nextTilePane.getChildren().add(imageView);
                displayMeeplePlacementOptions(nextTilePane, imageView);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayMeeplePlacementOptions(StackPane stackPane, ImageView tileImage) {
        double radius = tileImage.getImage().getHeight() * 0.1;
        Circle topCircle = new Circle(radius);
        Circle bottomCircle = new Circle(radius);
        Circle leftCircle = new Circle(radius);
        Circle rightCircle = new Circle(radius);

        Color playerColor = switch (gameController.getCurrentPlayingPlayer()) {
            case 1 -> Color.RED;
            case 2 -> Color.BLUE;
            case 3 -> Color.GREEN;
            case 4 -> Color.YELLOW;
            case 5 -> Color.ORANGE;
            default -> Color.GRAY;
        };

        topCircle.setFill(playerColor.darker());
        bottomCircle.setFill(playerColor.darker());
        leftCircle.setFill(playerColor.darker());
        rightCircle.setFill(playerColor.darker());

        stackPane.getChildren().add(topCircle);
        stackPane.getChildren().add(bottomCircle);
        stackPane.getChildren().add(leftCircle);
        stackPane.getChildren().add(rightCircle);

        // Set alignment of the circles within the StackPane
        // Use javafx.geometry.Pos for positioning: TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        // CENTER_LEFT, CENTER, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
        stackPane.setAlignment(topCircle, javafx.geometry.Pos.TOP_CENTER);
        stackPane.setAlignment(bottomCircle, javafx.geometry.Pos.BOTTOM_CENTER);
        stackPane.setAlignment(leftCircle, javafx.geometry.Pos.CENTER_LEFT);
        stackPane.setAlignment(rightCircle, javafx.geometry.Pos.CENTER_RIGHT);

        handleMeeplePlacement(stackPane, topCircle, bottomCircle, leftCircle, rightCircle);
    }

    // Handles logic and click handlers for placing a meeple on the currently placing tile
    private void handleMeeplePlacement (StackPane stackPane, Circle topCircle, Circle bottomCircle, Circle leftCircle, Circle rightCircle) {
        gameController.setCurrentMeeplePlacement(-1); // -1 means no meeple, 0-3 for top/right/bottom/left
        final Circle[] selectedCircle = new Circle[1]; // selectedCircle[0] is current selection

        Runnable resetColors = () -> {
            Color playerColor = switch (gameController.getCurrentPlayingPlayer()) {
                case 1 -> Color.RED;
                case 2 -> Color.BLUE;
                case 3 -> Color.GREEN;
                case 4 -> Color.YELLOW;
                case 5 -> Color.ORANGE;
                default -> Color.GRAY;
            };

            topCircle.setFill(playerColor.darker());
            bottomCircle.setFill(playerColor.darker());
            leftCircle.setFill(playerColor.darker());
            rightCircle.setFill(playerColor.darker());
        };

        java.util.function.Consumer<Circle> toggleSelection = clicked -> {
            Color lighterColor = switch (gameController.getCurrentPlayingPlayer()) {
                case 1 -> Color.RED.brighter();
                case 2 -> Color.BLUE.brighter();
                case 3 -> Color.GREEN.brighter();
                case 4 -> Color.YELLOW.brighter();
                case 5 -> Color.ORANGE.brighter();
                default -> Color.LIGHTGRAY;
            };

            if (selectedCircle[0] == clicked) {
                selectedCircle[0] = null;   // unselect if clicked again
                resetColors.run();
                gameController.setCurrentMeeplePlacement(
                        -1 // no meeple
                );
            } else {
                selectedCircle[0] = clicked;
                resetColors.run();
                gameController.setCurrentMeeplePlacement(
                        clicked == topCircle ? 0 :
                        clicked == rightCircle ? 1 :
                        clicked == bottomCircle ? 2 :
                        clicked == leftCircle ? 3 : -1
                );
                clicked.setFill(lighterColor);
            }
        };

        topCircle.setOnMouseClicked(e -> {
            System.out.println("Top section clicked for meeple placement");
            toggleSelection.accept(topCircle);
        });

        bottomCircle.setOnMouseClicked(e -> {
            System.out.println("Bottom section clicked for meeple placement");
            toggleSelection.accept(bottomCircle);
        });

        leftCircle.setOnMouseClicked(e -> {
            System.out.println("Left section clicked for meeple placement");
            toggleSelection.accept(leftCircle);
        });

        rightCircle.setOnMouseClicked(e -> {
            System.out.println("Right section clicked for meeple placement");
            toggleSelection.accept(rightCircle);
        });
    }

    private void displayMeepleOnCell(StackPane pane, int meeplePosition , int player) {
        Color playerColor = switch (player) {
            case 1 -> Color.RED;
            case 2 -> Color.BLUE;
            case 3 -> Color.GREEN;
            case 4 -> Color.YELLOW;
            case 5 -> Color.ORANGE;
            default -> Color.GRAY;
        };

        double radius = pane.getPrefWidth() * 0.1;
        Circle meepleCircle = new Circle(radius);
        meepleCircle.setFill(playerColor.darker());
        pane.getChildren().add(meepleCircle);

        // Position the meeple based on the specified position (0=top, 1=right, 2=bottom, 3=left)
        switch (meeplePosition) {
            case 0 -> pane.setAlignment(meepleCircle, javafx.geometry.Pos.TOP_CENTER);
            case 1 -> pane.setAlignment(meepleCircle, javafx.geometry.Pos.CENTER_RIGHT);
            case 2 -> pane.setAlignment(meepleCircle, javafx.geometry.Pos.BOTTOM_CENTER);
            case 3 -> pane.setAlignment(meepleCircle, javafx.geometry.Pos.CENTER_LEFT);
            default -> pane.setAlignment(meepleCircle, javafx.geometry.Pos.CENTER); // Fallback to center if invalid
        }
    }

    private void renderPlayerInfoBoxes(int playerCount, VBox container) {
        container.getChildren().clear();
        container.setSpacing(10); // Add spacing between player boxes

        for (int i = 1; i <= playerCount; i++) {
            HBox hbox = new HBox(10); // Add spacing between label and circle
            hbox.setStyle("-fx-alignment: center;"); // Center content horizontally and vertically
            hbox.getChildren().add(new Label("Player " + i));

            for (int j = 0; j < gameController.getPlayerMeepleCount(i); j++) {
                Circle circle = new Circle(10);
                switch (i) {
                    case 1 -> circle.setFill(Color.RED);
                    case 2 -> circle.setFill(Color.BLUE);
                    case 3 -> circle.setFill(Color.GREEN);
                    case 4 -> circle.setFill(Color.YELLOW);
                    case 5 -> circle.setFill(Color.ORANGE);
                    default -> circle.setFill(Color.GRAY);
                }

                hbox.getChildren().add(circle);
            }

            // Make each HBox grow to fill equal vertical space
            VBox.setVgrow(hbox, javafx.scene.layout.Priority.ALWAYS);

            if (gameController.getCurrentPlayingPlayer() == i) {
                hbox.setStyle("-fx-background-color: lightblue; -fx-alignment: center;"); // Highlight current player
            }

            container.getChildren().add(hbox);
        }

        // Add empty space at the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        container.getChildren().add(spacer);
    }
}
