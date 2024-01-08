package dk.dtu;

import java.util.concurrent.TimeUnit;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import dk.dtu.ui.CreateLobbyScreen;
import dk.dtu.ui.JoinLobbyScreen;
import dk.dtu.ui.StartScreen;
import dk.dtu.ui.util.ScreenController;
import dk.dtu.ui.util.ScreenSize;
import javafx.application.Application;
import javafx.event.*;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) throws InterruptedException {
        
        launch(args);
    }
	private Stage primaryStage;
    private ScreenSize screenSize;

    private Button createButton = new Button();
    private Button joinButton = new Button();

    private Scene mainScene;
    private ScreenController screenController;
    private Scene startScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initGraphics();
    }

    private void initGraphics() {
        screenSize = new ScreenSize(800, 600);
        StartScreen startScreen = new StartScreen(screenSize);

        screenController = new ScreenController(screenSize, startScreen);

        primaryStage.setResizable(false);
        primaryStage.setTitle("Poker Game");
        
        showStartScreen();

    }

    private void showStartScreen() {
        startScene = new Scene(screenController.getStartSceeen().getGroup(), screenSize.getWidth(),
        screenSize.getHeight());
        primaryStage.setScene(startScene);
        primaryStage.show();
    }
}