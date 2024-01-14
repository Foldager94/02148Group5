package dk.dtu.ui.controllers;

import dk.dtu.network.MasterPeer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MasterPeerController extends MasterPeer  {
    public ObservableList<String> peerNames = FXCollections.observableArrayList(name);

    public MasterPeerController(String name) {
        super(name);
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
