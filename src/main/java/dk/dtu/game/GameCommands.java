package dk.dtu.game;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jspace.Tuple;
import dk.dtu.game.commands.Action;
import dk.dtu.game.commands.enums.ConnectionStatusType;
import dk.dtu.game.commands.enums.GamePhaseType;
import dk.dtu.game.commands.enums.RoundStatusType;
import dk.dtu.game.commands.ConnectionStatus;
import dk.dtu.game.commands.RoundStatus;
import dk.dtu.game.round.RoundState;
import javafx.concurrent.Task;
import dk.dtu.game.commands.GamePhase;
import dk.dtu.game.commands.SyncState;

public class GameCommands{
    GameClient gameClient;

    public GameCommands(GameClient gameClient){
        this.gameClient = gameClient;
    }
   
    int readyCount = 0;
    public boolean isEveryoneReady(){
        return readyCount == gameClient.peer.chat.chats.size()+1;
    }
    
    public void commandHandler(Tuple messageTuple, RoundState roundState) throws InterruptedException {
        String command = messageTuple.getElementAt(String.class, 0);
        String jsonObject = messageTuple.getElementAt(String.class, 1);
       
        switch (command) {
            case "Action": actionCommand(jsonObject); break;
            case "ConnectionStatus": connectionStatusCommand(jsonObject); break;
            case "GamePhase": gamePhaseCommand(jsonObject); break;
            case "SyncState": syncStateCommand(jsonObject, roundState); break;
            case "RoundStatus": roundStatusCommand(jsonObject, roundState); break;
            case "DeterminHand": determinHandCommand(jsonObject); break;
            default: unknownCommand(messageTuple); break;
            // Mangler command for showdown, newRoundStarted, RoundEnded og Broke
        }
    }

    // function to hande an incoming action from another player
    public void actionCommand(String jsonObject){
        Action action = Action.fromJson(jsonObject);
        switch (action.getAction()) {
            case Fold:
                Boolean updateLast = false;
                gameClient.getCurrentRoundState().setPlayerFolded(action.getSenderId());
                if (isFirstPlayer(action.getSenderId())) {
                    System.out.println("Was the first player");
                    gameClient.getCurrentRoundState().setNewFirstPlayer(action.getSenderId());
                    System.out.println("New first player: " + gameClient.getCurrentRoundState().getFirstPlayerId());
                }
                String winningId = gameClient.isOnlyOnePlayer();

                if (gameClient.isLastPlayer(action.getSenderId())) {
                    updateLast = true;
                    if (gameClient.getCurrentRoundState().getOrigLastPlayer().equals(action.getSenderId())) { // if the actual last
                        gameClient.getCurrentRoundState().updateLastPlayer();
                    }
                }
                if (winningId != null) { 
                    // Have not looked at this part yet.
                    if (isDealer()) {
                        gameClient.getCurrentRoundState().addWinningId(winningId); // cannot get the cards of the winner, but who cares
                        List<Card> winningCards = gameClient.gameState.deck.getCardsByIndex(gameClient.gameState.findPlayerIndexById(winningId));
                        gameClient.getCurrentRoundState().addToTotalHoleCards(winningId, winningCards);
                        GamePhase gpCommand = new GamePhase(getOwnId(), GamePhaseType.Result, null, gameClient.getCurrentRoundState().getTotalHoleCards(), gameClient.getCurrentRoundState().getWinningIds());
                        for(String id : gameClient.peer.getPeerIds()) { // send to all peers, including itself
                            sendCommand(id, "GamePhase", gpCommand.toJson());
                        }
                        new Thread(() -> {
                            try {
                                Thread.sleep(5000); // wait 5 seconds before resetting round
                                RoundStatus rs = new RoundStatus(getOwnId(), RoundStatusType.RoundEnded, gameClient.getCurrentRoundState().getWinningIds());
                                gameClient.sendGlobalCommand(gameClient.peer.getPeerIds(), "RoundStatus", rs.toJson());
                                try { // Send new round status update to dealer
                                    gameClient.gameSpace.put("RoundStatus", rs.toJson());
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {}
                        }).start();
                    }
                } else { // game is not over
                    if (isDealer()) { // if delear
                        if (updateLast) { // the folded player was the last player in the round, so continue
                            System.out.println("Ready for next phase"); // goto next phase
                            gameClient.initNextPhase();
                        } else {
                            gameClient.sendPlayerTurnCommand(action.getSenderId());
                        }
                    }
                }
                printToScreen();
                System.out.println("Last Move: " + action.getSenderId() +" did a " + action.getAction() +(action.getAmount()!=0 ? action.getAmount():"" ));
                if (gameClient.getCurrentRoundState().getIsMyTurn()) {
                    showIsYourTurn();
                }
                break;
            case Raise:
                gameClient.getCurrentRoundState().calcPlayerRaise(action.getSenderId(), action.getAmount());
                gameClient.getCurrentRoundState().setLastRaise(action.getSenderId());
                if (isDealer()) {
                    gameClient.sendPlayerTurnCommand(action.getSenderId());
                }
                printToScreen();
                System.out.println("Last Move: " + action.getSenderId() + " did a " + action.getAction() + (action.getAmount() !=0  ? " " + action.getAmount() : "" ));
                if (gameClient.getCurrentRoundState().getIsMyTurn()) {
                    showIsYourTurn();
                }
                break;
            case Check:
                dealerPickNextPlayerOrNewPhase(action.getSenderId());
                printToScreen();
                System.out.println("Last Move: " + action.getSenderId() +" did a " + action.getAction() +(action.getAmount()!=0 ? action.getAmount():"" ));
                if (gameClient.getCurrentRoundState().getIsMyTurn()) {
                    showIsYourTurn();
                }
                break;
            case Call:
                gameClient.getCurrentRoundState().calcPlayerCall(action.getSenderId());
                dealerPickNextPlayerOrNewPhase(action.getSenderId());
                printToScreen();
                System.out.println("Last Move: " + action.getSenderId() +" did a " + action.getAction() +(action.getAmount()!=0 ? action.getAmount():"" ));
                if (gameClient.getCurrentRoundState().getIsMyTurn()) {
                    showIsYourTurn();
                }
                break;
            default:
                break;
        }
    }

    public void showIsYourTurn() {
        System.out.println("It is your turn to make a move");
    }

    public boolean isDealer(){
        return getOwnId().equals(getDealerId());
    }

    public void dealerPickNextPlayerOrNewPhase(String peerId) {
        if (isDealer()) {
            if (!gameClient.isLastPlayer(peerId)) {  // if the sender was not the last player
                gameClient.sendPlayerTurnCommand(peerId); // tell next player to move
            } else if (gameClient.isLastPlayer(peerId)) { // else if the sender was the last player
                System.out.println("Ready for Flop"); // goto next phase
                gameClient.initNextPhase();
            }
        }
    }

    public void connectionStatusCommand(String jsonObject) throws InterruptedException {
        ConnectionStatus connectionStatus = ConnectionStatus.fromJson(jsonObject);
        ConnectionStatus connectionStatusResponse;
        switch(connectionStatus.getConnectionStatus()){
            case Ping:
                System.out.println("Ping From: " + connectionStatus.getSenderId());
                String sendTo = connectionStatus.getSenderId();
                String commandString = "ConnectionStatus";
                String json = (new ConnectionStatus(getOwnId(), ConnectionStatusType.Pong, getOwnName())).toJson();
                sendCommand(sendTo, commandString, json);
                break;
            case Pong:
                addPlayerToGameState(connectionStatus.getSenderId(), connectionStatus.getSenderName());
                System.out.println("Pong From: " + connectionStatus.getSenderId());
                if(!gameClient.connectionEstablishedToAll()){ break;}
                System.out.println("All Connections established");
                connectionStatusResponse = new ConnectionStatus(
                    getOwnId(),
                    ConnectionStatusType.ConnectionsEstablished,
                    getOwnName()
                );
                if (getOwnId().equals(getMPId())) { 
                    gameClient.gameSpace.put("ConnectionStatus", connectionStatusResponse.toJson());                  
                    break; 
                }    
                sendCommand(
                    getMPId(),
                    "ConnectionStatus",
                    connectionStatusResponse.toJson()
                );
                break;
            case ConnectionsEstablished:
                readyCount++;
                System.out.println(connectionStatus.getSenderId() + " is Ready.");
                if (isEveryoneReady()) {
                    gameClient.startNewRound();
                }
                break;
            default:
                System.err.println("connectionStatus type unknown. Received: " + connectionStatus.getConnectionStatus());
        }
    } 
    

    public void gamePhaseCommand(String jsonObject){
        GamePhase gamePhase = GamePhase.fromJson(jsonObject);
        switch(gamePhase.getGamePhase()){
            case PreFlop:
                setGamePhaseType(GamePhaseType.PreFlop);
                setHoleCards(gamePhase.getCards());
                gameClient.getCurrentRoundState().setLastRaise(null);
                calcBlindBets();
                printToScreen();
                break;
            case Flop:
                setGamePhaseType(GamePhaseType.Flop);
                addCardsToCommunityCards(gamePhase.getCards());
                gameClient.getCurrentRoundState().setLastRaise(null);
                printToScreen();
                break;
            case Turn:
                setGamePhaseType(GamePhaseType.Turn);
                addCardsToCommunityCards(gamePhase.getCards());
                gameClient.getCurrentRoundState().setLastRaise(null);
                printToScreen();
                break;
            case River:
                setGamePhaseType(GamePhaseType.River);
                addCardsToCommunityCards(gamePhase.getCards());
                gameClient.getCurrentRoundState().setLastRaise(null);
                printToScreen();
                break;
            case Showdown:
                setGamePhaseType(GamePhaseType.Showdown);
                System.out.println("We are in showdown");
                if(!getOwnId().equals(getMPId())){ 
                    Hand hand = getPlayerHand();
                    gameClient.sendCommand(getDealerId(), "DeterminHand", hand.toJson());
                }
                break;
            case Result:
                gameClient.getCurrentRoundState().setTotalHoleCards(gamePhase.getTotalHoleCards()); 
                gameClient.getCurrentRoundState().setWinningIds(gamePhase.getWinningIds());
                printToScreen();
                break;
            default:
                break;
        }
    }

    // only used by the dealer, when they recieve a "DetermineHand" command from a peer
    public void determinHandCommand(String jsonObject) {
        Hand peerHand = Hand.fromJson(jsonObject);
        System.out.println("Delear recieved hand: " + peerHand.getHand().toString());
        Hand winningHand = gameClient.getCurrentRoundState().getWinningHand(); // assume not null, since dealer sets first
        if (gameClient.getCurrentRoundState().getPlayer(peerHand.getId()).getInRound()) { // compare if new best
            if (peerHand.compareTo(winningHand) > 0) {
                gameClient.getCurrentRoundState().setWinningHand(peerHand);
                gameClient.getCurrentRoundState().setWinningId(peerHand.getId());
                System.out.println("New winning hand, maybe");
            } else if (peerHand.compareTo(winningHand) == 0) {
                System.out.println("Tie, between two of the hands");
                gameClient.getCurrentRoundState().addWinningId(peerHand.getId());
            } else {
                System.out.println("Worse hand");
            }
        }
        gameClient.getCurrentRoundState().addToTotalHoleCards(peerHand.getId(), peerHand.getHoleCards());
        gameClient.getCurrentRoundState().incrementHandComparingCount();
        
        if (gameClient.getCurrentRoundState().getHandComparingCount() == gameClient.getCurrentRoundState().getPlayers().size()) {  
            try {
                GamePhase gpCommand = new GamePhase(getOwnId(), GamePhaseType.Result, null, gameClient.getCurrentRoundState().getTotalHoleCards(), gameClient.getCurrentRoundState().getWinningIds());
                for(String id : gameClient.peer.getPeerIds()) { // send to all peers, including itself
                    sendCommand(id, "GamePhase", gpCommand.toJson());
                }
                new Thread(() -> {
                    try {
                        Thread.sleep(8000); // wait 8 seconds before resetting round
                        RoundStatus rs = new RoundStatus(getOwnId(), RoundStatusType.RoundEnded, gameClient.getCurrentRoundState().getWinningIds());
                        gameClient.sendGlobalCommand(gameClient.peer.getPeerIds(), "RoundStatus", rs.toJson());
                        try { // Send new round status update to dealer
                            gameClient.gameSpace.put("RoundStatus", rs.toJson());
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } catch (Exception e) {}
                }).start();
                // have found the winners

            } catch (Exception e) {}
            
        }   
    }

    public boolean isMyTurn(String previusePeerId) {
        if (!gameClient.getCurrentRoundState().getOwnPlayerObject().getInRound()) {
            return false;
        }
        String nextPlayerId = gameClient.getCurrentRoundState().nextPlayer(previusePeerId);
        boolean isFolded = gameClient.getCurrentRoundState().hasPlayerFolded(nextPlayerId);
        if (isFolded) {
            return isMyTurn(nextPlayerId);
        }
        return getOwnId().equals(nextPlayerId);
    }

    public boolean isFirstPlayer(String peerId) {
        return gameClient.getCurrentRoundState().getFirstPlayerId().equals(peerId);
    }

    // recived by all peers
    public void roundStatusCommand(String jsonObject, RoundState roundState) {
        RoundStatus roundStatus = RoundStatus.fromJson(jsonObject);
        //RoundStatus roundStatusResponse;
        switch (roundStatus.getRoundStatus()) {
            case NewRoundStarted:
                createNewRoundState();
                if(getOwnId().equals(gameClient.getCurrentRoundState().getDealer()) && gameClient.getCurrentRoundState().getroundId() != 0){
                    gameClient.startGamePhases();
                }
                break;
            case RoundEnded: 
                System.out.println("Recieved round ending");
                // split the pot amoung the winning players (simplified)
                int winning = (gameClient.getCurrentRoundState().getPot() / roundStatus.getWinners().size());
                for(String id : roundStatus.getWinners()) {
                    gameClient.getCurrentRoundState().getPlayer(id).addToBalance(winning);
                }
                if(getOwnId().equals(gameClient.getCurrentRoundState().getDealer())){
                    RoundStatus newRoundRs = new RoundStatus(getOwnId(), RoundStatusType.NewRoundStarted);
                    gameClient.sendGlobalCommand(gameClient.peer.getPeerIds(), "RoundStatus", newRoundRs.toJson());
                    try {
                        gameClient.gameSpace.put("RoundStatus", newRoundRs.toJson());
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case PlayerTurn:
                gameClient.getCurrentRoundState().setIsMyTurn(true);
                showIsYourTurn();
            default:
                break;
        }
    }

    public void syncStateCommand(String jsonObject, RoundState roundState){
        SyncState syncState = SyncState.fromJson(jsonObject);
    }

	private void unknownCommand(Tuple messageTuple) {
        System.err.println("GameClient: Command unknown " +
        messageTuple.getElementAt(String.class, 0) + ": "+ messageTuple.getElementAt(String.class, 1));
    }

    public void setGamePhaseType(GamePhaseType gamePhase) {
        gameClient.getCurrentRoundState().setGamePhaseType(gamePhase);
    }

    public String getDealerId(){
        return gameClient.getCurrentRoundState().getDealer();
    }

    public void addCardsToCommunityCards(List<Card> communityCards){
        gameClient.gameState.currentRoundState.addCardsToCommunityCards(communityCards);
    }
    
    public void setHoleCards(List<Card> holeCards){
        gameClient.getCurrentRoundState().getOwnPlayerObject().setHoleCards(holeCards);
    }

    public void calcBlindBets(){
        gameClient.gameState.currentRoundState.calculateBlindsBet();
    }

    public void createNewRoundState(){
        gameClient.gameState.createNewRoundState(gameClient.peer.id);
    }

    public void sendCommand(String receiverId, String commandString, String json){
        gameClient.sendCommand(receiverId, commandString, json);
    }

    public String getOwnId(){
        return gameClient.peer.id;
    }

    public String getOwnName(){
        return gameClient.peer.name;
    }

    public String getMPId(){
        return gameClient.peer.MPID;
    }

    public void addPlayerToGameState(String playerId, String pName) {
        gameClient.addPlayerToGameState(playerId, pName);
    }

    public static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try { Thread.sleep(millis); }
                catch (InterruptedException e) { }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }
    
    public void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    public Hand getPlayerHand() {
        return new Hand(gameClient.getCurrentRoundState().getCommunityCards(), gameClient.getCurrentRoundState().getOwnPlayerObject().getHoleCards(), getOwnId());
    }

    public void printToScreen(){
        // clearScreen();
        System.out.println("\n");
        System.out.println("Round: " + gameClient.getCurrentRoundState().getroundId());
        System.out.println(gameClient.getCurrentRoundState().getGamePhaseType().toString());
        System.out.println(gameClient.getCurrentRoundState().getOwnPlayerObject().getInRound() ? "You are still in" : "You have folded");
        System.out.println("First Player: " + gameClient.getCurrentRoundState().getFirstPlayer() +" | Smallblind: " + gameClient.getCurrentRoundState().getSmallBlind() + " | Bigblind: "  +gameClient.getCurrentRoundState().getBigBlind());
        System.out.println("Your Balance: " + gameClient.getCurrentRoundState().getOwnPlayerObject().getBalance());
        System.out.println("Bets: " + gameClient.getCurrentRoundState().getBets());
        System.out.println("Pot: " + gameClient.getCurrentRoundState().getPot());
        System.out.println("CommunityCards: " + gameClient.getCurrentRoundState().getCommunityCards());
        System.out.println("HoleCards: " + gameClient.getCurrentRoundState().getOwnPlayerObject().getHoleCards());
    }
}


 // private void preFlopCommand(String jsonObject, RoundState roundState) {
    //     // Implementer PreFlop logik her
    //     String dealerId = roundState.getDealer();

    //     // request 2 cards
    //     gameClient.sendCommand(dealerId, "RequestHoleCards", "{'senderId':'"+gameClient.peer.id+"'}");
    //     // await 2 cards

    //     // send message that the 2 cards have been received
    //     // update roundState holeCards
    //     // calculate Blinds
    //     // Update roundState
    //     // calculate pot from small/big blind fee
    //     // Update roundState Pot
    //     // calculate small/big blind balance
    //     // Update round state players balance
    //     // Send message to dealer that roundState is updated and are ready to continue
    // }
    // private void sendHoleCardsCommand(String jsonObject, RoundState roundState) {
    //     SendHoleCards data = SendHoleCards.fromJson(jsonObject);
    //     roundState.getPlayer().getHoleCards().addAll(data.getHoleCards());
    //     // Implementer SendCards logik her
    // }

