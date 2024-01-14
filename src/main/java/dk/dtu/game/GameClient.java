package dk.dtu.game;
import dk.dtu.game.round.RoundState;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;
import org.jspace.Tuple;
import java.io.IOException;
import java.util.List;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import dk.dtu.game.commands.ConnectionStatus;
import dk.dtu.game.commands.GamePhase;
import dk.dtu.game.commands.RoundStatus;
import dk.dtu.game.commands.enums.ConnectionStatusType;
import dk.dtu.game.commands.enums.GamePhaseType;
import dk.dtu.game.commands.enums.RoundStatusType;
import static dk.dtu.game.GameSettings.*;

import dk.dtu.network.Peer;

public class GameClient {
    public SequentialSpace gameSpace;
    public SpaceRepository gameSpaces;
    public Peer peer;
    public GameState gameState;
    public final GameCommands gameCommands = new GameCommands(this);

    public GameClient(Peer peer) {
        gameSpace = new SequentialSpace();
        gameSpaces = new SpaceRepository();
        gameState = new GameState();
        this.peer = peer;
    }
    public void startGameCommandReceiver() {
        new Thread(() -> {
            while(true){
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
            }
        }).start();
    }

    public void initGame() {
        addPlayerToGameState(peer.id);
        startGameCommandReceiver();
        connectToPeersGameSpace();
        ConnectionStatus command = new ConnectionStatus(peer.id, ConnectionStatusType.Ping);
        sendGlobalCommand(peer.getPeerIds(), "ConnectionStatus", command.toJson());
    }

    public void sendCommand(String ReceiverID, String command, String jsonObject) {
        try {
            getPeerGameSpace(ReceiverID).put(command, jsonObject);
        }
        catch(Exception e) {System.out.println(e.getMessage());}
    }

    public void sendGlobalCommand(List<String> ids, String command, String jsonObject) {
        for (String ReceiverID : ids) {
            if(ReceiverID.equals(peer.id)){
                continue;
            }
            sendCommand(ReceiverID, command, jsonObject);

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
    public boolean connectionEstablishedToAll() {
        return peer.chat.chats.size() == gameState.players.size();
    }


    public Space getPeerGameSpace(String peerId){
        return gameSpaces.get(peerId);
    }
    
    // TODO: Change player balance at some point
    public void addPlayerToGameState(String peerId){
        Player newPlayer = new Player(peerId, START_BALANCE);
        gameState.addPlayer(newPlayer);
    }

    public void startNewRound(){
        RoundStatus roundStatus = new RoundStatus(peer.id, RoundStatusType.NewRoundStarted);
        sendGlobalCommand(peer.getPeerIds(), "RoundStatus", roundStatus.toJson());
        gameState.createNewRoundState(peer.id);
        initPreFlop();
    }

    public void initPreFlop(){
        gameState.resetDeck();
        List<String> PeerIds = peer.getPeerIds();
        List<Card> holeCards;
        for(String id : PeerIds) { // the dealer deal 2 cards to each player
            holeCards = gameState.deck.draw(2);
            GamePhase gpCommand = new GamePhase(peer.id, GamePhaseType.PreFlop, holeCards);
            sendCommand(id, "GamePhase", gpCommand.toJson());
        }
        holeCards = gameState.deck.draw(2);
        gameState.currentRoundState.getOwnPlayerObject().setHoleCards(holeCards);
        gameState.currentRoundState.calculateBlindsBet();
        System.out.println(gameState.currentRoundState.toString());
    }

    public RoundState getCurrentRoundState(){
        return gameState.currentRoundState;
    }

}