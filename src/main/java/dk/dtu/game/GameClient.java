package dk.dtu.game;

import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;
import org.jspace.Tuple;
import org.jspace.FormalField;
import org.jspace.QueueSpace;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static dk.dtu.game.GameSettings.*;
import dk.dtu.game.round.RoundState;
import dk.dtu.game.commands.enums.*;
import dk.dtu.game.commands.*;
import dk.dtu.network.Peer;


public class GameClient {
    private final String ACTION = "Action";
    private final String CONNECTION_STATUS = "ConnectionStatus";
    private final String GAME_PHASE = "GamePhase";
    private final String ROUND_STATUS = "RoundStatus";
    public QueueSpace gameSpace;
    public SpaceRepository gameSpaces;
    public Peer peer;
    public GameState gameState;
    public GameCommands gameCommands;
    
    public GameClient(Peer peer) {
        initGameCommands();
        gameSpace = new QueueSpace();
        gameSpaces = new SpaceRepository();
        gameState = new GameState();
        this.peer = peer;
    }

    public void initGameCommands() {
        gameCommands = new GameCommands(this);
    }

    public void startGameCommandReceiver() {
        new Thread(() -> {
            while(true){
                try {
                    Tuple messageTuple = new Tuple(gameSpace.get(
                        new FormalField(String.class), // command
                        new FormalField(String.class)  // json
                    ));
                    gameCommands.commandHandler(messageTuple);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }).start();
    }
    public void initGame() {
        addPlayerToGameState(peer.id, peer.name);
        startGameCommandReceiver();
        connectToPeersGameSpace();
        sendInitialConnectionStatus();
    }

    public void sendInitialConnectionStatus(){
        ConnectionStatus command = new ConnectionStatus(peer.id, ConnectionStatusType.Ping, peer.name);
        sendGlobalCommand(peer.getPeerIds(), CONNECTION_STATUS, command.toJson());
    }
    
    public void sendCommand(String ReceiverID, String command, String jsonObject) {
        try {
            getPeerGameSpace(ReceiverID).put(command, jsonObject);
        }
        catch(Exception e) {System.out.println(e.getMessage());}
    }

    // sends a command to all its peers, excluding itself
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
            System.err.println("Error: addGameSpaceToRepo");
            e.printStackTrace();
        }
    }
    public boolean connectionEstablishedToAll() {
        return gameSpaces.size() == gameState.players.size();
    }


    public Space getPeerGameSpace(String peerId){
        return gameSpaces.get(peerId);
    }
    public void addPlayerToGameState(String peerId, String peerName){
        Player newPlayer = new Player(peerId, START_BALANCE, peerName);
        gameState.addPlayer(newPlayer);
    }

    public void startNewRound(){
        RoundStatus roundStatus = new RoundStatus(peer.id, RoundStatusType.NewRoundStarted);
        sendGlobalCommand(peer.getPeerIds(), ROUND_STATUS, roundStatus.toJson());
        gameState.createNewRoundState(peer.id);
        startGamePhases();
    }

    public void startGamePhases(){
        initNextPhase();
    }

    public void initNextPhase(){
        try {
            GamePhaseType gpt = changePhase();
            switch (gpt) {
                case PreFlop:
                    initPreFlop();
                    break;
                case Flop:
                    initFlop();
                    break;
                case Turn:
                    initTurn();
                    break;
                case River:
                    initRiver();
                    break;
                case Showdown: // start new round
                    initShowdown();
                    break;
            }
        } catch (NullPointerException e) {
            System.err.println("InitNextPhase: gpt is null");
            e.printStackTrace();
        }
        
    }
    public GamePhaseType changePhase(){
        GamePhaseType gpt = getCurrentRoundState().getGamePhaseType();
        if(gpt==null){
            getCurrentRoundState().setGamePhaseType(GamePhaseType.PreFlop);
            return GamePhaseType.PreFlop;
        }
        switch(gpt){
            case PreFlop:
                getCurrentRoundState().setGamePhaseType(GamePhaseType.Flop);
                return GamePhaseType.Flop;
            case Flop:
                getCurrentRoundState().setGamePhaseType(GamePhaseType.Turn);
                return GamePhaseType.Turn;
            case Turn:
                getCurrentRoundState().setGamePhaseType(GamePhaseType.River);
                return GamePhaseType.River;
            case River:
                getCurrentRoundState().setGamePhaseType(GamePhaseType.Showdown);
                return GamePhaseType.Showdown;
        }
        return null;
    }

    // send a turn command to the next player
    public void sendPlayerTurnCommand(String previusePeerId) {
        try {
            String firstPlayerId = getCurrentRoundState().getNextNonFoldedPlayer(previusePeerId);
            System.out.println("Next player:" + firstPlayerId);
            RoundStatus rsCommand = new RoundStatus(peer.id, RoundStatusType.PlayerTurn);
            if (firstPlayerId.equals(peer.id)) {
                gameSpace.put(ROUND_STATUS, rsCommand.toJson());
            } else {
                sendCommand(firstPlayerId, ROUND_STATUS, rsCommand.toJson());
            }
        } catch (InterruptedException e){
            System.err.println("Error: sendPlayerTurnCommand");
            e.printStackTrace();
        }
    }


    public void initPreFlop() {
        gameState.resetDeck();
        List<String> PeerIds = peer.getPeerIds();
        List<Card> holeCards;
        for(String id : PeerIds) { // the dealer deals 2 cards to each player
            holeCards = gameState.deck.draw(2);
            GamePhase gpCommand = new GamePhase(peer.id, GamePhaseType.PreFlop, holeCards);
            sendCommand(id, GAME_PHASE, gpCommand.toJson());
        }
        String bigBlindId = getCurrentRoundState().getBigBlind();
        sendPlayerTurnCommand(bigBlindId);
    }

    public void initFlop(){
        List<Card> communityCards = gameState.deck.draw(3);
        List<String> PeerIds = peer.getPeerIds();
        for(String id : PeerIds) { // send to all peers, including itself
            GamePhase gpCommand = new GamePhase(peer.id, GamePhaseType.Flop, communityCards);
            sendCommand(id, GAME_PHASE, gpCommand.toJson());
        }
        String bigBlindId = getCurrentRoundState().getBigBlind();
        sendPlayerTurnCommand(bigBlindId);
    }

    public void initTurn(){
        List<Card> communityCards = gameState.deck.draw(1);
        List<String> PeerIds = peer.getPeerIds();
        for(String id : PeerIds) { // send to all peers, including itself
            GamePhase gpCommand = new GamePhase(peer.id, GamePhaseType.Turn, communityCards);
            sendCommand(id, GAME_PHASE, gpCommand.toJson());
        }
        String bigBlindId = getCurrentRoundState().getBigBlind();
        sendPlayerTurnCommand(bigBlindId);
    }

    public void initRiver(){
        List<Card> communityCards = gameState.deck.draw(1);
        List<String> PeerIds = peer.getPeerIds();
        for(String id : PeerIds) { // send to all peers, including itself
            GamePhase gpCommand = new GamePhase(peer.id, GamePhaseType.River, communityCards);
            sendCommand(id, GAME_PHASE, gpCommand.toJson());
        }
        String bigBlindId = getCurrentRoundState().getBigBlind();
        sendPlayerTurnCommand(bigBlindId);
    }

    public void initShowdown(){
        Hand hand = gameCommands.getPlayerHand();
        getCurrentRoundState().incrementHandComparingCount();
        getCurrentRoundState().setWinningHand(hand);
        getCurrentRoundState().addWinningId(peer.id);
        getCurrentRoundState().addToTotalHoleCards(peer.id, getCurrentRoundState().getOwnPlayerObject().getHoleCards());
        List<String> PeerIds = peer.getPeerIds();
        for(String id : PeerIds) { // send to all peers, including itself
            GamePhase gpCommand = new GamePhase(peer.id, GamePhaseType.Showdown, new ArrayList<Card>());
            sendCommand(id, GAME_PHASE, gpCommand.toJson());
        }
    }

    public RoundState getCurrentRoundState(){
        return gameState.currentRoundState;
    }

    public String makeFoldAction(){
        Action foldAction = new Action(peer.id, ActionType.Fold, 0);
        getCurrentRoundState().setPlayerFolded(peer.id);
        sendGlobalCommand(peer.getPeerIds(), ACTION, foldAction.toJson());
        printToScreen(getCurrentRoundState().getGamePhaseType().toString());
        getCurrentRoundState().setIsMyTurn(false);
        
        String winningId = isOnlyOnePlayer();
        if(winningId != null) { // if the player is the last player who haven't folded
            if (peer.id.equals(getCurrentRoundState().getDealer())) {
                System.out.println("Last player, end the game!");
                List<Card> winningCards = gameState.deck.getCardsByIndex(gameState.findPlayerIndexById(winningId));
                getCurrentRoundState().addToTotalHoleCards(winningId, winningCards);
                GamePhase gpCommand = new GamePhase(peer.id, GamePhaseType.Result, null, getCurrentRoundState().getTotalHoleCards(), getCurrentRoundState().getWinningIds());
                for(String id : peer.getPeerIds()) { // send to all peers, including itself
                    sendCommand(id, "GamePhase", gpCommand.toJson());
                }
                new Thread(() -> {
                    try {
                        Thread.sleep(5000); // wait 5 seconds before resetting round
                        RoundStatus rs = new RoundStatus(peer.id, RoundStatusType.RoundEnded, getCurrentRoundState().getWinningIds());
                        sendGlobalCommand(peer.getPeerIds(), "RoundStatus", rs.toJson());
                        try { // Send new round status update to dealer
                            gameSpace.put("RoundStatus", rs.toJson());
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } catch (Exception e) {}
                }).start();
            }
        }
        return null;
    }

    public String makeCheckAction(){
        RoundState currentRoundState = getCurrentRoundState();
        List<Integer> allbets = currentRoundState.getBets();
        int myIndex = gameState.findPlayerIndexById(peer.id);
        int myBet = allbets.get(myIndex);
        int highestBet = Collections.max(allbets);
        if (myBet == highestBet){
            Action checkAction = new Action(peer.id, ActionType.Check, 0);
            sendGlobalCommand(peer.getPeerIds(), ACTION, checkAction.toJson());
            getCurrentRoundState().setIsMyTurn(false);
            printToScreen(getCurrentRoundState().getGamePhaseType().toString());
            if (peer.id.equals(getCurrentRoundState().getDealer())) {
                dealerPickNextPlayerOrNewPhase();
            }
        } else {
            return "You need to call the highest bet";
        }
        return null;
    }

    // finds out if there are more than one player which is still in the round
    // if not, and there is only 1 player, then it returns the id of the last player, otherwise null
    public String isOnlyOnePlayer() { // 
        String idOne = null;
        for (Player player : gameState.currentRoundState.getPlayers()) {
            if (player.inRound) { // check if there is another player which is alive
                if (idOne != null) {
                    return null;
                } 
                else {
                    idOne = player.getId();
                }
            }
        }
        return idOne; // assuming it cant be null (since the game would have ended before th)
    }


    public String makeRaiseAction(String amount){
        RoundState currentRoundState = getCurrentRoundState();
        // If a player bets more than their balance, they are all in
        int intAmount = Integer.parseInt(amount);

        List<Integer> allbets = getCurrentRoundState().getBets();
        int myIndex = gameState.findPlayerIndexById(peer.id);
        int myBet = allbets.get(myIndex);
        int highestBet = Collections.max(allbets);
        int myBetHighestBetDiff = highestBet - myBet; 
        int toBeRaisedWith = myBetHighestBetDiff + intAmount;
        allbets.set(myIndex, myBet + toBeRaisedWith);

        Player p = gameState.players.get(myIndex);
        if(toBeRaisedWith > p.balance) {
            toBeRaisedWith = p.balance;
        }
        p.balance -= toBeRaisedWith;

        currentRoundState.setLastRaise(peer.id);
        getCurrentRoundState().addToPot(toBeRaisedWith);
        Action raiseAction = new Action(peer.id, ActionType.Raise, intAmount);
        sendGlobalCommand(peer.getPeerIds(), ACTION, raiseAction.toJson());
        getCurrentRoundState().setIsMyTurn(false);
        printToScreen(getCurrentRoundState().getGamePhaseType().toString());
        return null;
    }

    public String makeCallAction(){
        getCurrentRoundState().calcPlayerCall(peer.id);
        Action callAction = new Action(peer.id, ActionType.Call, 0);
        sendGlobalCommand(peer.getPeerIds(), ACTION, callAction.toJson());
        getCurrentRoundState().setIsMyTurn(false);
        printToScreen(getCurrentRoundState().getGamePhaseType().toString());
        return null;
    }

    public void dealerPickNextPlayerOrNewPhase() {
        if (peer.id.equals(getCurrentRoundState().getDealer())) {
            if (isLastPlayer(peer.id)) { // if the dealer is the new player
                initNextPhase();
            } else {
                sendPlayerTurnCommand(peer.id); // else tell next player to move
            }
        }
    }

    // returns if a peer is the last player
    public boolean isLastPlayer(String peerId) {
        return getCurrentRoundState().getLastPlayer().equals(peerId);        
    }

    public String gameCommandHandler(String command){
        String[] commandTag = command.split(" ");
        if(commandTag.length < 2){
            return "No Game command sent";
        }
        if(!getCurrentRoundState().getIsMyTurn()){
            return "It is not your turn yet";
        }
        String res;
        switch(commandTag[1]){
            case "Fold": 
                res =  makeFoldAction();
                if (peer.id.equals(getCurrentRoundState().getDealer())) {
                    dealerPickNextPlayerOrNewPhase();
                }
                break;
            case "Raise":
                res = makeRaiseAction(commandTag[2]);
                if (peer.id.equals(getCurrentRoundState().getDealer())) {
                    dealerPickNextPlayerOrNewPhase();
                }                
                break;
            case "Call":
                res = makeCallAction();
                if (peer.id.equals(getCurrentRoundState().getDealer())) {
                    dealerPickNextPlayerOrNewPhase();
                }            
                break;
            case "Check":
                res = makeCheckAction();       
                break;
            default: return "Unknown game command: Use Fold, Raise, Call or Check";
        }
        return res;
    }


    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


    public void printToScreen(String GamePhase){
        clearScreen();
        System.out.println("Round: " + getCurrentRoundState().getroundId());
        System.out.println(GamePhase);
        System.out.println(getCurrentRoundState().getOwnPlayerObject().getInRound() ? "You are still in" : "You have folded");
        System.out.println("First Player: " + getCurrentRoundState().getFirstPlayer() +" | Smallblind: " + getCurrentRoundState().getSmallBlind() + " | Bigblind: "  +getCurrentRoundState().getBigBlind());
        System.out.println("Your Balance: " + getCurrentRoundState().getOwnPlayerObject().getBalance());
        System.out.println("Bets: " + getCurrentRoundState().getBets());
        System.out.println("Pot: " + getCurrentRoundState().getPot());
        System.out.println("CommunityCards: " + getCurrentRoundState().getCommunityCards());
        System.out.println("HoleCards: " + getCurrentRoundState().getOwnPlayerObject().getHoleCards());
    }
}