package dk.dtu.ui.controllers;

import dk.dtu.game.GameClient;
import dk.dtu.game.GameCommands;
import dk.dtu.ui.GameScreen;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class GameCommandsController extends GameCommands {
    GameScreen gameScreen;
    public GameCommandsController(GameClient gameClient) {
        super(gameClient);
    }

    public void setGameScreen(GameScreen gs) {
        gameScreen = gs;
    }

    @Override
    public void showIsYourTurn() {
        Platform.runLater(() -> {
            gameScreen.setIsYourTurn();
        });
    }

    @Override
    public void printToScreen() {
        Platform.runLater(() -> {
            gameScreen.makeGraphics();
        });
    }
}
