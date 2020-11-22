package main;

import service.Log;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class Main {

    public static final int defaultBuffer = 2097152;
    public static Stage window;
    public static FXMLLoader mainLoader = new FXMLLoader();
    public static Log logger;

    public static void main(String[] args) {
        mainLoader.setLocation(Main.class.getResource("/fxml/main.fxml"));
        Application.launch(Launcher.class, args);
    }
}
