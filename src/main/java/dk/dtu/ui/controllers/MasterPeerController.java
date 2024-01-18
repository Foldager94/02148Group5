package dk.dtu.ui.controllers;

import dk.dtu.network.MasterPeer;
import dk.dtu.ui.GameScreen;
import dk.dtu.ui.components.PlayersListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
public class MasterPeerController extends MasterPeer  {
    private PlayersListView listView;
    private StartController startController;

    public MasterPeerController(String name, PlayersListView list, StartController startController) {
        super(name);
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
}
