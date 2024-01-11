package dk.dtu.game;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;
import org.jspace.Tuple;
import java.io.IOException;
import java.util.List;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import dk.dtu.game.round.RoundState;
import dk.dtu.network.Peer;
public class GameClient {
    public SequentialSpace gameSpace;
    public SpaceRepository gameSpaces;
    public Peer peer;
    public GameState gameState;
    public GameCommands gameCommands; 

    public GameClient(Peer peer) {
        gameSpace = new SequentialSpace();
        gameSpaces = new SpaceRepository();
        gameState = new GameState();
        this.peer = peer;
    }
    
    public void startGameCommandReciver() {
        new Thread(() -> {
            try {
                Tuple messageTuple = new Tuple(gameSpace.get(
                    new FormalField(String.class), // command
                    new FormalField(String.class)  // json
                ));
                gameCommands.commandHandler(messageTuple, gameState.currentRoundState);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
                return;
            }
        }).start();
    }


    public void sendCommand(String recieverID, String command, String jsonObject) {
        try {
            getPeerGameSpace(recieverID).put(command, jsonObject);
        }
        catch(Exception e) {System.out.println(e.getMessage());}
    }

    public void sendGlobalCommand(List<String> ids, String command, String jsonObject) {
        for (String recieverID : ids) {
            if(recieverID.equals(peer.id)){
                continue;
            }
            sendCommand(recieverID, command, jsonObject);
        }
    }

    public SequentialSpace getGameSpace(){
        return gameSpace;
    }
    public void connectToPeersGameSpace(){
        List<Object[]> peersList = peer.getListOfPeers();
        for(Object[] peerInfo : peersList){
            String peerId   = (String)peerInfo[0];
            String peerUri  = (String)peerInfo[2];
            addGameSpaceToRepo(peerId, peerUri);
        }
    }

    public void addGameSpaceToRepo(String peerId, String peerUri){
        try {
            gameSpaces.add(peerId, new RemoteSpace(peerUri + "/gameSpace?keep"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Space getPeerGameSpace(String peerId){
        return gameSpaces.get(peerId);
    }

    // public void commandHandler(Tuple messageTuple){
    //     String command = messageTuple.getElementAt(String.class, 0);
    //     switch (command) {
    //         // Dealer Commands
    //         case "PreFlop":
    //             System.err.println("NotImplementedException: PreFlop");
    //             break;
    //         case "Flop":
    //             System.err.println("NotImplementedException: Flop");
    //             break;
    //         case "Turn":
    //             System.err.println("NotImplementedException: Turn");
    //             break;
    //         case "River":
    //             System.err.println("NotImplementedException: River");
    //             break;
    //         case "Showdown":
    //             System.err.println("NotImplementedException: Showdown");
    //             break;
    //         case "BettingRound":
    //             System.err.println("NotImplementedException: BettingRound");
    //             break;
    //         case "SendCards":
    //             System.err.println("NotImplementedException: SendCards");
    //             break;
    //         case "NewRoundStarted":
    //             System.err.println("NotImplementedException: NewRoundStarted");
    //             break;
    //         case "RoundEnded":
    //             System.err.println("NotImplementedException: RoundEnded");
    //             break;
    //         // Player commands
    //         case "Ping":
    //             System.err.println("NotImplementedException: Ping");
    //             break;
    //         case "Pong":
    //             System.err.println("NotImplementedException: Pong");
    //             break;
    //         case "RequestCards":
    //             System.err.println("NotImplementedException: RequestCards");
    //             break;
    //         case "MessageRecived":
    //             System.err.println("NotImplementedException: MessageRecived");
    //             break;
    //         case "Fold":
    //             System.err.println("NotImplementedException: Fold");
    //             break;
    //         case "Bet":
    //             System.err.println("NotImplementedException: Bet");
    //             break;
    //         case "Raise":
    //             System.err.println("NotImplementedException: Raise");
    //             break;
    //         case "Check":
    //             System.err.println("NotImplementedException: Check");
    //             break;
    //         case "Call":
    //             System.err.println("NotImplementedException: Call");
    //             break;
    //         case "Broke":
    //             System.err.println("NotImplementedException: Broke");
    //             break;
    //         case "RoundStateUpdated":
    //             System.err.println("NotImplementedException: RoundStateUpdated");
    //             break;
    //         case "RoundStateSync":
    //             System.err.println("NotImplementedException: RoundStateSync");
    //             break;
    //         case "RoundStateSyncApproved":
    //             System.err.println("NotImplementedException: RoundStateSyncApproved");
    //             break;
    //         case "RoundStateSyncDisapproved":
    //             System.err.println("NotImplementedException: RoundStateSyncDisapproved");
    //             break;
    //         default:
    //             System.err.println("GameClient: Command unknown");
    //             break;
    //     }
    // }
}