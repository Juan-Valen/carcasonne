package carcassonne.UI;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginView extends View {
    @FXML
    private TextField userNameField;
    @FXML
    private TextField passWordField;

    @FXML
    private void onLogin() {
        try {
            String username = userNameField.getText();
            String password = passWordField.getText();
            System.out.println("Login attempted with username: " + username + " and password: " + password);
            // show main view via MainApp
            carcassonne.MainApp.getInstance().showScene("/Carcassonne UI.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
