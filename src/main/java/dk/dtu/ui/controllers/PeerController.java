package dk.dtu.ui.controllers;

import dk.dtu.chat.ChatClient;
import dk.dtu.network.Peer;
import dk.dtu.ui.components.PlayersListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PeerController extends Peer  {
    private PlayersListView listView;

    public PeerController(String name, PlayersListView list, String port) {
        super(name, port);
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

    @Override
    public void showTryingtoConnectToPear(String peerId, String peerName, String peerUri) {
        System.out.println("Got introduction from: " + peerName);
        Platform.runLater(() -> {
            listView.addName(peerName);
        });
    }
}
