package carcassonne.View;

import carcassonne.App;
import carcassonne.Controller.GameController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;

public class StartView extends View {

    public Button loginButton;
    GameController gameController = GameController.getInstance();

    App mainApp = App.getInstance();

    @FXML
    public VBox rootContainer;

    @FXML
    public Label enterPlayerNumPrompt;

    @FXML
    public Spinner<Integer> playerNumSpinner;

    @FXML
    protected void initialize() {
        super.initialize();
        System.out.println("StartView.initialize() called");
        playerNumSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 2));
        if (gameController.getCurrentUser() != null) {
            loginButton.setText("Game history");
        }
    }

    @FXML
    public void onBack() {
        try {
            // show main view via App
            mainApp.showScene("/Carcassonne UI.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("current player count: " + playerNumSpinner.getValue());
        gameController.setMaxPlayer(playerNumSpinner.getValue());

    }

    @FXML
    public void onLogin() {
        try {
            if (gameController.getCurrentUser() != null) {
                // show game history view via App
                mainApp.showScene("/GameHistoryView.fxml");
                return;
            }
            // show login view via App
            mainApp.showScene("/LoginView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
