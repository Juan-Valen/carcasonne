package carcassonne.View;

import carcassonne.Controller.GameController;
import carcassonne.Model.User;
import carcassonne.Service.databaseService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginView extends View {

    public Label errorLabel;
    public Button btnLogin;
    public Button btnBack;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField passWordField;

    public void initialize() {
        super.initialize();

        btnBack.setText(GameController.getInstance().getText("back"));
        btnLogin.setText(GameController.getInstance().getText("login"));

        userNameField.setPromptText(GameController.getInstance().getText("username"));
        passWordField.setPromptText(GameController.getInstance().getText("password"));
    }

    @FXML
    private void onLogin() {
        try {
            String username = userNameField.getText();
            String password = passWordField.getText();

            User loggedInUser = databaseService.getInstance().loginUser(username, password);

            if (loggedInUser != null) {
                GameController.getInstance().setCurrentUser(loggedInUser);
                // show main view via MainApp
                carcassonne.App.getInstance().showScene("/StartView.fxml");
            } else {
                errorLabel.setText("Invalid username or password. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBack(ActionEvent actionEvent) {
        try {
            // show main view via MainApp
            carcassonne.App.getInstance().showScene("/StartView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
