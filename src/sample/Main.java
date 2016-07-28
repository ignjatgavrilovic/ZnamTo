package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage window;

    public static Stage getWindow() { return window; }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Znam To");
        primaryStage.setScene(new Scene(new MainPane()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
