package carcassonne.controller;

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

    public static class Cell {
        public final int row;
        public final int col;
        public Character tileId = null; // ID of the tile placed in this cell
        public boolean placed = false; // Whether a tile has been placed in this cell
        public int rotation = 0; // Rotation of the tile in this cell (0, 90, 180, 270)

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


    public int getGridSize() {
        return gridSize;
    }

    public void placeTile (int row, int col) {
        Cell cell = new Cell(row, col);
        cell.tileId = getCurrentTileId(); // Placeholder for getting the current tile ID from the game state
        cell.placed = true;
        cell.rotation = currentRotation;
        getNextTile(); // Update the current tile to the next tile after placing
        PlacedTilePositions.add(cell);
    }

    public Character getCurrentTileId() {
        // Placeholder function to get the current tile ID from the game state
        // This should return the ID of the tile that is currently being placed

        return currentTileId; // Placeholder for getting the current tile from the model
    }

    public void rotateTile () {
        if (!(currentRotation >= 3)) {
            currentRotation += 1;
        } else {
            currentRotation = 0;
        }
    }

    public int getCurrentRotation() {
        return currentRotation;
    }

    public Set<Cell> getPlaceableCells() {
        Set<Cell> placeableTiles = new HashSet<>();
        placeableTiles = calculatePlaceableCells();
        return placeableTiles;
    }

    public Set<Cell> getPlacedTiles() {
        return new HashSet<>(PlacedTilePositions);
    }

    public void getNextTile() {
        // This should return the next tile based on the game state
        currentTileId = getNextTileFromModel(); // Placeholder for getting the next tile from the model
        currentRotation = 0; // Reset rotation for the new tile
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

    // Stuff that should be in the model but is here for testing purposes, should be moved to the model later

    private Set<Cell> PlacedTilePositions = new HashSet<>();

    final Random random = new Random();

    List<Character> keys = Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X'); // Example list of tile identifiers, should be replaced with actual tile data from the model

    private Character currentTileId = getNextTileFromModel(); // Placeholder for the current tile, should be set based on the game state

    private int currentRotation = 0; // Placeholder for the current tile rotation, should be set based on the game state

    private Set<Cell> calculatePlaceableCells() {
        // Placeholder function in the controller, should be implemented in the model to calculate the placeable tiles based on the current game state
        // This should return a set of tile identifiers that can be placed on the grid
        // All empty tiles adjacent to already placed tiles should be considered placeable

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

    private Character getNextTileFromModel() {
        // Random instance for picking random tiles
            return keys.get(random.nextInt(keys.size()));
    }
}
