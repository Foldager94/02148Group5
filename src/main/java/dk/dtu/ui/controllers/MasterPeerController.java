package dk.dtu.ui.controllers;

import dk.dtu.network.MasterPeer;
import dk.dtu.ui.components.PlayersListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;

public class MasterPeerController extends MasterPeer  {
    private PlayersListView listView;

    public MasterPeerController(String name, PlayersListView list) {
        super(name);
        this.listView = list;
    }


    @Override
    public void initChatClient() {
        chat = new ChatController(this);
    }

    @Override
    public void showRecievedIntroduction(String peerId, String peerName, String peerUri) {
        System.out.println("Got introduction from: " + peerName);
        Platform.runLater(() -> {
            listView.addName(peerName);
        });
    }
}
