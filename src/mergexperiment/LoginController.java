package mergexperiment;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class LoginController extends Application {
    GridPane loginPane = new GridPane();
    BorderPane screenPane = new BorderPane();
    Scene mainScene = new Scene(screenPane, 300, 300);

    Label loginLabel = new Label("Login to Database");
    Label usernameLabel = new Label("Username:");
    Label passwordLabel = new Label("Password: ");
    TextField username = new TextField();
    PasswordField password = new PasswordField();
    Button loginBtn = new Button("Login");

    static String[] mArgs;

    public void start(Stage stage) {
        stage.setScene(mainScene);

        Font smallFont = new Font(10);
        usernameLabel.setFont(smallFont);
        passwordLabel.setFont(smallFont);

        screenPane.setCenter(loginPane);
        loginPane.add(loginLabel, 0, 0);
        loginPane.add(usernameLabel, 0, 1);
        loginPane.add(username, 0, 2);
        loginPane.add(passwordLabel, 0, 3);
        loginPane.add(password, 0, 4);
        loginPane.add(loginBtn, 0, 5);
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setVgap(10);
//        loginPane.setPadding(new Insets(20, 20, 20, 20));
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    DBUtil.disconnect();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
        });

        loginBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String userStr = username.getText();
                String passStr = password.getText();

                DBUtil.setDbUser(userStr);
                DBUtil.setDbPass(passStr);

                try {
                    DBUtil.connect();
                    DataController mController = new DataController();
                    mController.start(new Stage());
                    stage.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        mArgs = args;
        launch(args);
    }
}
