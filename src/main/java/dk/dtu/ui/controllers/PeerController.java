package dk.dtu.ui.controllers;

import dk.dtu.chat.ChatClient;
import dk.dtu.network.Peer;

public class PeerController extends Peer  {
    public PeerController(String name, String port) {
        super(name, port);
    }

    @Override
    public void initChatClient() {
        System.out.println("Hej");
        chat = new ChatController(this);
    }
}
