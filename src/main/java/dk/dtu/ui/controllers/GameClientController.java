package dk.dtu.ui.controllers;

import dk.dtu.chat.ChatClient;
import dk.dtu.game.GameClient;
import dk.dtu.game.GameCommands;
import dk.dtu.game.commands.ConnectionStatus;
import dk.dtu.game.commands.GamePhase;
import dk.dtu.game.commands.enums.ConnectionStatusType;
import dk.dtu.network.Peer;
import dk.dtu.ui.GameScreen;
import dk.dtu.ui.LobbyScreen;
import javafx.application.Platform;

public class GameClientController extends GameClient {
    private StartController startController;
    private GameScreen gameScreen;

    public GameClientController(Peer peer) {
        super(peer);
    }

    public void setStartController(StartController startController) {
        this.startController = startController;
    }

    public void setGameScreen(GameScreen gs) {
        this.gameScreen = gs;
        ((GameCommandsController)gameCommands).setGameScreen(gs);;
    }


    @Override
    public void initGameCommands() {
        gameCommands = new GameCommandsController(this);
    }
    
    @Override
    public void initGame() {
        super.initGame();
        startController.startGame(peer);
    }

    @Override
    public void printToScreen(String gamePhase) {
        Platform.runLater(() -> {
            gameScreen.makeGraphics();
        });
    }
}
