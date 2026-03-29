package carcassonne.View;

import carcassonne.App;
import carcassonne.Controller.GameController;
import carcassonne.Model.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class EndView extends View {

    private final GameController controller = GameController.getInstance();

    @FXML
    public VBox scoreBox;
    public Button btnBack;

    @FXML
    public void initialize() {
        super.initialize();
        btnBack.setText(controller.getText("back"));
    }

    @FXML
    public void onBack(ActionEvent actionEvent) {
        try {
            App.getInstance().showScene("/StartView.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onViewShow() {
        super.onViewShow();
        scoreBox.getChildren().clear();

        Player[] sortedPlayers = Arrays.stream(controller.getPlayers())
                .filter(player -> player != null)
                .sorted(Comparator.comparingInt(Player::getPoints).reversed())
                .toArray(Player[]::new);

        for (int i = 0; i < sortedPlayers.length; i++) {
            Player player = sortedPlayers[i];
            Label label = new Label("#" + (i + 1) + "  " + controller.getText("player") + " " + (i + 1) + ": " + player.getPoints() + " " + controller.getText("pts"));
            scoreBox.getChildren().add(label);
        }
    }
}
