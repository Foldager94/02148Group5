package dk.dtu.ui.controllers;

import dk.dtu.chat.ChatClient;
import dk.dtu.game.GameClient;
import dk.dtu.game.GameCommands;
import dk.dtu.game.commands.ConnectionStatus;
import dk.dtu.game.commands.enums.ConnectionStatusType;
import dk.dtu.network.Peer;

public class GameClientController extends GameClient {
    ChatClient chatClient;
    public GameClientController(Peer peer, ChatClient chatClient) {
        super(peer);
        this.chatClient = chatClient;
    }
    @Override
    public void initGameCommands() {
        gameCommands = new GameCommandsController(this);
    }

    @Override
    public void initGame() {
        super.initGame();
        // launch GameScreen 
    }
}
