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

    private int maxPlayers = 5; // Maximum number of players

    private int maxMeeples = 5; // Maximum number of meeples per player, adjust as needed

    public int getGridSize() {
        return gridSize;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setCurrentPlayerCount(int currentPlayerCount) {
        setCurrentPlayerCountToModel(currentPlayerCount); // Update the player count in the model
    }

    public int getCurrentPlayerCount() {
        return getCurrentPlayerCountFromModel(); // Get the current player count from the model
    }

    public int getCurrentPlayingPlayer() {
        return getCurrentPlayingPlayerFromModel(); // Get the current playing player from the model
    }

    public void setNextPlayingPlayer() {
        setNextPlayingPlayerInModel();
    }

    public void placeTile (int row, int col) {
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

    public void setCurrentMeeplePlacement(int position) {
        // 0 = top, 1 = right, 2 = bottom, 3 = left, -1 = no meeple
        // Placeholder function to set the current meeple placement in the game state
        setCurrentMeeplePlacementToModel(position);
    }

    public int getCurrentMeeplePlacement() {
        // 0 = top, 1 = right, 2 = bottom, 3 = left, -1 = no meeple
        return getCurrentMeeplePlacementFromModel();
    }

    public void decrementPlayerMeepleCount(int player) {
            // Update the model with the new meeple count for the player
        decrementPlayerMeepleCountInModel(player);
    }

    public int getPlayerMeepleCount(int player) {
        // Placeholder function to get the current meeple count for a player from the game state
        return getPlayerMeepleCountFromModel(player);
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

    private int currentPlayerCount = 2; // the current number of players

    private Set<Cell> PlacedTilePositions = new HashSet<>();

    final Random random = new Random();

    List<Character> keys = Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X'); // Example list of tile identifiers, should be replaced with actual tile data from the model

    private Character currentTileId = getNextTileFromModel(); // Placeholder for the current tile, should be set based on the game state

    private int currentRotation = 0; // Placeholder for the current tile rotation, should be set based on the game state

    private int currentMeeplePlacement = -1; // Placeholder for the current meeple placement, should be set based on the game state

    private int currentPlayingPlayer = 1; // Placeholder for the current playing player, should be set based on the game state

    private int [] playerMeepleCounts = null; // Placeholder for tracking the number of meeples each player has, should be managed in the model

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
