package carcassonne.View;

import carcassonne.Controller.GameController;
import carcassonne.Model.GameState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;

public class GameHistoryView extends  View {

    GameController gameController = GameController.getInstance();

    @FXML
    private VBox saveContainer;

    @Override
    protected void initialize() {
        super.initialize();
        System.out.println("GameHistoryView.initialize() called");
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

        // Get saved games from GameController
        ArrayList<GameState> savedGames = gameController.getSavedGamesInfo();

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
            }
        } else {
            Label noSavesLabel = new Label("No saved games found.");
            saveContainer.getChildren().add(noSavesLabel);
        }
    }
}
