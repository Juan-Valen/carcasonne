package carcassonne.Controller;

import carcassonne.Model.Board;
import carcassonne.Model.Game;
import carcassonne.Model.Spot;
import carcassonne.Model.Tile;
import carcassonne.View.GameView;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;

import java.util.*;

public class GameController {
    private static GameController instance;
    private GameView view; // Reference to the view, can be used to update the UI based on game state
                           // changes
    private Game model;
    private int currentMeeplePlacement = -1; // Placeholder for the current meeple placement, should be set based on the
                                             // game state
    private int currentPlayingPlayer = 1; // Placeholder for the current playing player, should be set based on the game
                                          // state
    private int[] playerMeepleCounts = null; // Placeholder for tracking the number of meeples each player has, should
                                             // be managed in the model
    private int gridSize = 144; // Default grid size
    private int RENDER_BUFFER = 2; // Number of extra rows/columns to render beyond the visible area for smoother
                                   // scrolling
    private int maxPlayers = 5; // Maximum number of players
    private int maxMeeples = 5; // Maximum number of meeples per player, adjust as needed

    private List<Pane> visiblePanes = new ArrayList<>();

    private GameController() {
        // Private constructor to prevent instantiation
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

    public void initView() {
        // Initialize the view with the current game state
        if (view != null) {
            Tile tile = model.getCurrentTile();
            view.displayCurrentTile(tile.getOrientation(), tile.getType());
            // Update the player info boxes in the view based on the current player count
            // and meeple counts
            //
            //
            // view.displayPlayerInfoBoxes(model.getActivePlayer(), model.getMaxPlayers(),
            // 4);// players maple count
            //
            //
            // missing
            // Initialize the grid in the view
            view.initGrid();
            // Listen for scroll changes to update visible cells
            view.addScrollListeners();
            // Update scroll constraints based on initial tile placements (if any)
            renderVisibleTiles(view.getVisibleBounds());
        }
    }

    public void placeTile(int x, int y) {
        // enable panning of grid if it was previously disabled due to no tiles being
        // placed
        view.updateScrollingState(true);

        Tile tile = model.getCurrentTile();
        model.placeTile(x, y, view.createPane(x, y, tile.getOrientation(), tile.getType(), -1, 0));

        // // if (getCurrentMeeplePlacement() != -1 &&
        // // getPlayerMeepleCount(getCurrentPlayingPlayer()) > 0) {
        // // cell.meeple = getCurrentMeeplePlacement(); // Placeholder for getting the
        // // current meeple placement from the
        // // // game state
        // // } else {
        // // cell.meeple = -1; // No meeple placed
        // // }
        // // if (cell.meeple != -1) {
        // // decrementPlayerMeepleCount(getCurrentPlayingPlayer());
        // // }
        Tile currentTile = model.getCurrentTile();
        // display the next tile image
        view.displayCurrentTile(currentTile.getOrientation(), currentTile.getType());
        //
        // // redraw player info boxes to update scores and current player
        // view.renderPlayerInfoBoxes(getCurrentPlayingPlayer(),
        // model.getMaxPlayers(), view.playerUiBox,
        // getPlayersMeepleCounts());

        // Update scroll constraints based on new tile placement
        Spot min = model.getMin();
        Spot max = model.getMax();
        view.setScrollConstraint(min.getX(), min.getY(), max.getX(), max.getY());

        // Only enforce constraints if tiles now exceed the viewport
        if (view.shouldEnforceScrollConstraints()) {
            view.enforceScrollConstraints();
        }

        int[] visibleEdges = view.getVisibleBounds();
        if (visibleEdges != null) {
            // Tile state changed; rebuild currently visible cells once to refresh
            // styles/content.
            clearGrid();
            renderVisibleTiles(visibleEdges);
        }
    }

    public void rotateTile() {
        model.rotateTile(true);

        // Update the displayed tile in the view to reflect the new rotation
        Tile tile = model.getCurrentTile();
        view.displayCurrentTile(tile.getOrientation(), tile.getType());
    }

    // Handles events from scrolling or resizing the window, enforces scroll
    // constraints and updates visible cells
    public void handleScroll(Boolean isEnforcingConstraints) {
        // Skip if we're already enforcing constraints (prevents infinite recursion due
        // to function scrolls causing more scrolls)
        if (isEnforcingConstraints) {
            return;
        }

        // Enforce constraints if tiles are placed (but don't disable panning)
        if (model.getMin().getX() != 73) {
            view.enforceScrollConstraints();
        }
        int[] visibleEdges = view.getVisibleBounds();
        // Render new visible cells if scroll resulted in a change of visible cells
        if (visibleEdges != null) {
            renderVisibleTiles(visibleEdges);
        }
    }

    // private int[] getPlayersMeepleCounts() {
    // int[] counts = new int[model.getMaxPlayers()];
    // for (int i = 0; i < model.getMaxPlayers(); i++) {
    // counts[i] = getPlayerMeepleCount(i + 1); // Player numbers are 1-indexed
    // }
    // return counts;
    // }

    // Renders the cells that are currently visible on the screen based on the
    // current scroll position and grid size, should only render cells that have
    // changed visibility since the last render for performance optimization
    private void renderVisibleTiles(int[] visibleBounds) {
        // cellbounds format: {minRow, minCol, maxRow, maxCol}
        if (visibleBounds == null) {
            return;
        }

        Board board = model.getBoard();
        clearGrid();
        for (int row = visibleBounds[0]; row <= visibleBounds[2]; row++) {
            for (int col = visibleBounds[1]; col <= visibleBounds[3]; col++) {
                Tile tile = board.getTile(row, col);
                Pane newPane;
                if (tile != null) {
                    newPane = tile.getPane();
                } else if (model.getAvailableSpots().contains(new Spot(row, col))) {
                    newPane = view.createPane(row, col, false);
                } else {
                    newPane = view.createPane(row, col, false);
                }

                // Cache the rendered pane for this cell so we can reuse it if the cell remains
                // visible, and remove it from the grid if it goes out of view
                visiblePanes.add(newPane);
                view.displayPane(newPane, row, col);
            }
        }

        // // Iterator<Map.Entry<Cell, Pane>> iterator =
        // // visiblePanes.entrySet().iterator();
        // // while (iterator.hasNext()) {
        // // Map.Entry<Cell, Pane> entry = iterator.next();
        // // if (!targetVisibleCells.contains(entry.getKey())) {
        // // view.currentGameGrid.getChildren().remove(entry.getValue());
        // // iterator.remove();
        // // }
        // // }
    }

    private void clearGrid() {
        // for (Pane pane : visiblePanes) {
        // view.removeGridPane(pane);
        //
        // }
        visiblePanes.clear();
        view.clearGrid();
    }

    private void setNextPlayingPlayerInModel() {
        // Placeholder function to set the next playing player in the game state
        if (currentPlayingPlayer == model.getMaxPlayers()) {
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

    private void decrementPlayerMeepleCountInModel(int player) {
        // Placeholder function to decrement the meeple count for a player in the game
        // state

        if (player >= 1 && player <= model.getMaxPlayers()) {
            playerMeepleCounts[player - 1] = Math.max(0, playerMeepleCounts[player - 1] - 1); // Decrement meeple count
                                                                                              // for the player,
                                                                                              // ensuring it doesn't go
                                                                                              // below 0
        }
    }

    public void setMaxPlayer(int length) {
        model.setMaxPlayer(length);
    }

    public int getMaxPlayers() {
        return model.getMaxPlayers();
    }

    private int getCurrentMeeplePlacementFromModel() {
        // 0 = top, 1 = right, 2 = bottom, 3 = left, -1 = no meeple
        // Placeholder function to get the current meeple placement from the game state
        return currentMeeplePlacement; // Should return the actual meeple placement from the model
    }
}
