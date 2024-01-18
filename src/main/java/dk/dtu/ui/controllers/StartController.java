package dk.dtu.ui.controllers;

import java.io.File;
import java.util.Random;

import dk.dtu.network.MasterPeer;
import dk.dtu.network.Peer;
import dk.dtu.ui.CreateLobbyScreen;
import dk.dtu.ui.GameScreen;
import dk.dtu.ui.LobbyScreen;
import dk.dtu.ui.components.PlayersListView;
import dk.dtu.ui.util.ScreenSize;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
public class StartController {
    ScreenSize screenSize;
    Label errorText;
    private int errors = 0;
    Stage primaryStage;
    GameScreen gameScreen;

    public StartController(Stage primaryStage, ScreenSize screenSize, Label errorText) {
        this.primaryStage = primaryStage;
        this.screenSize = screenSize;
        this.errorText = errorText;
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

    public void joinLobby(String username) {
        if (username.trim().isEmpty()) {
            showError("Username cannot be empty");
        } else if (username.equalsIgnoreCase("You")) {
            showError("Invalid username");
        } else {
            PlayersListView list = new PlayersListView(8, false);
            list.addName(username, null);
            PeerController p = new PeerController(username, list, String.valueOf((new Random().nextInt(10000 - 6000 + 1) + 6000)), this);
            p.connectToMP();
            p.sendIntroduction();
            p.getIntroduction();
            p.startMessageReceiver();
            gameScreen = new GameScreen(screenSize, p.game);
            p.setGameScreen(gameScreen);
            LobbyScreen lobbyScreen = new LobbyScreen(screenSize, p, list);
            Scene lobbyScene = new Scene(lobbyScreen.getView(), screenSize.getWidth(), screenSize.getHeight());
            addCss("src\\resources\\main.css", lobbyScene);
            addCss("src\\resources\\chat.css", lobbyScene);
            primaryStage.setScene(lobbyScene);
        }
    }

    public void createLobby(String username) {
        if (username.trim().isEmpty()) {
            showError("Username cannot be empty");
        } else {
            PlayersListView list = new PlayersListView(8, false);
            list.addName(username, null);
            MasterPeerController mp = new MasterPeerController(username, list, this);
            mp.awaitLobbyRequest();
            mp.awaitReadyFlags();
            mp.getIntroduction();
            mp.startMessageReceiver();
            gameScreen = new GameScreen(screenSize, mp.game);
            mp.setGameScreen(gameScreen);
            CreateLobbyScreen lobbySreen = new CreateLobbyScreen(screenSize, mp, list);
            Scene lobbyScene = new Scene(lobbySreen.getView(), screenSize.getWidth(), screenSize.getHeight());
            addCss("src\\resources\\main.css", lobbyScene);
            addCss("src\\resources\\chat.css", lobbyScene);
            primaryStage.setScene(lobbyScene);
        }
    }

    public void startGame(Peer peer) {
        Platform.runLater(() -> {
            Scene gameScene = new Scene(gameScreen.getView(), screenSize.getWidth(), screenSize.getHeight());
            addCss("src\\resources\\main.css", gameScene);
            addCss("src\\resources\\chat.css", gameScene);
            primaryStage.setScene(gameScene);
        });
    }

    public void addCss(String url, Scene scene) {
        File css = new File(url);
        scene.getStylesheets().add("file:///" + css.getAbsolutePath().replace("\\", "/"));
    }

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
}
