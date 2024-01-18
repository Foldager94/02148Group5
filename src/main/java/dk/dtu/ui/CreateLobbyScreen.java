package dk.dtu.ui;

import dk.dtu.network.MasterPeer;
import dk.dtu.network.Peer;
import dk.dtu.ui.components.PlayersListView;
import dk.dtu.ui.controllers.ChatController;
import dk.dtu.ui.controllers.MasterPeerController;
import dk.dtu.ui.controllers.PeerController;
import dk.dtu.ui.util.ScreenSize;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;

public class CreateLobbyScreen extends LobbyScreen {
    public CreateLobbyScreen(ScreenSize screenSize, MasterPeer peer, PlayersListView list) {
        super(screenSize, peer, list);
    }

    @Override
    public void initGraphics(Boolean host) {
        super.initGraphics(true);
        Button startButton = new Button("Start game");
        startButton.setLayoutY(150);
        startButton.getStyleClass().add("main-button");
        startButton.setOnAction(event -> {
            startGame();
		});
        getRoot().getChildren().add(startButton);
        startButton.setLayoutX(150 + 554);
    }

    public void startGame() {
        getChat(getPeer()).sendGlobalMessage("StartGame", getPeer().getPeerIds());
        getPeer().game.initGame();
    }

    @Override
	public ChatController getChat(Peer peer) {
		return (ChatController)(peer.chat);
	}

    

    private void kick() {

    }
    
}
