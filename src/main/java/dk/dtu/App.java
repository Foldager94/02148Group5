package dk.dtu;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import dk.dtu.ui.CreateLobbyScreen;
import dk.dtu.ui.JoinLobbyScreen;
import dk.dtu.ui.StartScreen;
import dk.dtu.ui.components.LargeButton;
import dk.dtu.ui.util.ScreenController;
import dk.dtu.ui.util.ScreenSize;
import javafx.application.Application;
import javafx.event.*;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) throws InterruptedException {
        
        launch(args);
    }
	private Stage primaryStage;
    private ScreenSize screenSize = new ScreenSize(800, 600);

    private Button joinButton;
    private Scene scene;

    private static Pane root = new Pane();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // createButton.setOnAction(this::handleClick);
        joinButton = new LargeButton("Join lobby").getButton();
        joinButton.setLayoutY(20);
        screenSize.getHeight();
        
        root.getChildren().add(joinButton);
        root.getStyleClass().add("pane");

        scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());

        File css = new File("src\\resources\\main.css");
        scene.getStylesheets().add("file:///" + css.getAbsolutePath().replace("\\", "/"));

        primaryStage.setScene(scene);
        primaryStage.show();
        joinButton.setLayoutX(screenSize.getWidth() / 2 - joinButton.getLayoutBounds().getWidth() / 2);
    }
}