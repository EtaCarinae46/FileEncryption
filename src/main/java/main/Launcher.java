package main;

import service.Log;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.window = primaryStage;
        Parent root = Main.mainLoader.load();
        Main.logger = new Log();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/main.css");
        Main.window.setTitle("Encription / Decription");
        Main.window.setScene(scene);
        Main.window.setWidth(640);
        Main.window.setHeight(520);
        Main.window.setResizable(false);
        Main.window.initStyle(StageStyle.UNDECORATED);
        Main.window.show();
    }
}
