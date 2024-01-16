package dk.dtu.game;
import java.util.List;
import org.jspace.Tuple;
import dk.dtu.game.commands.Action;
import dk.dtu.game.commands.enums.ConnectionStatusType;
import dk.dtu.game.commands.enums.GamePhaseType;
import dk.dtu.game.commands.enums.RoundStatusType;
import dk.dtu.game.commands.ConnectionStatus;
import dk.dtu.game.commands.RoundStatus;
import dk.dtu.game.round.RoundState;
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
        switch(action.getAction()){
            case Fold:
                if(isFirstPlayer(action.getSenderId())){
                    gameClient.getCurrentRoundState().setNewFirstPlayer(action.getSenderId());
                    System.out.println(gameClient.getCurrentRoundState().getFirstPlayerId());
                }
                gameClient.getCurrentRoundState().setPlayerFolded(action.getSenderId());
                String winningId = gameClient.isOnlyOnePlayer();
                if(winningId != null){
                    System.out.println("You are the last player, you have won! (TODO)");
                    if(getOwnId().equals(getDealerId())){
                            gameClient.getCurrentRoundState().addWinningId(winningId);
                            RoundStatus rs = new RoundStatus(getOwnId(), RoundStatusType.RoundEnded, gameClient.getCurrentRoundState().getWinningId());
                            gameClient.sendGlobalCommand(gameClient.peer.getPeerIds(), "RoundStatus", rs.toJson());
                            try {
                                gameClient.gameSpace.put("RoundStatus", rs.toJson());
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                
                if(getOwnId().equals(getDealerId())){
                    if(!isLastPlayer(action.getSenderId())){  // if the sender was not the last player
                        gameClient.sendPlayerTurnCommand(action.getSenderId());
                    } else if(isLastPlayer(action.getSenderId())){ // else if the sender was the last player
                        System.out.println("Ready for Flop"); // goto next phase

                        gameClient.initNextPhase();
                    }
                }
                printToScreen();
                System.out.println("Last Move: " + action.getSenderId() +" did a " + action.getAction() +(action.getAmount()!=0 ? action.getAmount():"" ));
                break;
            case Raise:
                gameClient.getCurrentRoundState().calcPlayerRaise(action.getSenderId(), action.getAmount());
                gameClient.getCurrentRoundState().setLastRaise(action.getSenderId());
                if(getOwnId().equals(getDealerId())){
                    gameClient.sendPlayerTurnCommand(action.getSenderId());
                }
                printToScreen();
                System.out.println("Last Move: " + action.getSenderId() +" did a " + action.getAction() + (action.getAmount()!=0 ?" " +action.getAmount():"" ));
            break;

            case Check:
                if(getOwnId().equals(getDealerId())){
                    if(!isLastPlayer(action.getSenderId())){  // if the sender was not the last player
                        gameClient.sendPlayerTurnCommand(action.getSenderId());
                    } else if(isLastPlayer(action.getSenderId())){ // else if the sender was the last player
                        System.out.println("Ready for Flop"); // goto next phase

                        gameClient.initNextPhase();
                    }
                }
                printToScreen();
                System.out.println("Last Move: " + action.getSenderId() +" did a " + action.getAction() +(action.getAmount()!=0 ? action.getAmount():"" ));
                break;
            case Call:
                gameClient.getCurrentRoundState().calcPlayerCall(action.getSenderId());

                if(getOwnId().equals(getDealerId())){ // Check if own player is dealer
                    if(!isLastPlayer(action.getSenderId())){  // if the sender was not the last player
                            gameClient.sendPlayerTurnCommand(action.getSenderId());
                    } else if(isLastPlayer(action.getSenderId())){ // else if the sender was the last player
                        System.out.println("Ready for Flop"); // goto next phase

                            gameClient.initNextPhase();
                    }
                }
                printToScreen();
                System.out.println("Last Move: " + action.getSenderId() +" did a " + action.getAction() +(action.getAmount()!=0 ? action.getAmount():"" ));
                break;
            default:
                break;
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
                String json = (new ConnectionStatus(getOwnId(), ConnectionStatusType.Pong)).toJson();
                sendCommand(sendTo, commandString, json);
                break;
            case Pong:
                addPlayerToGameState(connectionStatus.getSenderId());
                System.out.println("Pong From: " + connectionStatus.getSenderId());
                if(!gameClient.connectionEstablishedToAll()){ break;}
                System.out.println("All Connections established");
                
                connectionStatusResponse = new ConnectionStatus(
                        getOwnId(),
                        ConnectionStatusType.ConnectionsEstablished
                );
                if(getOwnId().equals(getMPId())){ 
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
                System.out.println(connectionStatus.getSenderId()+ " is Ready.");
                if(isEveryoneReady()){
                   // TimeUnit.SECONDS.sleep(5);
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
                System.out.println();

                break;
            case Showdown:
                setGamePhaseType(GamePhaseType.Showdown);
                printToScreen();
                System.out.println("We are in showdown");
                if(!getOwnId().equals(getMPId())){ 
                    Hand hand = getPlayerHand();
                    gameClient.sendCommand(getDealerId(), "DeterminHand", hand.toJson());
                }
                // Calculate best hand
                // Send Best Hand To Dealer
            default:
                break;
                
        }
    }

    // only used by the dealer, when they recieve a "DetermineHand" command from a peer
    public void determinHandCommand(String jsonObject) {
        Hand peerHand = Hand.fromJson(jsonObject);
        System.out.println("Delear recieved hand: " + peerHand.getHand().toString());
        Hand winningHand = gameClient.getCurrentRoundState().getWinningHand(); // assume not null, since dealer sets first
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
        gameClient.getCurrentRoundState().incrementHandComparingCount();
        if(gameClient.getCurrentRoundState().getHandComparingCount() == gameClient.getCurrentRoundState().getPlayers().size()){
            RoundStatus rs = new RoundStatus(getOwnId(), RoundStatusType.RoundEnded, gameClient.getCurrentRoundState().getWinningId());
            gameClient.sendGlobalCommand(gameClient.peer.getPeerIds(), "RoundStatus", rs.toJson());
            try { // Send new round status update to dealer
                gameClient.gameSpace.put("RoundStatus", rs.toJson());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }   
    }

    public boolean isLastPlayer(String previusePeerId) {
        String nextPlayerId = gameClient.getCurrentRoundState().getNextNonFoldedPlayer(previusePeerId);
        String lastPlayer;
        //String lastRaiseId = gameClient.getCurrentRoundState().getLastRaise();
        if (gameClient.getCurrentRoundState().getLastRaise() != null) {
            lastPlayer = gameClient.getCurrentRoundState().getLastRaise();
        } else {
            lastPlayer = gameClient.getCurrentRoundState().getFirstPlayerId();
        }

        System.out.println("previusePeerId: " + previusePeerId + " | " + "nextPlayerId: " + nextPlayerId + " | lastPlayer: " + lastPlayer);
        return nextPlayerId.equals(lastPlayer);
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
                System.out.println("It is your turn to make a move");
            default:
                break;
        }
    }


    public void syncStateCommand(String jsonObject, RoundState roundState){
        SyncState syncState = SyncState.fromJson(jsonObject);
    }

	private void unknownCommand(Tuple messageTuple) {
        System.err.println("GameClient: Command unknown " +
        messageTuple.getElementAt(String.class, 0)+ ": "+ messageTuple.getElementAt(String.class, 1));
    }    
    
    

    // Auxiliary functions


    public void setGamePhaseType(GamePhaseType gamePhase){
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

    public String getMPId(){
        return gameClient.peer.MPID;
    }

    public void addPlayerToGameState(String playerId){
        gameClient.addPlayerToGameState(playerId);
    }
    
    public void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }


    public Hand getPlayerHand() {
        return new Hand(gameClient.getCurrentRoundState().getCommunityCards(), gameClient.getCurrentRoundState().getOwnPlayerObject().getHoleCards(), getOwnId());
    }

    public void printToScreen(){
        clearScreen();
        System.out.println(gameClient.getCurrentRoundState().getGamePhaseType().toString());
        System.out.println(gameClient.getCurrentRoundState().getOwnPlayerObject().getInRound() ? "You are still in" : "You have folded");
        System.out.println("Smallblind: " + gameClient.getCurrentRoundState().getSmallBlind() + " | Bigblind: "  +gameClient.getCurrentRoundState().getBigBlind());
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

