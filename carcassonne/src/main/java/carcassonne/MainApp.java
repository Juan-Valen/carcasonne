package carcassonne;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import carcassonne.UI.GameView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainApp extends Application {

    private static MainApp instance;
    private Stage primaryStage;
    private final Map<String, Scene> sceneCache = new HashMap<>();
    private final Map<String, Object> controllers = new HashMap<>();

    public MainApp() {
        instance = this;
    }

    public static MainApp getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        showMainView();
    }

    public void showMainView() throws IOException {
        showScene("/Carcassonne UI.fxml");
    }

    public void showScene(String resource) throws IOException {
        Scene scene = sceneCache.get(resource);
        Object ctrl = controllers.get(resource);

        if (scene == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            ctrl = loader.getController();
            Scene newScene = new Scene(root);
            sceneCache.put(resource, newScene);
            controllers.put(resource, ctrl);
            scene = newScene;
        }

        // If controller exists and is GameView, pass the stage (initGrid will be called after show)
        if (ctrl instanceof GameView) {
            ((GameView) ctrl).setPrimaryStage(primaryStage);
        }

        primaryStage.setTitle("Carcassonne Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize grid after the stage is shown so width/viewport are valid
        if (ctrl instanceof GameView) {
            Object finalCtrl = ctrl;
            Platform.runLater(() -> {
                try {
                    ((GameView) finalCtrl).initGrid();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public Stage getPrimaryStage() { return primaryStage; }

    @SuppressWarnings("unchecked")
    public <T> T getController(String resource, Class<T> cls) {
        return (T) controllers.get(resource);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
