package dk.dtu.ui;

import dk.dtu.network.MasterPeer;
import dk.dtu.network.Peer;
import dk.dtu.ui.util.ScreenSize;

public class CreateLobbyScreen extends LobbyScreen {
    public CreateLobbyScreen(ScreenSize screenSize, MasterPeer peer) {
        super(screenSize, peer);
    }

    @Override
    public void initGraphics(Boolean host) {
        super.initGraphics(true);

    }

    private void kick() {

    }
    
}
