package dk.dtu.game;
import dk.dtu.game.round.RoundState;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;
import org.jspace.Tuple;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jspace.FormalField;
import org.jspace.QueueSpace;
import org.jspace.RemoteSpace;
import dk.dtu.game.commands.ConnectionStatus;
import dk.dtu.game.commands.GamePhase;
import dk.dtu.game.commands.RoundStatus;
import dk.dtu.game.commands.enums.ActionType;
import dk.dtu.game.commands.enums.ConnectionStatusType;
import dk.dtu.game.commands.enums.GamePhaseType;
import dk.dtu.game.commands.enums.RoundStatusType;
import dk.dtu.game.commands.Action;
import static dk.dtu.game.GameSettings.*;
import dk.dtu.network.Peer;

public class GameClient {
    public QueueSpace gameSpace;
    public SpaceRepository gameSpaces;
    public Peer peer;
    public GameState gameState;
    public final GameCommands gameCommands = new GameCommands(this);
    public GameClient(Peer peer) {
        gameSpace = new QueueSpace();
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
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void initGame() {
        clearScreen();
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
        return gameSpaces.size() == gameState.players.size();
    }


    public Space getPeerGameSpace(String peerId){
        return gameSpaces.get(peerId);
    }
    // TODO: Change player balance at some point
    public void addPlayerToGameState(String peerId){
        Player newPlayer = new Player(peerId, START_BALANCE);
        gameState.addPlayer(newPlayer);
    }

    // TODO: Move to RoundLogic (18th of January)

    public void startNewRound(){
        RoundStatus roundStatus = new RoundStatus(peer.id, RoundStatusType.NewRoundStarted);
        sendGlobalCommand(peer.getPeerIds(), "RoundStatus", roundStatus.toJson());
        gameState.createNewRoundState(peer.id);
        startGamePhases();
    }

    public void startGamePhases(){
        initNextPhase();
    }
    
    // TODO: Move to RoundLogic (18th of January)
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


    public void initPreFlop() {
        gameState.resetDeck();
        List<String> PeerIds = peer.getPeerIds();
        List<Card> holeCards;
        for(String id : PeerIds) { // the dealer deals 2 cards to each player
            holeCards = gameState.deck.draw(2);
            GamePhase gpCommand = new GamePhase(peer.id, GamePhaseType.PreFlop, holeCards);
            sendCommand(id, "GamePhase", gpCommand.toJson());
        }
        String bigBlindId = getCurrentRoundState().getBigBlind();
        sendPlayerTurnCommand(bigBlindId);
        // Send command to player that have to do the first action

    }

    public void sendPlayerTurnCommand(String previusePeerId) {
        try {
            String firstPlayerId = getCurrentRoundState().getNextNonFoldedPlayer(previusePeerId);
            RoundStatus rsCommand = new RoundStatus(peer.id, RoundStatusType.PlayerTurn);
            if (firstPlayerId.equals(peer.id)) {
                gameSpace.put("RoundStatus", rsCommand.toJson());
            } else {
                sendCommand(firstPlayerId, "RoundStatus", rsCommand.toJson());
            }
        } catch (InterruptedException e){
            System.err.println("Error sendPlayerTurnCommand");
            e.printStackTrace();
        }
    }
    

    public void initFlop(){
        List<Card> communityCards = gameState.deck.draw(3);
        List<String> PeerIds = peer.getPeerIds();
        for(String id : PeerIds) {
            GamePhase gpCommand = new GamePhase(peer.id, GamePhaseType.Flop, communityCards);
            sendCommand(id, "GamePhase", gpCommand.toJson());
        }
        String bigBlindId = getCurrentRoundState().getBigBlind();
        sendPlayerTurnCommand(bigBlindId);
    }

    public void initTurn(){
        List<Card> communityCards = gameState.deck.draw(1);
        List<String> PeerIds = peer.getPeerIds();
        for(String id : PeerIds) {
            GamePhase gpCommand = new GamePhase(peer.id, GamePhaseType.Turn, communityCards);
            sendCommand(id, "GamePhase", gpCommand.toJson());
        }
        String bigBlindId = getCurrentRoundState().getBigBlind();
        sendPlayerTurnCommand(bigBlindId);
    }
    public void initRiver(){
        List<Card> communityCards = gameState.deck.draw(1);
        List<String> PeerIds = peer.getPeerIds();
        for(String id : PeerIds) {
            GamePhase gpCommand = new GamePhase(peer.id, GamePhaseType.River, communityCards);
            sendCommand(id, "GamePhase", gpCommand.toJson());
        }
        String bigBlindId = getCurrentRoundState().getBigBlind();
        sendPlayerTurnCommand(bigBlindId);
    }

    public void initShowdown(){
        List<String> PeerIds = peer.getPeerIds();
        for(String id : PeerIds) {
            GamePhase gpCommand = new GamePhase(peer.id, GamePhaseType.Showdown, new ArrayList<Card>());
            sendCommand(id, "GamePhase", gpCommand.toJson());
        }
    }

    public RoundState getCurrentRoundState(){
        return gameState.currentRoundState;
    }



    public void makeFoldAction(){
        Action foldAction = new Action(peer.id, ActionType.Fold, 0);
        getCurrentRoundState().setPlayerFolded(peer.id);
        sendGlobalCommand(peer.getPeerIds(), "Action", foldAction.toJson());
        printToScreen(getCurrentRoundState().getGamePhaseType().toString());
    }

    public void makeCheckAction(){
        RoundState currentRoundState = getCurrentRoundState();
        List<Integer> allbets = currentRoundState.getBets();
        int myIndex = gameState.findPlayerIndexById(peer.id);
        int myBet = allbets.get(myIndex);
        int highestBet = Collections.max(allbets);
        if(myBet ==highestBet){
            Action checkAction = new Action(peer.id, ActionType.Check, 0);
            sendGlobalCommand(peer.getPeerIds(), "Action", checkAction.toJson());
        } else {
            System.out.println("----\n"+allbets);
            System.out.println("You need to call the highest bet");
        }
        printToScreen(getCurrentRoundState().getGamePhaseType().toString());
    }

    public boolean isOnlyPlayer() { // check if the player is the only player who have not folded
        for (Player player : gameState.currentRoundState.getPlayers()) {
            if (player.id.equals(peer.id) && player.inRound) {
                return false;
            }
        }
        return true;
    }

    
    // public void addPotToPlayerBalance() {
    //     for (Player player : gameClient.gameState.currentRoundState.getPlayers()) {
    //      if(isOnlyPlayer()) {
    //         player.balance+=
    //      }


    //     }
    // }



    public void makeRaiseAction(String amount){
        RoundState currentRoundState = getCurrentRoundState();
        
        // If a player bets more than their balance, they are all in

        int intAmount = Integer.parseInt(amount);

        List<Integer> allbets = getCurrentRoundState().getBets();

        int myIndex = gameState.findPlayerIndexById(peer.id);

        int myBet = allbets.get(myIndex);

        int highestBet = Collections.max(allbets);

        int myBetHighestBetDiff = highestBet - myBet; 

        int toBeRaisedWith = myBetHighestBetDiff+intAmount;
        allbets.set(myIndex, myBet+toBeRaisedWith);
        
        
        Player p = gameState.players.get(myIndex);
        if(toBeRaisedWith> p.balance) {
            toBeRaisedWith=p.balance;
        }

        p.balance -= toBeRaisedWith;
        getCurrentRoundState().addToPot(toBeRaisedWith);
        Action raiseAction = new Action(peer.id, ActionType.Raise, intAmount);
        sendGlobalCommand(peer.getPeerIds(), "Action", raiseAction.toJson());
        printToScreen(getCurrentRoundState().getGamePhaseType().toString());
    }
    public void makeCallAction(){
        //TODO: Update own roundState, so balance and bets matches
        getCurrentRoundState().calcPlayerCall(peer.id);
        Action callAction = new Action(peer.id, ActionType.Call, 0);
        sendGlobalCommand(peer.getPeerIds(), "Action", callAction.toJson());
        printToScreen(getCurrentRoundState().getGamePhaseType().toString());

    }


    public void printToScreen(String GamePhase){
        clearScreen();
        System.out.println(GamePhase);
        System.out.println("Smallblind: " + getCurrentRoundState().getSmallBlind() + " | Bigblind: "  +getCurrentRoundState().getBigBlind());
        System.out.println("Bets: " + getCurrentRoundState().getBets());
        System.out.println("Pot: " + getCurrentRoundState().getPot());
        System.out.println("CommunityCards: " + getCurrentRoundState().getCommunityCards());
        System.out.println("HoleCards: " + getCurrentRoundState().getOwnPlayerObject().getHoleCards());
    }
    public void dealerPickNextPlayerOrNewPhase(){
        if(peer.id.equals(getCurrentRoundState().getDealer())){
            if(isLastPlayer(peer.id)){
                initNextPhase();
            }else{
            sendPlayerTurnCommand(peer.id);
            }
        }

    }
    public boolean isLastPlayer(String previusePeerId) {
        String nextPlayerId = getCurrentRoundState().getNextNonFoldedPlayer(previusePeerId);
        String lastPlayer;
        if (getCurrentRoundState().getLastRaise() != null) {
            lastPlayer = getCurrentRoundState().getLastRaise();
        } else {
            lastPlayer = getCurrentRoundState().getFirstPlayerId();
        }

        return nextPlayerId.equals(lastPlayer);
    }
    public void gameCommandHandler(String command){
        String[] commandTag = command.split(" ");
        if(commandTag.length < 2){
            System.out.println("No Game command sent");
            return;
        }
        if(!getCurrentRoundState().getIsMyTurn()){
            System.out.println("It is not your turn yet");
            return;
        }
        switch(commandTag[1]){
            case "Fold": 
                dealerPickNextPlayerOrNewPhase();
                makeFoldAction();
                getCurrentRoundState().setIsMyTurn(false);
                break;
            case "Raise":
                makeRaiseAction(commandTag[2]);
                dealerPickNextPlayerOrNewPhase();
                getCurrentRoundState().setIsMyTurn(false);
                break;
            case "Call":
                makeCallAction();
                dealerPickNextPlayerOrNewPhase();
                getCurrentRoundState().setIsMyTurn(false);
                break;
            case "Check":
                makeCheckAction();
                dealerPickNextPlayerOrNewPhase();
                getCurrentRoundState().setIsMyTurn(false);
            break;
            default: System.out.println("Unknown game command: Use Fold, Raise, Call or Check");
        }
    }
}