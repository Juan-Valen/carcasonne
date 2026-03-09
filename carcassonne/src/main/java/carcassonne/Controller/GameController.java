package carcassonne.Controller;

import java.util.*;
import carcassonne.Model.Game;
import carcassonne.Model.Tile;
import carcassonne.UI.GameView;

public class GameController {
    private static GameController instance;
    private Game model;
    private GameView view;

    private GameController() {
        model = new Game();
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public void setView(GameView view) {
        this.view = view;
    };

    private int gridSize = 144; // Default grid size

    public int getGridSize() {
        return gridSize;
    }

    public void placeTile(int row, int col) {

        model.placeTile(row, col);
        Tile tile = model.getCurrentTile();
        view.setCurrentTile(tile.getType(), tile.getOrientation());
        // Refresh all visible cells to update placeable highlights
        view.refreshVisibleCells();
        // Update scroll constraints based on new tile placement
        view.updateScrollConstraints();
        // Enable scrolling if tiles now exceed the viewport
        view.updateScrollingState();
        view.enforceScrollConstraints();

    }

    public void rotateTile() {
        if (!(currentRotation >= 3)) {
            currentRotation += 1;
        } else {
            currentRotation = 0;
        }
        Tile tile = model.getCurrentTile();
        view.setCurrentTile(tile.getType(), tile.getOrientation());
    }

    public Set<Cell> getPlaceableCells() {
        Set<Cell> placeableTiles = new HashSet<>();
        placeableTiles = calculatePlaceableCells();
        return placeableTiles;
    }

    public Set<Cell> getPlacedTiles() {
        return new HashSet<>(PlacedTilePositions);
    }

    public Cell getCellAt(int row, int col) {
        Cell cell = new Cell(row, col);
        for (Cell existingCell : PlacedTilePositions) {
            if (existingCell.equals(cell)) {
                return existingCell; // Return the cell with the tile ID
            }
        }
        cell.placed = false;
        return cell;
    }

    // Stuff that should be in the model but is here for testing purposes, should be
    // moved to the model later

    private Set<Cell> PlacedTilePositions = new HashSet<>();

    final Random random = new Random();

    List<Character> keys = Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X'); // Example list of tile identifiers, should be replaced with actual
                                                     // tile data from the model

    // on the game state

    private int currentRotation = 0; // Placeholder for the current tile rotation, should be set based on the game
                                     // state

    private Set<Cell> calculatePlaceableCells() {
        // Placeholder function in the controller, should be implemented in the model to
        // calculate the placeable tiles based on the current game state
        // This should return a set of tile identifiers that can be placed on the grid
        // All empty tiles adjacent to already placed tiles should be considered
        // placeable

        Set<Cell> placeableTiles = new HashSet<>();

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

    private void renderCellRange(int minRow, int maxRow, int minCol, int maxCol) {
        System.out.println("renderCellRange() - rendering: rows [" + minRow + "-" + maxRow + "] cols [" + minCol + "-"
                + maxCol + "]");
        System.out.println(
                "renderCellRange() - gridPane has " + currentGameGrid.getChildren().size() + " children before update");

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

}
