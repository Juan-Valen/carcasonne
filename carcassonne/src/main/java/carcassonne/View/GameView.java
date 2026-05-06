package carcassonne.View;

import carcassonne.Controller.GameController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.*;
import java.util.function.Consumer;

public class GameView extends View {

    @FXML
    public Button btnRotateTile;
    @FXML
    private ScrollPane gridScreen;
    @FXML
    public StackPane nextTilePane;
    @FXML
    public VBox playerUiBox;
    @FXML
    public Button quitButton;
    
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
    private int lastRenderedMinY = -1;
    private int lastRenderedMaxY = -1;
    private int lastRenderedMinX = -1;
    private int lastRenderedMaxX = -1;
    private static final int RENDER_BUFFER = 2; // Extra cells to render outside viewport for smoother scrolling
    // Scroll constraints based on selected tiles
    private int minSelectedY = Integer.MAX_VALUE;
    private int maxSelectedY = Integer.MIN_VALUE;
    private int minSelectedX = Integer.MAX_VALUE;
    private int maxSelectedX = Integer.MIN_VALUE;
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

    private int gridSize = 144; // Default grid size





    @Override
    protected void onAfterStageAvailable() {
        controller.setView(this);
        controller.initView();
    }

    @Override
    protected void initialize() {
        super.initialize();
        gridScreen.setPannable(false);

        quitButton.setText(controller.getText("game.quit"));
        btnRotateTile.setText(controller.getText("rotate.tile"));
    }

    @Override
    public void onViewShow() {
        super.onViewShow();
    }

    // Coalesces high-frequency scroll callbacks into one UI update per pulse.
    private boolean scrollUpdateQueued = false;

    @FXML
    public void rotateCurrentTile() {
        controller.rotateTile();
    }

    @FXML
    public void onQuit(ActionEvent actionEvent) {
        try {
            // Save game state
            controller.saveGame();
            // show main view via MainApp
            carcassonne.App.getInstance().showScene("/StartView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // Handles logic and click handlers for placing a meeple on the currently
    // placing tile
    private void handleMeeplePlacement(
            StackPane stackPane,
            Circle topCircle,
            Circle bottomCircle,
            Circle leftCircle,
            Circle rightCircle,
            int currentPlayerNumber) {
        final Circle[] selectedCircle = new Circle[1]; // selectedCircle[0] is current selection

        Runnable resetColors = () -> {
            Color playerColor = switch (currentPlayerNumber) {
                case 0 -> Color.RED;
                case 1 -> Color.BLUE;
                case 2 -> Color.GREEN;
                case 3 -> Color.YELLOW;
                case 4 -> Color.ORANGE;
                default -> Color.GRAY;
            };

            topCircle.setFill(playerColor.darker());
            bottomCircle.setFill(playerColor.darker());
            leftCircle.setFill(playerColor.darker());
            rightCircle.setFill(playerColor.darker());
        };

        Consumer<Circle> toggleSelection = clicked -> {
            Color lighterColor = switch (currentPlayerNumber) {
                case 0 -> Color.RED.brighter();
                case 1 -> Color.BLUE.brighter();
                case 2 -> Color.GREEN.brighter();
                case 3 -> Color.YELLOW.brighter();
                case 4 -> Color.ORANGE.brighter();
                default -> Color.LIGHTGRAY;
            };

            if (selectedCircle[0] == clicked) {
                selectedCircle[0] = null; // unselect if clicked again
                resetColors.run();
                controller.placeMeeple(-1); // -1 indicates no meeple
            } else {
                selectedCircle[0] = clicked;
                resetColors.run();
                controller.placeMeeple(
                        clicked == topCircle ? 0
                                : clicked == rightCircle ? 1
                                        : clicked == bottomCircle ? 2 : clicked == leftCircle ? 3 : -1);
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

    public void displayCurrentTile(int orientation, Character currentTileId, int activePlayer, boolean currentPlayerHasMeeples) {
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
                if (currentPlayerHasMeeples) {
                    displayMeeplePlacementOptions(nextTilePane, imageView, activePlayer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayPane(Pane pane, int x, int y) {
        if (currentGameGrid == null || pane == null) {
            return;
        }
        currentGameGrid.add(pane, x, y);
    }

    private void displayMeeplePane(StackPane pane, int meeplePosition, int player) {
        Color playerColor = switch (player) {
            case 0 -> Color.RED;
            case 1 -> Color.BLUE;
            case 2 -> Color.GREEN;
            case 3 -> Color.YELLOW;
            case 4 -> Color.ORANGE;
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
            case 0 -> Color.RED;
            case 1 -> Color.BLUE;
            case 2 -> Color.GREEN;
            case 3 -> Color.YELLOW;
            case 4 -> Color.ORANGE;
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

        handleMeeplePlacement(stackPane, topCircle, bottomCircle, leftCircle, rightCircle, currentPlayerNumber);
    }

    public void displayPlayerInfoBoxes(int currentPlayerNumber, int playerCount, int[] playerMeepleCounts, int[] playerPoints) {
        playerUiBox.getChildren().clear();
        playerUiBox.setSpacing(10); // Add spacing between player boxes

        for (int i = 0; i < playerCount; i++) {
            HBox hbox = new HBox(10); // Add spacing between label and circle
            hbox.setStyle("-fx-alignment: center;"); // Center content horizontally and vertically
            hbox.getChildren().add(new Label(controller.getText("player") + " " + (i+1)));

            for (int j = 0; j < playerMeepleCounts[i]; j++) {
                Circle circle = new Circle(10);
                switch (i) {
                    case 0 -> circle.setFill(Color.RED);
                    case 1 -> circle.setFill(Color.BLUE);
                    case 2 -> circle.setFill(Color.GREEN);
                    case 3 -> circle.setFill(Color.YELLOW);
                    case 4 -> circle.setFill(Color.ORANGE);
                    default -> circle.setFill(Color.GRAY);
                }

                hbox.getChildren().add(circle);
            }

            hbox.getChildren().add(new Label("  " + controller.getText("points") + ": " + playerPoints[i]));

            // Make each HBox grow to fill equal vertical space
            VBox.setVgrow(hbox, javafx.scene.layout.Priority.ALWAYS);

            if (currentPlayerNumber == i) {
                hbox.setStyle("-fx-background-color: lightblue; -fx-alignment: center;"); // Highlight current player
            }

            playerUiBox.getChildren().add(hbox);
        }

        // Add empty space at the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        playerUiBox.getChildren().add(spacer);
    }

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
            displayMeeplePane(newPane, meeplePosition, playerNumber);
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
        minSelectedX = minX;
        minSelectedY = minY;
        maxSelectedX = maxX;
        maxSelectedY = maxY;
    }

    /**
     * Adds listeners for scroll events to update which cells are visible.
     */
    public void addScrollListeners() {
        if (gridScreen == null)
            return;

        gridScreen.hvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isEnforcingConstraints) {
                // Enforce constraints IMMEDIATELY to prevent the "bounce"
                enforceScrollConstraints();

                // Queue the expensive rendering logic for the next pulse
                if (!scrollUpdateQueued) {
                    scrollUpdateQueued = true;
                    Platform.runLater(() -> {
                        scrollUpdateQueued = false;
                        controller.handleScroll(false);
                    });
                }
            }
        });

        gridScreen.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isEnforcingConstraints) {
                enforceScrollConstraints();
                if (!scrollUpdateQueued) {
                    scrollUpdateQueued = true;
                    Platform.runLater(() -> {
                        scrollUpdateQueued = false;
                        controller.handleScroll(false);
                    });
                }
            }
        });
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
        if (lastRenderedMinY == values[0] && lastRenderedMaxY == values[1] &&
                lastRenderedMinX == values[2] && lastRenderedMaxX == values[3]) {
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
        int minY = Math.max(0, (int) Math.floor(scrollOffsetY / cellSize) - RENDER_BUFFER);
        int maxY = Math.min(gridSize - 1,
                (int) Math.ceil((scrollOffsetY + viewportHeight) / cellSize) + RENDER_BUFFER);
        int minX = Math.max(0, (int) Math.floor(scrollOffsetX / cellSize) - RENDER_BUFFER);
        int maxX = Math.min(gridSize - 1,
                (int) Math.ceil((scrollOffsetX + viewportWidth) / cellSize) + RENDER_BUFFER);

        return new int[] { minY, minX, maxY, maxX };
    }

    /**
     * Enforces scroll constraints so the viewport can never go more than 1 cell
     * beyond the furthest placed tile in any direction.
     *
     * Two independent pixel-space limits are applied to the current scroll offset:
     *   leftLimit  = (minPlacedX - 1) * cellSize       — how far left  the viewport top-left can go
     *   rightLimit = (maxPlacedX + 2) * cellSize - vpW  — how far right the viewport top-left can go
     *
     * These are applied as separate clamps, never compared against each other.
     * This works correctly whether tiles are narrower or wider than the viewport.
     */
    public void enforceScrollConstraints() {
        if (gridScreen == null || currentGameGrid == null || cellSize <= 0 || minSelectedY == Integer.MAX_VALUE) {
            return;
        }

        Bounds viewportBounds = gridScreen.getViewportBounds();
        if (viewportBounds == null || viewportBounds.getWidth() <= 0)
            return;

        double gridWidth = currentGameGrid.getPrefWidth();
        double gridHeight = currentGameGrid.getPrefHeight();
        double viewportWidth = viewportBounds.getWidth();
        double viewportHeight = viewportBounds.getHeight();

        double maxScrollPixelX = gridWidth - viewportWidth;
        double maxScrollPixelY = gridHeight - viewportHeight;

        if (maxScrollPixelX <= 0 || maxScrollPixelY <= 0)
            return;

        // Current scroll position in pixels
        double scrollX = gridScreen.getHvalue() * maxScrollPixelX;
        double scrollY = gridScreen.getVvalue() * maxScrollPixelY;

        // minScrollX: viewport can't go further LEFT  than (minPlacedX - 1) cell start
        //             = the floor of the allowed scroll range
        // maxScrollX: viewport can't go further RIGHT than where (maxPlacedX + 2) cell
        //             end is still visible = (maxPlacedX + 2) * cellSize - viewportWidth
        //             = the ceiling of the allowed scroll range
        //
        // Both cases handled by a single clamp [minScrollX, maxScrollX]:
        //   tiles narrower than viewport: maxScrollX < minScrollX possible only near
        //     grid edge (both are clamped to [0, maxScrollPixel] so this won't happen
        //     in practice for a centred layout)
        //   tiles wider than viewport: maxScrollX > minScrollX — valid range, user
        //     can pan freely between the two 1-cell buffer limits
        double minScrollX = Math.max(0,               (minSelectedX - 1) * cellSize);
        double maxScrollX = Math.min(maxScrollPixelX, (maxSelectedX + 2) * cellSize - viewportWidth);
        double minScrollY = Math.max(0,               (minSelectedY - 1) * cellSize);
        double maxScrollY = Math.min(maxScrollPixelY, (maxSelectedY + 2) * cellSize - viewportHeight);

        // If the tile span is smaller than the viewport, maxScrollX can be negative
        // before the Math.max(0) clamp, meaning the tile group fits entirely with room
        // to spare. In that case allow free scroll (don't constrain).
        if (maxScrollX < minScrollX) maxScrollX = minScrollX;
        if (maxScrollY < minScrollY) maxScrollY = minScrollY;

        double targetX = Math.max(minScrollX, Math.min(scrollX, maxScrollX));
        double targetY = Math.max(minScrollY, Math.min(scrollY, maxScrollY));

        double targetH = targetX / maxScrollPixelX;
        double targetV = targetY / maxScrollPixelY;

        if (Math.abs(targetH - gridScreen.getHvalue()) > 1.0e-4 || Math.abs(targetV - gridScreen.getVvalue()) > 1.0e-4) {
            isEnforcingConstraints = true;
            gridScreen.setHvalue(targetH);
            gridScreen.setVvalue(targetV);
            isEnforcingConstraints = false;
        }
    }

    /**
     * Always returns true when placed tiles exist, since enforceScrollConstraints()
     * now handles all cases (both small and large tile spans) without collapsing.
     */
    public boolean shouldEnforceScrollConstraints() {
        return gridScreen != null && currentGameGrid != null
                && cellSize > 0 && minSelectedY != Integer.MAX_VALUE;
    }

    public void endGame() {
            try {
                carcassonne.App.getInstance().showScene("/EndGameView.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
