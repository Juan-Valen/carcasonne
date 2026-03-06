package carcassonne.View;

import carcassonne.Controller.GameController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.*;

public class GameView extends View {

    // Reference to game controller
    private final GameController controller = GameController.getInstance();
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
    public double cellSize = 0;
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
    // Flag to prevent infinite recursion when enforceScrollConstraints modifies
    // scroll values
    private boolean isEnforcingConstraints = false;
    Map<Character, Image> tileIdToImage = new HashMap<>(); // Cache for tile images based on tile ID

    private int gridSize = 144; // Default grid size

    @FXML
    private ScrollPane gridScreen;

    @FXML
    public StackPane nextTilePane;

    @FXML
    public VBox playerUiBox;

    // Coalesces high-frequency scroll callbacks into one UI update per pulse.
    private boolean scrollUpdateQueued = false;

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

    @Override
    protected void onAfterStageAvailable() {
        controller.setView(this);
        controller.initView();
    }

    @Override
    protected void initialize() {
        super.initialize();
        System.out.println("GameView.initialize() called");
    }

    @Override
    public void onViewShow() {
        super.onViewShow();
    }

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
     * Builds the grid. Called automatically after the view is added to a stage.
     * Uses viewport-based rendering to only create cells visible on screen.
     */
    public void initGrid() {
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
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else {
            System.out.println("Cannot set content because gridScreen is null");
        }
    }

    // // // Handles logic and click handlers for placing a meeple on the currently
    // // // placing tile
    // // private void handleMeeplePlacement(StackPane stackPane, Circle topCircle,
    // // Circle bottomCircle, Circle leftCircle,
    // // Circle rightCircle, int currentPlayerNumber) {
    // // controller.setCurrentMeeplePlacement(-1); // -1 means no meeple, 0-3 for
    // // top/right/bottom/left
    // // final Circle[] selectedCircle = new Circle[1]; // selectedCircle[0] is
    // // current selection
    // //
    // // Runnable resetColors = () -> {
    // // Color playerColor = switch (currentPlayerNumber) {
    // // case 1 -> Color.RED;
    // // case 2 -> Color.BLUE;
    // // case 3 -> Color.GREEN;
    // // case 4 -> Color.YELLOW;
    // // case 5 -> Color.ORANGE;
    // // default -> Color.GRAY;
    // // };
    // //
    // // topCircle.setFill(playerColor.darker());
    // // bottomCircle.setFill(playerColor.darker());
    // // leftCircle.setFill(playerColor.darker());
    // // rightCircle.setFill(playerColor.darker());
    // // };
    // //
    // // // Consumer<Circle> toggleSelection = clicked -> {
    // // // Color lighterColor = switch (currentPlayerNumber) {
    // // // case 1 -> Color.RED.brighter();
    // // // case 2 -> Color.BLUE.brighter();
    // // // case 3 -> Color.GREEN.brighter();
    // // // case 4 -> Color.YELLOW.brighter();
    // // // case 5 -> Color.ORANGE.brighter();
    // // // default -> Color.LIGHTGRAY;
    // // // };
    // // //
    // // // if (selectedCircle[0] == clicked) {
    // // // selectedCircle[0] = null; // unselect if clicked again
    // // // resetColors.run();
    // // // controller.setCurrentMeeplePlacement(
    // // // -1 // no meeple
    // // // );
    // // // } else {
    // // // selectedCircle[0] = clicked;
    // // // resetColors.run();
    // // // controller.setCurrentMeeplePlacement(
    // // // clicked == topCircle ? 0
    // // // : clicked == rightCircle ? 1
    // // // : clicked == bottomCircle ? 2 : clicked == leftCircle ? 3 : -1);
    // // // clicked.setFill(lighterColor);
    // // // }
    // // // };
    // //
    // // topCircle.setOnMouseClicked(e -> {
    // // System.out.println("Top section clicked for meeple placement");
    // // toggleSelection.accept(topCircle);
    // // });
    // //
    // // bottomCircle.setOnMouseClicked(e -> {
    // // System.out.println("Bottom section clicked for meeple placement");
    // // toggleSelection.accept(bottomCircle);
    // // });
    // //
    // // leftCircle.setOnMouseClicked(e -> {
    // // System.out.println("Left section clicked for meeple placement");
    // // toggleSelection.accept(leftCircle);
    // // });
    // //
    // // rightCircle.setOnMouseClicked(e -> {
    // // System.out.println("Right section clicked for meeple placement");
    // // toggleSelection.accept(rightCircle);
    // // });
    // // }

    public void displayCurrentTile(int orientation, Character currentTileId) {
        try {
            if (currentTileId == null) {
                return;
            }

            Image tileImage = tileIdToImage.get(currentTileId);
            if (tileImage != null) {
                ImageView imageView = new ImageView(tileImage);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.rotateProperty().set(-(orientation * 90)); // Rotate based on current rotation state

                nextTilePane.getChildren().clear();
                nextTilePane.getChildren().add(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayPane(Pane pane, int x, int y) {
        currentGameGrid.add(pane, x, y);
    }

    private void displayMeepleOnCell(StackPane pane, int meeplePosition, int player) {
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

        // Position the meeple based on the specified position (0=top, 1=right,
        // 2=bottom, 3=left)
        switch (meeplePosition) {
            case 0 -> pane.setAlignment(meepleCircle, Pos.TOP_CENTER);
            case 1 -> pane.setAlignment(meepleCircle, Pos.CENTER_RIGHT);
            case 2 -> pane.setAlignment(meepleCircle, Pos.BOTTOM_CENTER);
            case 3 -> pane.setAlignment(meepleCircle, Pos.CENTER_LEFT);
            default -> pane.setAlignment(meepleCircle, Pos.CENTER); // Fallback to center if invalid
        }
    }

    private void displayMeeplePlacementOptions(StackPane stackPane, ImageView tileImage, int currentPlayerNumber) {
        double radius = tileImage.getImage().getHeight() * 0.1;
        Circle topCircle = new Circle(radius);
        Circle bottomCircle = new Circle(radius);
        Circle leftCircle = new Circle(radius);
        Circle rightCircle = new Circle(radius);

        Color playerColor = switch (currentPlayerNumber) {
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
        stackPane.setAlignment(topCircle, Pos.TOP_CENTER);
        stackPane.setAlignment(bottomCircle, Pos.BOTTOM_CENTER);
        stackPane.setAlignment(leftCircle, Pos.CENTER_LEFT);
        stackPane.setAlignment(rightCircle, Pos.CENTER_RIGHT);

        // // handleMeeplePlacement(stackPane, topCircle, bottomCircle, leftCircle,
        // // rightCircle, currentPlayerNumber);
    }

    // // public void displayPlayerInfoBoxes(int currentPlayerNumber, int
    // playerCount,
    // // VBox container,
    // // int[] playerMeepleCounts) {
    // // container.getChildren().clear();
    // // container.setSpacing(10); // Add spacing between player boxes
    // //
    // // for (int i = 1; i <= playerCount; i++) {
    // // HBox hbox = new HBox(10); // Add spacing between label and circle
    // // hbox.setStyle("-fx-alignment: center;"); // Center content horizontally
    // and
    // // vertically
    // // hbox.getChildren().add(new Label("Player " + i));
    // //
    // // for (int j = 0; j < playerMeepleCounts[i - 1]; j++) {
    // // Circle circle = new Circle(10);
    // // switch (i) {
    // // case 1 -> circle.setFill(Color.RED);
    // // case 2 -> circle.setFill(Color.BLUE);
    // // case 3 -> circle.setFill(Color.GREEN);
    // // case 4 -> circle.setFill(Color.YELLOW);
    // // case 5 -> circle.setFill(Color.ORANGE);
    // // default -> circle.setFill(Color.GRAY);
    // // }
    // //
    // // hbox.getChildren().add(circle);
    // // }
    // //
    // // // Make each HBox grow to fill equal vertical space
    // // VBox.setVgrow(hbox, javafx.scene.layout.Priority.ALWAYS);
    // //
    // // if (currentPlayerNumber == i) {
    // // hbox.setStyle("-fx-background-color: lightblue; -fx-alignment: center;");
    // //
    // // Highlight current player
    // // }
    // //
    // // container.getChildren().add(hbox);
    // // }
    // //
    // // // Add empty space at the bottom
    // // Region spacer = new Region();
    // // VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
    // // container.getChildren().add(spacer);
    // // }

    public void removeGridPane(Pane pane) {
        if (currentGameGrid != null) {
            currentGameGrid.getChildren().remove(pane);
        }
    }

    public Pane createPane(int x, int y, int orientation, Character tileId, int meeplePosition, int playerNumber) {
        StackPane newPane = new StackPane();
        newPane.setPrefSize(cellSize, cellSize);
        newPane.setMinSize(cellSize, cellSize);
        newPane.setMaxSize(cellSize, cellSize);

        // Tile is placed - show as occupied (green background)
        newPane.setStyle("-fx-background-color: lightgreen; -fx-border-color: gray;");
        ImageView image = new ImageView(tileIdToImage.get(tileId));
        image.rotateProperty().set(-(orientation * 90)); // Rotate based on tile's rotation state
        image.setPreserveRatio(true);
        image.setSmooth(true);
        newPane.getChildren().add(image); // Show tile image if placed

        if (meeplePosition != -1) {
            displayMeepleOnCell(newPane, meeplePosition, playerNumber);
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
                System.out.println("Cell clicked at [" + x + "," + y + "]");
                System.out.println("Tile already placed at [" + x + "," + y + "]");
                return;
            }
        });

        return newPane;
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

    public void clearGrid() {
        currentGameGrid.getChildren().clear();
    }

    public Pane createPane(int x, int y, boolean placeable) {
        StackPane newPane = new StackPane();
        newPane.setPrefSize(cellSize, cellSize);
        newPane.setMinSize(cellSize, cellSize);
        newPane.setMaxSize(cellSize, cellSize);

        if (placeable) {
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
                System.out.println("Cell clicked at [" + x + "," + y + "]");
                if (!placeable) {
                    System.out.println("Cannot place tile at [" + x + "," + y + "] - not adjacent to existing tiles");
                    return;
                }

                // Update visual state immediately to give feedback (will be refreshed again in
                // updateVisibleCellBounds)
                newPane.setStyle("-fx-background-color: lightgreen; -fx-border-color: gray;");

                controller.placeTile(x, y);
            }
        });

        return newPane;
    }

    /**
     * Scrolling (panning) is enabled once tiles are placed.
     * Constraints are enforced separately when tiles fit within viewport.
     */
    public void updateScrollingState(boolean hasTilesPlaced) {
        if (gridScreen == null || gridScreen.pannableProperty().get()) {
            return;
        }
        // Enable panning once tiles are placed (even if they span entire viewport)
        gridScreen.setPannable(hasTilesPlaced);
        System.out.println("updateScrollingState() - panning " + (hasTilesPlaced ? "ENABLED" : "DISABLED"));
    }

    public void setScrollConstraint(int minX, int minY, int maxX, int maxY) {
        minSelectedRow = minX;
        minSelectedCol = minY;
        maxSelectedRow = maxX;
        maxSelectedCol = maxY;
    }

    /**
     * Adds listeners for scroll events to update which cells are visible.
     */
    public void addScrollListeners() {
        if (gridScreen == null) {
            return;
        }

        Runnable queueScrollUpdate = () -> {
            if (scrollUpdateQueued) {
                return;
            }
            scrollUpdateQueued = true;
            Platform.runLater(() -> {
                scrollUpdateQueued = false;
                controller.handleScroll(isEnforcingConstraints);
            });
        };

        // Listen for horizontal scroll
        gridScreen.hvalueProperty().addListener((obs, oldVal, newVal) -> queueScrollUpdate.run());

        // Listen for vertical scroll
        gridScreen.vvalueProperty().addListener((obs, oldVal, newVal) -> queueScrollUpdate.run());

        // Listen for viewport size changes (window resize)
        gridScreen.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> queueScrollUpdate.run());
    }

    /**
     * Updates which cells are visible based on the current scroll position.
     * (plus a buffer for smoother scrolling).
     */
    public int[] updateVisibleBounds() {
        if (gridScreen == null || currentGameGrid == null || cellSize <= 0) {
            System.out.println("updateVisibleCellBounds() - early return: gridScreen=" + (gridScreen != null) +
                    " currentGameGrid=" + (currentGameGrid != null) + " cellSize=" + cellSize);
            return null;
        }

        int[] values = getVisibleBounds();

        // If the visible range hasn't changed significantly, skip update
        if (lastRenderedMinRow == values[0] && lastRenderedMaxRow == values[1] &&
                lastRenderedMinCol == values[2] && lastRenderedMaxCol == values[3]) {
            return null; // No update needed
        }

        System.out.println(
                "updateVisibleBounds() - new range: rows [" + values[0] + "-" + values[1] + "] cols [" + values[2]
                        + "-" + values[3] + "]");
        return values;
    }

    public int[] getVisibleBounds() {
        if (currentGameGrid == null) {
            System.out.println("ERROR: currentGameGrid is null in getVisibleCellBounds");
            return null;
        }

        Bounds viewportBounds = gridScreen.getViewportBounds();
        double viewportWidth = viewportBounds.getWidth();
        double viewportHeight = viewportBounds.getHeight();

        // Current scroll offset in pixels from the left edge of the grid
        double scrollOffsetX = gridScreen.getHvalue()
                * Math.max(0, currentGameGrid.getPrefWidth() - viewportWidth);

        // Current scroll offset in pixels from the top edge of the grid
        double scrollOffsetY = gridScreen.getVvalue()
                * Math.max(0, currentGameGrid.getPrefHeight() - viewportHeight);

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

        return new int[] { minRow, minCol, maxRow, maxCol };
    }

    /**
     * Enforces scroll constraints to prevent scrolling more than one tile beyond
     * the set tiles.
     */
    public void enforceScrollConstraints() {
        if (gridScreen == null || currentGameGrid == null || cellSize <= 0 || minSelectedRow == Integer.MAX_VALUE) {
            return;
        }

        Bounds viewportBounds = gridScreen.getViewportBounds();
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
     * Checks if the placeable tiles exceed the current viewport.
     * Returns true if placeable cells extend beyond ANY viewport edge.
     * Returns false if tiles span across the entire viewport (can't fit them all).
     */
    public boolean shouldEnforceScrollConstraints() {
        if (gridScreen == null || currentGameGrid == null || cellSize <= 0) {
            return false;
        }

        Bounds viewportBounds = gridScreen.getViewportBounds();
        if (viewportBounds == null || viewportBounds.getWidth() <= 0 || viewportBounds.getHeight() <= 0) {
            return false;
        }

        // If no placeable cells found, don't enforce constraints
        if (minSelectedRow == 73) {
            System.out.println("shouldEnforceScrollConstraints() - no placeable cells found");
            return false;
        }

        // Get the bounds of all placeable cells (including the first tile at center if
        // no tiles placed yet)
        int minPlaceableRow = minSelectedRow - 1;
        int minPlaceableCol = minSelectedCol - 1;
        int maxPlaceableRow = maxSelectedRow + 1;
        int maxPlaceableCol = maxSelectedCol + 1;

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
            return false;
        }

        // Calculate the right and bottom edges of the current viewport
        double viewportMaxX = scrollOffsetX + viewportWidth;
        double viewportMaxY = scrollOffsetY + viewportHeight;

        // Check if placeable tiles extend beyond ANY edge of the viewport
        // Tiles exceed if:
        boolean exceedsLeft = minPixelX < scrollOffsetX;
        boolean exceedsRight = maxPixelX > viewportMaxX;
        boolean exceedsTop = minPixelY < scrollOffsetY;
        boolean exceedsBottom = maxPixelY > viewportMaxY;
        boolean tilesExceedViewport = exceedsLeft || exceedsRight || exceedsTop || exceedsBottom;

        System.out.println("  Result: scrolling " + (tilesExceedViewport ? "ENABLED" : "DISABLED"));

        return tilesExceedViewport;
    }
}
