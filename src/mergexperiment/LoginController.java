package mergexperiment;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LoginController extends Application {
    GridPane loginPane = new GridPane();
    BorderPane screenPane = new BorderPane();
    Scene mainScene = new Scene(screenPane);

    public void start(Stage stage) {
        stage.setScene(mainScene);
        stage.setMaximized(true);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                //TODO: Handle closing
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
