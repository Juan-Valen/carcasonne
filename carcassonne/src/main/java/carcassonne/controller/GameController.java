package carcassonne.controller;

import carcassonne.UI.GameView;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;

import java.util.*;

public class GameController {
    private static GameController instance;

    private GameController() {
        // Private constructor to prevent instantiation
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    private GameView view; // Reference to the view, can be used to update the UI based on game state changes

    public void setView(GameView view) {
        this.view = view;
    };

    public static class Cell {
        public final int row;
        public final int col;
        public Character tileId = null; // ID of the tile placed in this cell
        public boolean placed = false; // Whether a tile has been placed in this cell
        public int rotation = 0; // Rotation of the tile in this cell (0, 90, 180, 270)
        public int meeple = -1; // Meeple placement on the tile in this cell (0 = top, 1 = right, 2 = bottom, 3 = left, -1 = no meeple)
        public int player = -1; // Player who placed the tile/meeple in this cell, -1 if no tile/meeple

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Cell)) return false;
            Cell cell = (Cell) o;
            return row == cell.row && col == cell.col;
        }

        @Override
        public int hashCode() {
            return 31 * row + col;
        }

        @Override
        public String toString() {
            return "Cell[" + row + "," + col + "]";
        }
    }

    private int gridSize = 144; // Default grid size

    private int RENDER_BUFFER = 2; // Number of extra rows/columns to render beyond the visible area for smoother scrolling

    private int maxPlayers = 5; // Maximum number of players

    private int maxMeeples = 5; // Maximum number of meeples per player, adjust as needed

    // Incremental render cache for currently visible cell nodes.
    private final Map<Cell, Pane> renderedCellPanes = new HashMap<>();

    public void initView() {
        // Initialize the view with the current game state
        if (view != null) {
            // Display the current tile to be placed in the view
            view.displayCurrentPlacingTile(getCurrentRotation(), getCurrentTileId(), getCurrentPlayingPlayer());
            // Update the player info boxes in the view based on the current player count and meeple counts
            view.renderPlayerInfoBoxes(getCurrentPlayingPlayer(), getCurrentPlayerCount(),view.playerUiBox, getPlayersMeepleCounts());
            // Initialize the grid in the view
            view.initGrid(getGridSize());
            // Listen for scroll changes to update visible cells
            view.addScrollListeners();
            // Update visible cells in the view based on the initial grid
            setCurrentVisibleCellBounds(getVisibleCellBounds(getGridSize()));
            // Update scroll constraints based on initial tile placements (if any)
            renderVisibleCells(getVisibleCellBounds(getGridSize()));
        }
    }

    public void placeTile (int row, int col) {
        // enable panning of grid if it was previously disabled due to no tiles being placed
        view.updateScrollingState(true);

        Cell cell = new Cell(row, col);
        cell.tileId = getCurrentTileId(); // Placeholder for getting the current tile ID from the game state
        cell.placed = true;
        cell.rotation = currentRotation;
        if (getCurrentMeeplePlacement() != -1 && getPlayerMeepleCount(getCurrentPlayingPlayer()) > 0) {
            cell.meeple = getCurrentMeeplePlacement(); // Placeholder for getting the current meeple placement from the game state
        } else {
            cell.meeple = -1; // No meeple placed
        }
        if (cell.meeple != -1) {
            decrementPlayerMeepleCount(getCurrentPlayingPlayer());
        }
        cell.player = getCurrentPlayingPlayer();
        getNextTile(); // Update the current tile to the next tile after placing
        setNextPlayingPlayer();
        PlacedTilePositions.add(cell);

        // display the next tile image
        view.displayCurrentPlacingTile(getCurrentRotation(), getCurrentTileId(), getCurrentPlayingPlayer());

        // redraw player info boxes to update scores and current player
        view.renderPlayerInfoBoxes(getCurrentPlayingPlayer() ,getCurrentPlayerCount(), view.playerUiBox, getPlayersMeepleCounts());

        // Update scroll constraints based on new tile placement
        view.updateScrollConstraints(getPlacedTiles());

        // Only enforce constraints if tiles now exceed the viewport
        if (view.shouldEnforceScrollConstraints(getPlacedTiles(), getPlaceableCells(), getGridSize())) {
            view.enforceScrollConstraints();
        }

        int[] visibleCellsEdges = getVisibleCellBounds(getGridSize());
        if (visibleCellsEdges != null) {
            // Tile state changed; rebuild currently visible cells once to refresh styles/content.
            clearRenderedCells();
            renderVisibleCells(visibleCellsEdges);
        }
    }

    public void rotateTile() {
        if (!(getCurrentRotation() >= 3)) {
            setCurrentRotation(getCurrentRotation()+1);
        } else {
            setCurrentRotation(0);
        }

        // Update the displayed tile in the view to reflect the new rotation
        view.displayCurrentPlacingTile(getCurrentRotation(), getCurrentTileId(), getCurrentPlayingPlayer());
    }

    // Handles events from scrolling or resizing the window, enforces scroll constraints and updates visible cells
    public void handleScroll(Boolean isEnforcingConstraints) {
        // Skip if we're already enforcing constraints (prevents infinite recursion due to function scrolls causing more scrolls)
        if (isEnforcingConstraints) {
            return;
        }

        // Enforce constraints if tiles are placed (but don't disable panning)
        if (!getPlacedTiles().isEmpty()) {
            view.enforceScrollConstraints();
        }
        int[] visibleCellsEdges = getVisibleCellBounds(getGridSize());
        // Render new visible cells if scroll resulted in a change of visible cells
        if (visibleCellsEdges != null) {
            renderVisibleCells(visibleCellsEdges);
        }
    }

    // Called on clicking a meeple placement option in the UI, sets the current meeple placement in the game state
    public void setCurrentMeeplePlacement(int position) {
        // 0 = top, 1 = right, 2 = bottom, 3 = left, -1 = no meeple
        // Placeholder function to set the current meeple placement in the game state
        setCurrentMeeplePlacementToModel(position);
    }

    private int getGridSize() {
        return gridSize;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setCurrentPlayerCount(int currentPlayerCount) {
        setCurrentPlayerCountToModel(currentPlayerCount); // Update the player count in the model
    }

    private int getCurrentPlayerCount() {
        return getCurrentPlayerCountFromModel(); // Get the current player count from the model
    }

    private int getCurrentPlayingPlayer() {
        return getCurrentPlayingPlayerFromModel(); // Get the current playing player from the model
    }

    private void setNextPlayingPlayer() {
        setNextPlayingPlayerInModel();
    }

    private Character getCurrentTileId() {
        // Placeholder function to get the current tile ID from the game state
        // This should return the ID of the tile that is currently being placed

        return currentTileId; // Placeholder for getting the current tile from the model
    }

    private int getCurrentRotation() {
        return currentRotation;
    }

    private void  setCurrentRotation(int currentRotation) {
        setCurrentRotationInModel(currentRotation);
    }

    private int getCurrentMeeplePlacement() {
        // 0 = top, 1 = right, 2 = bottom, 3 = left, -1 = no meeple
        return getCurrentMeeplePlacementFromModel();
    }

    private void decrementPlayerMeepleCount(int player) {
            // Update the model with the new meeple count for the player
        decrementPlayerMeepleCountInModel(player);
    }

    private int getPlayerMeepleCount(int player) {
        // Placeholder function to get the current meeple count for a player from the game state
        return getPlayerMeepleCountFromModel(player);
    }

    private int[] getPlayersMeepleCounts() {
        int[] counts = new int[getCurrentPlayerCount()];
        for (int i = 0; i < getCurrentPlayerCount(); i++) {
            counts[i] = getPlayerMeepleCount(i + 1); // Player numbers are 1-indexed
        }
        return counts;
    }

    private void setPlacedTilePositions(Cell cell) {
        setPlacedTilePositionsToModel(cell); // Update the model with the new placed tile position
    }

    private Set<Cell> getPlacedTilePositions() {
        return getPlacedTilesFromModel(); // Get the currently placed tiles from the model
    }

    private Set<Cell> getPlaceableCells() {
        Set<Cell> placeableTiles = new HashSet<>();
        placeableTiles = calculatePlaceableCells();
        return placeableTiles;
    }

    private Set<Cell> getPlacedTiles() {
        return new HashSet<>(PlacedTilePositions);
    }

    private void getNextTile() {
        // This should return the next tile based on the game state
        currentTileId = getNextTileIdFromModel(); // Placeholder for getting the next tile from the model
        currentRotation = 0; // Reset rotation for the new tile
    }

    private int [] getVisibleCellBounds(int gridSize) {
        // Placeholder function to calculate the bounds of the currently visible cells based on the current scroll position and grid size
        // This returns an array with the format {minRow, minCol, maxRow, maxCol} representing the range of currently visible cells

        // Works after view.InitGrid() hase been called as the cell size is set in initGrid() based on the tile image size

        if (view.currentGameGrid == null) {
            System.out.println("ERROR: currentGameGrid is null in getVisibleCellBounds");
            return null;
        }

        Bounds viewportBounds = view.gridScreen.getViewportBounds();

        // Get current scroll position
        double gridWidth = view.currentGameGrid.getPrefWidth();
        double gridHeight = view.currentGameGrid.getPrefHeight();
        double viewportWidth = viewportBounds.getWidth();
        double viewportHeight = viewportBounds.getHeight();

        // Calculate pixel offset of the top-left corner of the viewport within the grid
        // Scroll value times the maximum scrollable distance (total grid size minus viewport size)

        // Current scroll offset in pixels from the left edge of the grid
        double scrollOffsetX = view.gridScreen.getHvalue() * Math.max(0, gridWidth - viewportWidth);

        // Current scroll offset in pixels from the top edge of the grid
        double scrollOffsetY = view.gridScreen.getVvalue() * Math.max(0, gridHeight - viewportHeight);

        // Calculate which rows and columns are visible (with buffer)

        // Pixels from top of grid to top of viewport, divided by cell size gives the index of the topmost visible row
        // We can show partial cells, so we use floor for min and ceil for max to ensure we include any partially visible cells
        int minRow = Math.max(0, (int) Math.floor(scrollOffsetY / view.cellSize) - RENDER_BUFFER);
        int maxRow = Math.min(gridSize - 1, (int) Math.ceil((scrollOffsetY + viewportHeight) / view.cellSize) + RENDER_BUFFER);
        int minCol = Math.max(0, (int) Math.floor(scrollOffsetX / view.cellSize) - RENDER_BUFFER);
        int maxCol = Math.min(gridSize - 1, (int) Math.ceil((scrollOffsetX + viewportWidth) / view.cellSize) + RENDER_BUFFER);

        return new int[]{minRow, minCol, maxRow, maxCol};
    }

    // Renders the cells that are currently visible on the screen based on the current scroll position and grid size, should only render cells that have changed visibility since the last render for performance optimization
    private void renderVisibleCells(int[] visibleCellBounds) {
        // cellbounds format: {minRow, minCol, maxRow, maxCol}
        if (visibleCellBounds == null || view.currentGameGrid == null) {
            return;
        }

        Set<Cell> targetVisibleCells = new HashSet<>();
        Set<Cell> placedTiles = getPlacedTilesFromModel();
        Set<Cell> placeableCells = getPlaceableCells();

        for (int row = visibleCellBounds[0]; row <= visibleCellBounds[2]; row++) {
            for (int col = visibleCellBounds[1]; col <= visibleCellBounds[3]; col++) {
                Cell cell = new Cell(row, col);
                targetVisibleCells.add(cell);

                if (renderedCellPanes.containsKey(cell)) {
                    continue;
                }

                Pane newCellPane;
                if (getPlacedTilesFromModel().contains(cell)) {
                    Cell existingCell = getCellAt(row, col);
                    newCellPane = view.createCell(row, col, existingCell.rotation, existingCell.tileId, existingCell.meeple, existingCell.player, true, false);
                } else if (getPlaceableCells().contains(cell)) {
                    newCellPane = view.createCell(row, col, 0, null, -1, -1, false, true);
                } else {
                    newCellPane = view.createCell(row, col, 0, null, -1, -1, false, false);
                }

                // Cache the rendered pane for this cell so we can reuse it if the cell remains visible, and remove it from the grid if it goes out of view
                renderedCellPanes.put(cell, newCellPane);
                view.currentGameGrid.add(newCellPane, col, row);
            }
        }

        Iterator<Map.Entry<Cell, Pane>> iterator = renderedCellPanes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Cell, Pane> entry = iterator.next();
            if (!targetVisibleCells.contains(entry.getKey())) {
                view.currentGameGrid.getChildren().remove(entry.getValue());
                iterator.remove();
            }
        }

        setCurrentVisibleCellBounds(visibleCellBounds);
    }

    private void clearRenderedCells() {
        if (view != null && view.currentGameGrid != null) {
            for (Pane pane : renderedCellPanes.values()) {
                view.currentGameGrid.getChildren().remove(pane);
            }
        }
        renderedCellPanes.clear();
    }

    private Cell getCellAt(int row, int col) {
        Cell cell = new Cell(row, col);
        for (Cell existingCell : PlacedTilePositions) {
            if (existingCell.equals(cell)) {
                return existingCell; // Return the cell with the tile ID
            }
        }
        cell.placed = false;
        return cell;
    }

    // Stuff that should be in the model but is here for testing purposes, should be moved to the model later

    private int currentPlayerCount = 2; // the current number of players

    private Set<Cell> PlacedTilePositions = new HashSet<>();

    // Top left and bottom right corners of the currently placed tiles, used for optimizing scroll constraints
    private int [] currentPlacedTileBounds = new int[]{0, 0, 0, 0}; // {minRow, minCol, maxRow, maxCol}

    // Top left and bottom right corners of the currently visible cells, used for optimizing rendering of visible cells
    private int [] currentVisibleCellBounds = new int[]{0, 0, 0 , 0}; // {minRow, minCol, maxRow, maxCol}

    final Random random = new Random();

    List<Character> keys = Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X'); // Example list of tile identifiers, should be replaced with actual tile data from the model

    private Character currentTileId = getNextTileIdFromModel(); // Placeholder for the current tile, should be set based on the game state

    private int currentRotation = 0; // Placeholder for the current tile rotation, should be set based on the game state

    private int currentMeeplePlacement = -1; // Placeholder for the current meeple placement, should be set based on the game state

    private int currentPlayingPlayer = 1; // Placeholder for the current playing player, should be set based on the game state

    private int [] playerMeepleCounts = null; // Placeholder for tracking the number of meeples each player has, should be managed in the model

    private void setCurrentPlacedTileBounds(int[] bounds) {
        this.currentPlacedTileBounds = bounds; // Should update the actual placed tile bounds in the model
    }

    private void setCurrentVisibleCellBounds(int[] bounds) {
        this.currentVisibleCellBounds = bounds; // Should update the actual visible cell bounds in the model
    }

    private int getCurrentRotationFromModel() {
        return currentRotation;
    }

    private void setCurrentRotationInModel(int currentRotation) {
        this.currentRotation = currentRotation; // Should update the actual rotation in the model
    }

    private int getCurrentPlayingPlayerFromModel() {
        // Placeholder function to get the current playing player from the game state
        return currentPlayingPlayer; // Should return the actual current playing player from the model
    }

    private void setNextPlayingPlayerInModel() {
        // Placeholder function to set the next playing player in the game state
        if  (currentPlayingPlayer == currentPlayerCount) {
            currentPlayingPlayer = 1; // Loop back to the first player
        } else {
            currentPlayingPlayer += 1; // Move to the next player
        }
    }

    private void setCurrentMeeplePlacementToModel(int currentMeeplePlacement) {
        // 0 = top, 1 = right, 2 = bottom, 3 = left, -1 = no meeple
        // Placeholder function to set the current meeple placement in the game state
        this.currentMeeplePlacement = currentMeeplePlacement; // Should update the actual meeple placement in the model
    }

    private int getPlayerMeepleCountFromModel(int player) {
        if (playerMeepleCounts == null) {
            playerMeepleCounts = new int[currentPlayerCount];
            Arrays.fill(playerMeepleCounts, maxMeeples); // Assuming each player starts with 7 meeples, adjust as needed
        }
        // Placeholder function to get the current meeple count for a player from the game state
        if (player >= 1 && player <= currentPlayerCount) {
            return playerMeepleCounts[player - 1]; // Return meeple count for the player
        }
        return 0; // Return 0 if player number is invalid
    }

    private void decrementPlayerMeepleCountInModel(int player) {
        // Placeholder function to decrement the meeple count for a player in the game state
        if (player >= 1 && player <= currentPlayerCount) {
            playerMeepleCounts[player - 1] = Math.max(0, playerMeepleCounts[player - 1] - 1); // Decrement meeple count for the player, ensuring it doesn't go below 0
        }
    }

    private int getCurrentMeeplePlacementFromModel() {
        // 0 = top, 1 = right, 2 = bottom, 3 = left, -1 = no meeple
        // Placeholder function to get the current meeple placement from the game state
        return currentMeeplePlacement; // Should return the actual meeple placement from the model
    }

    private int getCurrentPlayerCountFromModel() {
        // Placeholder function to get the current player count from the game state
        return currentPlayerCount; // Should return the actual player count from the model
    }

    private void setCurrentPlayerCountToModel(int currentPlayerCount) {
        // Placeholder function to set the current player count in the game state
        this.currentPlayerCount = currentPlayerCount; // Should update the actual player count in the model
    }

    private void setPlacedTilePositionsToModel(Cell cell) {
        PlacedTilePositions.add(cell); // Should add the placed tile position to the model
    }

    private Set<Cell> getPlacedTilesFromModel() {
        // Placeholder function to get the currently placed tiles from the game state
        // This should return a set of tile identifiers that are currently placed on the grid
        return PlacedTilePositions;
    }

    private Set<Cell> calculatePlaceableCells() {
        // Placeholder function in the controller, should be implemented in the model to calculate the placeable tiles based on the current game state
        // This should return a set of tile identifiers that can be placed on the grid
        // All empty tiles adjacent to already placed tiles should be considered placeable

        Set<Cell> placeableTiles = new HashSet<>();

        if (PlacedTilePositions.isEmpty()) {
            // If no tiles are placed yet, the starting tile can be placed in the center of the grid
            placeableTiles.add(new Cell(gridSize / 2, gridSize / 2));
            return placeableTiles;
        }

        // For each placed tile, check all four orthogonal neighbors
        for (Cell cell : PlacedTilePositions) {
            // Check above
            Cell above = new Cell(cell.row - 1, cell.col);
            if (!PlacedTilePositions.contains(above)) {
                placeableTiles.add(above);
            }

            // Check below
            Cell below = new Cell(cell.row + 1, cell.col);
            if (!PlacedTilePositions.contains(below)) {
                placeableTiles.add(below);
            }

            // Check left
            Cell left = new Cell(cell.row, cell.col - 1);
            if (!PlacedTilePositions.contains(left)) {
                placeableTiles.add(left);
            }

            // Check right
            Cell right = new Cell(cell.row, cell.col + 1);
            if (!PlacedTilePositions.contains(right)) {
                placeableTiles.add(right);
            }
        }

        return placeableTiles;
    }

    private Character getNextTileIdFromModel() {
        // Random instance for picking random tiles
            return keys.get(random.nextInt(keys.size()));
    }
}
