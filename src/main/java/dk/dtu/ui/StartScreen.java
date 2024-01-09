package dk.dtu.ui;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import dk.dtu.ui.LobbyScreen;
import dk.dtu.ui.LobbyScreen;
import dk.dtu.ui.StartScreen;
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

public class StartScreen extends Application {
    private ScreenSize screenSize = new ScreenSize(1200, 800);

	private Stage primaryStage;
    private Scene scene;
    private static Pane root = new Pane();
    private Label errorText;
    private TextField username;
    private int errors = 0;
    private LobbyScreen lobbyScreen;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;


        Button joinButton = new Button("Join lobby");
        Button createButton = new Button("Create lobby");
        joinButton.setLayoutY(screenSize.getHeight() / 2 + 50);
        createButton.setLayoutY(screenSize.getHeight() / 2 + 50);
		joinButton.setOnAction(event -> {
		    joinLobby();
		});
        createButton.setOnAction(event -> {
		    createLobby();
		});


        username = new TextField();
        username.setLayoutY(screenSize.getHeight() / 2 - 80);
        username.setPromptText("Username");

        Label header = new Label("Welcome to our Poker Game");
        header.getStyleClass().add("header");
        header.setLayoutY(screenSize.getHeight() / 2 - 140);
        errorText = new Label("");
        errorText.setLayoutY(screenSize.getHeight() / 2 + 100);
        errorText.getStyleClass().add("error");

        root.getChildren().add(header);
        root.getChildren().add(joinButton);
        root.getChildren().add(createButton);
        root.getChildren().add(username);
        root.getChildren().add(errorText);
        root.getStyleClass().add("pane");

        scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
        addCss("src\\resources\\main.css", scene);
        File icon = new File("src\\resources\\images\\king.png");

        primaryStage.getIcons().add(new Image(icon.getAbsolutePath().replace("\\", "/")));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        joinButton.setLayoutX(screenSize.getWidth() / 2 - joinButton.getLayoutBounds().getWidth() / 2 + 110);
        createButton.setLayoutX(screenSize.getWidth() / 2 - joinButton.getLayoutBounds().getWidth() / 2 - 110);
        header.setLayoutX(screenSize.getWidth() / 2 - header.getLayoutBounds().getWidth() / 2);
        username.setLayoutX(screenSize.getWidth() / 2 - username.getLayoutBounds().getWidth() / 2);
    }

    public void joinLobby() {
        if (username.getText().trim().isEmpty()) {
            showError("Username cannot be empty");
        } else {
            lobbyScreen = new LobbyScreen(screenSize, false);
            Scene lobbyScene = new Scene(lobbyScreen.getView(), screenSize.getWidth(), screenSize.getHeight());
            addCss("src\\resources\\main.css", lobbyScene);
            primaryStage.setScene(lobbyScene);
        }
    }

    private void createLobby() {
        if (username.getText().trim().isEmpty()) {
            showError("Username cannot be empty");
        } else {
            lobbyScreen = new LobbyScreen(screenSize, true);
            Scene lobbyScene = new Scene(lobbyScreen.getView(), screenSize.getWidth(), screenSize.getHeight());
            addCss("src\\resources\\main.css", lobbyScene);
            primaryStage.setScene(lobbyScene);
        }
    }

    public void showError(String errorMsg) {
        errorText.setText(errorMsg);
        double centerX = screenSize.getWidth() / 2 - errorText.getBoundsInLocal().getWidth() / 2;
        errorText.setLayoutX(centerX);
        errors++;
        delay(3000, () -> {
            if (errors-- == 1) errorText.setText("");
        });
    }

    private void addCss(String url, Scene scene) {
        File css = new File("src\\resources\\main.css");
        scene.getStylesheets().add("file:///" + css.getAbsolutePath().replace("\\", "/"));
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