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
        public int tileId; // ID of the tile placed in this cell

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

    private Set<Cell> PlacedTilePositions = new HashSet<>();

    public int getGridSize() {
        return gridSize;
    }

    public void placeTile (int row, int col) {
        Cell cell = new Cell(row, col);
        cell.tileId = getCurrentTileId(); // Placeholder for getting the current tile ID from the game state
        PlacedTilePositions.add(cell);
    }

    public int getCurrentTileId() {
        // Placeholder function to get the current tile ID from the game state
        // This should return the ID of the tile that is currently being placed
        return 0; // Replace with actual logic to retrieve the current tile ID
    }

    public Set<Cell> getPlaceableCells() {
        Set<Cell> placeableTiles = new HashSet<>();
        placeableTiles = calculatePlaceableCells();
        return placeableTiles;
    }

    public Set<Cell> getPlacedTiles() {
        return new HashSet<>(PlacedTilePositions);
    }


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
}
