package dk.dtu.ui;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import dk.dtu.ui.LobbyScreen;
import dk.dtu.ui.LobbyScreen;
import dk.dtu.ui.StartScreen;
import dk.dtu.ui.controllers.StartController;
import dk.dtu.ui.util.ScreenController;
import dk.dtu.ui.util.ScreenSize;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StartScreen extends Application {
    private ScreenSize screenSize = new ScreenSize(960, 680);

	private Stage primaryStage;
    private Scene scene;
    private static Pane root = new Pane();
    private Label errorText;
    private TextField username;
    private int errors = 0;
    private LobbyScreen lobbyScreen;
    private Scene lobbyScene;
    private StartController startController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        errorText = new Label("");
        errorText.setLayoutY(screenSize.getHeight() / 2 + 100);
        errorText.getStyleClass().add("error");

        startController = new StartController(primaryStage, screenSize, errorText);

        Button joinButton = new Button("Join lobby");
        Button createButton = new Button("Create lobby");
        joinButton.setLayoutY(screenSize.getHeight() / 2 + 50);
        createButton.setLayoutY(screenSize.getHeight() / 2 + 50);
        joinButton.getStyleClass().add("main-button");
        createButton.getStyleClass().add("main-button");

		joinButton.setOnAction(event -> {
		    startController.joinLobby(username.getText());;
		});

        createButton.setOnAction(event -> {
		    startController.createLobby(username.getText());;
		});

        username = new TextField();
        username.setLayoutY(screenSize.getHeight() / 2 - 80);
        username.setPromptText("Username");
        username.getStyleClass().add("username-field");

        Label header = new Label("Welcome to our Poker Game");
        header.getStyleClass().add("header");
        header.setLayoutY(screenSize.getHeight() / 2 - 140);

        root.getChildren().add(header);
        root.getChildren().add(joinButton);
        root.getChildren().add(createButton);
        root.getChildren().add(username);
        root.getChildren().add(errorText);
        root.getStyleClass().add("pane");

        scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
        startController.addCss("src\\resources\\main.css", scene);
        File icon = new File("src\\resources\\images\\king.png");
        primaryStage.setTitle("Poker Group 5");
        primaryStage.getIcons().add(new Image(icon.getAbsolutePath().replace("\\", "/")));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        joinButton.setLayoutX(screenSize.getWidth() / 2 - joinButton.getLayoutBounds().getWidth() / 2 + 110);
        createButton.setLayoutX(screenSize.getWidth() / 2 - joinButton.getLayoutBounds().getWidth() / 2 - 110);
        header.setLayoutX(screenSize.getWidth() / 2 - header.getLayoutBounds().getWidth() / 2);
        username.setLayoutX(screenSize.getWidth() / 2 - username.getLayoutBounds().getWidth() / 2);
    }


    // function from https://stackoverflow.com/questions/26454149/make-javafx-wait-and-continue-with-code
    public static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try { Thread.sleep(millis); }
                catch (InterruptedException e) { }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }

    public static void main(String[] args) throws InterruptedException {    
        launch(args);
    }
}