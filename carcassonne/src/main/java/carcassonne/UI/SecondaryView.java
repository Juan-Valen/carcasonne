package carcassonne.UI;

import javafx.fxml.FXML;

public class SecondaryView {

    @FXML
    public void onBack() {
        try {
            // show main view via MainApp
            carcassonne.MainApp.getInstance().showScene("/Carcassonne UI.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

