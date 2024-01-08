package dk.dtu.network;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

public class MasterPeer extends Peer {
    final SequentialSpace requests = new SequentialSpace();
    final SequentialSpace readyFlag = new SequentialSpace();
    String lobbyPort = "9001";

    // Redundant from peer class. Delete when merged
    final SequentialSpace peers = new SequentialSpace();
    final String localIp = "127.0.0.1";
    // Redundant end
    public MasterPeer(){

    }


    public void awaitLobbyJoinRequest(){
        new Thread(() -> {
            while (true) {
                try {
                    // {"Helo", Name, IP:PORT}
                    Object[] info = this.requests.get(new ActualField("HELO"), new FormalField(String.class), new FormalField(String.class));
                    this.peers.put();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }

    public void addMasterPeerToLobby() {
        // TODO: Add Master Peer to lobby
    }



    /*
        TODO: Kick Player from lobby
        Function that kicks a chosen player from the lobby
    */
    public void setLobbyPort(String port){
        this.lobbyPort = port;
    }

    public String getLobbyPort() {
        return lobbyPort;
    }

    public SequentialSpace getReadyFlags() {
        return readyFlag;
    }

    public SequentialSpace getRequests() {
        return requests;
    }
}
