package dk.dtu.ui;

import dk.dtu.network.MasterPeer;
import dk.dtu.network.Peer;
import dk.dtu.ui.components.PlayersListView;
import dk.dtu.ui.controllers.ChatController;
import dk.dtu.ui.controllers.MasterPeerController;
import dk.dtu.ui.controllers.PeerController;
import dk.dtu.ui.util.ScreenSize;
import javafx.collections.ObservableList;

public class CreateLobbyScreen extends LobbyScreen {
    public CreateLobbyScreen(ScreenSize screenSize, MasterPeer peer, PlayersListView list) {
        super(screenSize, peer, list);
    }

    @Override
    public void initGraphics(Boolean host) {
        super.initGraphics(true);
    }

    @Override
	public ChatController getChat(Peer peer) {
		return (ChatController)(peer.chat);
	}

    private void kick() {

    }
    
}
