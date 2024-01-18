package dk.dtu.ui.controllers;

import dk.dtu.chat.ChatClient;
import dk.dtu.game.GameClient;
import dk.dtu.network.Peer;
import dk.dtu.ui.GameScreen;
import dk.dtu.ui.LobbyScreen;
import dk.dtu.ui.components.PlayersListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;


public class PeerController extends Peer  {
    private PlayersListView listView;
    private StartController startController;

    public PeerController(String name, PlayersListView list, String port, StartController startController) {
        super(name, port);
        this.startController = startController;
        ((GameClientController)game).setStartController(startController);
        this.listView = list;
    }

    public void setGameScreen(GameScreen gs) {
        ((GameClientController)game).setGameScreen(gs);
    }

    @Override
    public void initChatClient() {
        chat = new ChatController(this);
    }

    @Override
    public void initGameClient() {
        game = new GameClientController(this);
    }

    @Override
    public void showRecievedIntroduction(String peerId, String peerName, String peerUri) {
        System.out.println("Got introduction from: " + peerName);
        Platform.runLater(() -> {
            listView.addName(peerName, peerId);
        });
    }

    @Override
    public void showTryingtoConnectToPear(String peerId, String peerName, String peerUri) {
        System.out.println("Got introduction from: " + peerName);
        Platform.runLater(() -> {
            listView.addName(peerName, peerId);
        });
    }
}
