package dk.dtu.network;
import org.jspace.*;

import java.util.HashMap;
import java.util.LinkedList;


public class MasterPeer extends Peer {
    final SequentialSpace requests = new SequentialSpace();
    final SequentialSpace readyFlags = new SequentialSpace();
    
    private int idTracker = 0;

    public MasterPeer(){
        super("");

    }


    public void awaitLobbyRequest(){
        new Thread(() -> {
            while (true) {
                try {
                    // await join request from requests space. Object[] = {"Helo", Name, IP:PORT}
                    Object[] info = this.requests.get(new ActualField("Helo"), new FormalField(String.class), new FormalField(String.class));
                    int peerId = generateNewPeerId();

                    // Peer class should take care of this?
                    // Add peer to MB's Peers space. {Id, Name, Ip:port}
                    this.peers.put(peerId,info[1],info[2]);

                    // Retrieve current peers connected
                    LinkedList<Object[]> peers = this.peers.queryAll(new FormalField(Integer.class), new FormalField(String.class), new FormalField(String.class));

                    this.requests.put("Helo", this.id, peerId, peers, info[2]);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }

    public void awaitReadyFlags(){
        readyFlags.getp(new ActualField("isReadyList"), new FormalField(HashMap.class));
        new Thread(() -> {
            try {
                readyFlags.put("isReadyList", new HashMap<String, Boolean>());
                while (true) {

                    Object[] readyInfo = readyFlags.get(new ActualField("isReady"), new FormalField(Integer.class), new FormalField(Boolean.class));

                    // Hashmap has been initialized above, reason why it is safe to suppress warning
                    @SuppressWarnings("unchecked")
                    HashMap<String, Boolean> isReadyMap = (HashMap<String, Boolean>) readyFlags.get(new ActualField("isReadyList"), new FormalField(HashMap.class))[1];
                    isReadyMap.put((String) readyInfo[1], (Boolean) readyInfo[2]);

                    if(isAllReady(isReadyMap)){
                        //TODO: Start Game
                        break;
                    }
                    readyFlags.put("isReady", isReadyMap);

                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    private boolean isAllReady(HashMap<String, Boolean> isReadyMap){
        for (Boolean value : isReadyMap.values()) {
            if (!value) {
                return false;
            }
        }
        return true;
    };
    // returns current unused id and then increments
    private int generateNewPeerId(){
        return idTracker++;
    };

    // Peer class should maybe add itself to its own Peer Space
    // Adds master peer to its own peers space inside Peer class
    public void addMasterToOwnPeers() {
        try {
            this.peers.put(generateNewPeerId(), name, ip+":"+port);
        } catch (InterruptedException e) {
            System.err.println("Can't add MP to peer space.");
        }
    }

    @Override
    private void initSpaces() {
        try {
            chat = new SequentialSpace();
            chatResp = new SpaceRepository();
            chatResp.add("chat", chat);
            chatResp.addGate(formatURI(ip, port) + "/chatResp?keep");
            peers = new SequentialSpace();
            
            chats = new SpaceRepository();
        } catch(Exception e) {}
    }


    /*
        TODO: Kick Player from lobby
        Function that kicks a chosen player from the lobby
    */
    public SequentialSpace getReadyFlags() {
        return readyFlags;
    }

    public SequentialSpace getRequests() {
        return requests;
    }
}
