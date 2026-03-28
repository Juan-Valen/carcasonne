package carcassonne.View;

import carcassonne.App;
import carcassonne.Controller.GameController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class StartView extends View {

    public Button loginButton;
    public ComboBox langComboBox;
    GameController gameController = GameController.getInstance();

    App mainApp = App.getInstance();

    Button savedGamesButton;

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

        langComboBox.setItems(FXCollections.observableArrayList("English", "French", "German"));
        langComboBox.setValue("English");  // Set default value
        
        // Listener for when ComboBox value changes
        langComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue.toString().toLowerCase()) {
                    case "english":
                        enterPlayerNumPrompt.setText("Enter number of players:");
                        break;
                    case "french":
                        enterPlayerNumPrompt.setText("Entrez le nombre de joueurs:");
                        break;
                    case "german":
                        enterPlayerNumPrompt.setText("Geben Sie die Anzahl der Spieler ein:");
                        break;
                    default:
                        enterPlayerNumPrompt.setText("Enter number of players:");
                }
            }
        });
    }

    @Override
    public void onViewShow() {
        super.onViewShow();
        System.out.println("StartView.onViewShow() called");
        if (gameController.getCurrentUser() != null) {
            loginButton.setText("Change User");
            if (savedGamesButton == null) {
                savedGamesButton = new Button("Saved Games");
                savedGamesButton.setOnAction(e -> {
                    try {
                        mainApp.showScene("/GameHistoryView.fxml");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                if (!rootContainer.getChildren().contains(savedGamesButton)) {
                    rootContainer.getChildren().add(savedGamesButton);
                }
            }
        } else {
            loginButton.setText("Login");
        }
    }

    @FXML
    public void onBack() {
        int selectedPlayers = playerNumSpinner.getValue();

        try {
            // show main view via App
            mainApp.showScene("/Carcassonne UI.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }

        gameController.newgame(selectedPlayers);
        System.out.println("current player count: " + selectedPlayers);
    }

    @FXML
    public void onLogin() {
        try {
            // show login view via App
            mainApp.showScene("/LoginView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
