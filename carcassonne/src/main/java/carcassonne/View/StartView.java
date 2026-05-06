package carcassonne.View;

import carcassonne.App;
import carcassonne.Controller.GameController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class StartView extends View {

    @FXML
    public Button loginButton;
    @FXML
    public ComboBox<String> langComboBox;
    @FXML
    public Button btnNewGame;
    @FXML
    Button savedGamesButton;
    @FXML
    public VBox rootContainer;
    @FXML
    public Label enterPlayerNumPrompt;
    @FXML
    public Spinner<Integer> playerNumSpinner;
    
    GameController gameController = GameController.getInstance();
    App mainApp = App.getInstance();
    private String selectedLanguageCode = "en";





    @FXML
    protected void initialize() {
        super.initialize();
        playerNumSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 2));

        langComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(String code) {
                return getLanguageDisplayText(code);
            }

            @Override
            public String fromString(String string) {
                return selectedLanguageCode;
            }
        });

        langComboBox.setItems(FXCollections.observableArrayList("en", "ch", "ru"));
        langComboBox.setValue(selectedLanguageCode);

        langComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedLanguageCode = newValue;
                gameController.setLanguage(selectedLanguageCode);
                setLang();
            }
        });

        setLang();
    }

    private String getLanguageDisplayText(String code) {
        return switch (code) {
            case "ch" -> gameController.getText("chinese");
            case "ru" -> gameController.getText("russian");
            default -> gameController.getText("english");
        };
    }

    @Override
    public void onViewShow() {
        super.onViewShow();
        if (gameController.getCurrentUser() != null) {
            if (savedGamesButton == null) {
                savedGamesButton = new Button(gameController.getText("game.saved"));
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
            loginButton.setText(gameController.getText("login"));
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

    public void setLang() {
        if (savedGamesButton != null) {
            savedGamesButton.setText(gameController.getText("game.saved"));
        }

        enterPlayerNumPrompt.setText(gameController.getText("players.prompt"));

        loginButton.setText(gameController.getText("login"));

        btnNewGame.setText(gameController.getText("game.new"));

        if (gameController.getCurrentUser() != null) {
            loginButton.setText(gameController.getText("login"));
        } else {
            loginButton.setText(gameController.getText("user.change"));
        }

        if (savedGamesButton != null) {
            savedGamesButton.setText(gameController.getText("game.saved"));
        }

        langComboBox.setItems(FXCollections.observableArrayList("en", "ch", "ru"));
        if (!langComboBox.getItems().contains(selectedLanguageCode)) {
            selectedLanguageCode = "en";
        }
        if (!selectedLanguageCode.equals(langComboBox.getValue())) {
            langComboBox.setValue(selectedLanguageCode);
        }
    }
}
