package carcassonne.UI;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class SecondaryView extends View {

    @FXML
    public VBox rootContainer;

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

