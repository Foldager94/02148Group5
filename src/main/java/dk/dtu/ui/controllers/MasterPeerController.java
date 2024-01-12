package dk.dtu.ui.controllers;

import dk.dtu.network.MasterPeer;

public class MasterPeerController extends MasterPeer  {
    public MasterPeerController(String name) {
        super(name);
    }

    @Override
    public void initChatClient() {
        chat = new ChatController(this);
    }
}
