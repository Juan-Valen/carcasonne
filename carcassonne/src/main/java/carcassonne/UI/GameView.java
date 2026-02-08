package carcassonne.UI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class GameView extends Application {

    int gridSize = 144;
    private Stage primaryStage;

    @FXML
    public ScrollPane gridScreen;

    @Override
    public void start (Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Carcassonne UI.fxml"));
        fxmlLoader.setController(this);
        Parent view = fxmlLoader.load();

        primaryStage.setTitle("Carcassonne Game");
        primaryStage.setScene(new Scene(view));

        primaryStage.show();

        Platform.runLater(this::initializeGrid);
    }

    @FXML
    public void initialize() {

    }

    private void initializeGrid() {
        GridPane gameGrid = new GridPane();

        gameGrid.gridLinesVisibleProperty().setValue(true);

        double cellSize = primaryStage.getWidth() / 4; // Adjust cell size based on window width

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Pane cell = new Pane();
                cell.setPrefSize(cellSize, cellSize); // Set preferred size for each cell
                gameGrid.add(cell, col, row);
            }
        }

        gridScreen.setContent(gameGrid);

        // Center the ScrollPane after the scene is shown
        gridScreen.setHvalue(0.5);
        gridScreen.setVvalue(0.5);
    }
}
