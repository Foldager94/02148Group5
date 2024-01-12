package dk.dtu.network;
import org.jspace.*;

import java.util.HashMap;
import java.util.LinkedList;


public class MasterPeer extends Peer {
    public MasterPeer(String name) {
		super(name, "9004");
        this.id = generateNewPeerId();
        this.MPID = this.id;
        addMasterToOwnPeers();
		//TODO Auto-generated constructor stub
        // initSpaces();
	}

    public final int MAX_LOBBY_SIZE = 8;


	SequentialSpace MPrequests;
    SequentialSpace MPreadyFlags;
    
    public int idTracker = 0;


    // Get a join request
    // Retrieve all the piers that are connected
    // MP includes the peer in the lobby
    public void awaitLobbyRequest(){
        new Thread(() -> {
            while (true) {
                try {
                    // await join request from requests space. Object[] = {"Helo", Name, IP:PORT}
                    Object[] info = MPrequests.get(new ActualField("Helo"), new FormalField(String.class), new FormalField(String.class));
                    String peerId = generateNewPeerId();

                    // Peer class should take care of this?
                    // Add peer to MB's Peers space. {Id, Name, Ip:port}
                    //peers.put(peerId,info[1],info[2]);

                    // Retrieve current peers connected
                    LinkedList<Object[]> peers = this.peers.queryAll(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class), new FormalField(Boolean.class));
                    if (isLobbyFull()) {
                        MPrequests.put("Lobby is full", info[2]);
                        System.out.println("Lobby is full");
                        continue;
                    }
                    MPrequests.put("Approved", info[2]);

                    MPrequests.put("Helo", this.id, peerId, peers, info[2]);
                
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    
    public int LobbySize() {
        return peers.size();
    }
    
    public boolean isLobbyFull() {        
        return LobbySize() >= MAX_LOBBY_SIZE;
    }

    public void awaitReadyFlags(){
        MPreadyFlags.getp(new ActualField("isReadyList"), new FormalField(HashMap.class));
        new Thread(() -> {
            try {
                MPreadyFlags.put("isReadyList", new HashMap<String, Boolean>());
                while (true) {

                    Object[] readyInfo = MPreadyFlags.get(new ActualField("isReady"), new FormalField(String.class), new FormalField(Boolean.class));

                    // Hashmap has been initialized above, reason why it is safe to suppress warning
                    @SuppressWarnings("unchecked")
                    HashMap<String, Boolean> isReadyMap = (HashMap<String, Boolean>) MPreadyFlags.get(new ActualField("isReadyList"), new FormalField(HashMap.class))[1];
                    isReadyMap.put((String) readyInfo[1], (Boolean) readyInfo[2]);

                    if(isAllReady(isReadyMap)){
                        //TODO: Start Game
                        break;
                    }
                    MPreadyFlags.put("isReady", isReadyMap);

                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public boolean isAllReady(HashMap<String, Boolean> isReadyMap){
        for (Boolean value : isReadyMap.values()) {
            if (!value) {
                return false;
            }
        }
        return true;
    };
    
    // returns current unused id and then increments
    public String generateNewPeerId(){
        String idString = String.valueOf(idTracker);
        idTracker++;
        return idString;
    };

    // Peer class should maybe add itself to its own Peer Space
    // Adds master peer to its own peers space inside Peer class
    public void addMasterToOwnPeers() {
        try {
            this.peers.put(this.id, name, formatURI(ip, port), false);
        } catch (InterruptedException e) {
            System.err.println("Can't add MP to peer space.");
        }
    }

    @Override
    public void initSpaces() {
        try {
            remoteResp = new SpaceRepository();
            remoteResp.add("chat", chat.getChat());
            peers = new SequentialSpace();
            remoteResp.add("gameSpace", game.getGameSpace());
            MPrequests = new SequentialSpace();
            MPreadyFlags = new SequentialSpace();
            remoteResp.add("requests", MPrequests);
            remoteResp.add("ready", MPreadyFlags);
            remoteResp.addGate(formatURI(ip, port) + "/?keep");
        } catch(Exception e) {
            System.out.println("MP initSpaces: " + e.getMessage());
        }
    }

    /*
        TODO: Kick Player from lobby
        Function that kicks a chosen player from the lobby
    */
    public SequentialSpace getReadyFlags() {
        return MPreadyFlags;
    }

    public SequentialSpace getRequests() {
        return MPrequests;
    }
}
