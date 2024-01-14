package dk.dtu.ui.controllers;

import dk.dtu.chat.ChatClient;
import dk.dtu.network.Peer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PeerController extends Peer  {
    public ObservableList<String> peerNames = FXCollections.observableArrayList(name);

    public PeerController(String name, String port) {
        super(name, port);
    }

    @Override
    public void initChatClient() {
        chat = new ChatController(this);
    }

    @Override
    public void showRecievedIntroduction(String peerId, String peerName, String peerUri) {
        peerNames.add(peerName);
    }
}
