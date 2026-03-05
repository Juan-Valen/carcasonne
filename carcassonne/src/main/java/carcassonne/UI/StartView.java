package carcassonne.UI;

import carcassonne.controller.GameController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;

public class StartView extends View {

    GameController gameController = GameController.getInstance();

    @FXML
    public VBox rootContainer;

    @FXML
    public Label enterPlayerNumPrompt;

    @FXML
    public Spinner<Integer> playerNumSpinner;

    @FXML
    public void onBack() {
        try {
            // show main view via MainApp
            carcassonne.MainApp.getInstance().showScene("/Carcassonne UI.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("current player count: " + playerNumSpinner.getValue());
        gameController.setCurrentPlayerCount(playerNumSpinner.getValue());

    }

    @FXML
    protected void initialize() {
        super.initialize();
        System.out.println("StartView.initialize() called");

        playerNumSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, gameController.getMaxPlayers(), 2));
    }
}

