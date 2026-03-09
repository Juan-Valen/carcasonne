package carcassonne.View;

import carcassonne.Controller.GameController;
import carcassonne.Model.GameState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class GameHistoryView extends  View {

    GameController gameController = GameController.getInstance();

    @FXML
    private VBox saveContainer;

    @Override
    protected void initialize() {
        super.initialize();
        System.out.println("GameHistoryView.initialize() called");
    }

    @Override
    protected void onAfterStageAvailable() {
        super.onAfterStageAvailable();
        System.out.println("GameHistoryView.onAfterStageAvailable() called");
        renderSavedGames();
    }

    public void onBack(ActionEvent actionEvent) {
        try {
            // show main view via App
            carcassonne.App.getInstance().showScene("/StartView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderSavedGames() {
        // Clear existing saved games
        saveContainer.getChildren().clear();
        System.out.println("Rendering saved games...");

        // Get saved games from GameController
        ArrayList<GameState> savedGames = gameController.getSavedGamesInfo();
        System.out.println("Found " + (savedGames != null ? savedGames.size() : 0) + " saved games.");

        // Render each saved game as a button
        if (savedGames != null) {
            for (GameState savedGame : savedGames) {
                HBox gameEntry = new HBox();
                Label idLabel = new Label("Game ID: " + savedGame.id);
                Label dateLabel = new Label(savedGame.updatedDate.toString());
                Label onlineLabel = new Label(savedGame.online ? "Online" : "Offline");
                gameEntry.getChildren().addAll(idLabel, dateLabel, onlineLabel);
                gameEntry.setOnMouseClicked(event -> {
                    try {
                        // Load the selected game and show the GameView
//                    gameController.loadGame(savedGame.id);
                        carcassonne.App.getInstance().showScene("/GameView.fxml");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                gameEntry.setFocusTraversable(true);
                gameEntry.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-spacing: 10;");

                // Add focus styling
                gameEntry.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                    if (isFocused) {
                        gameEntry.setStyle("-fx-border-color: #0078d4; -fx-border-width: 2; -fx-padding: 10; -fx-spacing: 10; -fx-background-color: #e8f4f8;");
                    } else {
                        gameEntry.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-spacing: 10;");
                    }
                });

                saveContainer.getChildren().add(gameEntry);
            }
        } else {
            Label noSavesLabel = new Label("No saved games found.");
            saveContainer.getChildren().add(noSavesLabel);
        }
    }
}
