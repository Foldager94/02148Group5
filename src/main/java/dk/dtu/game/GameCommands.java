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
    private static final String ACTION = "Action";
    private static final String CONNECTION_STATUS = "ConnectionStatus";
    private static final String GAME_PHASE = "GamePhase";
    private static final String SYNC_STATE = "SyncState";
    private static final String ROUND_STATUS = "RoundStatus";
    private static final String DETERMINE_HAND = "DetermineHand";
    private static final String UNKNOWN_COMMAND_MESSAGE = "GameClient: Command unknown ";
    GameClient gameClient;

    public GameCommands(GameClient gameClient){
        this.gameClient = gameClient;
    }
   
    int readyCount = 0;
    public boolean isEveryoneReady(){
        return readyCount == gameClient.peer.chat.chats.size()+1;
    }
    
    public void commandHandler(Tuple messageTuple) throws InterruptedException {
        String command = messageTuple.getElementAt(String.class, 0);
        String jsonObject = messageTuple.getElementAt(String.class, 1);
       
        switch (command) {
            case ACTION: actionCommand(jsonObject); break;
            case CONNECTION_STATUS: connectionStatusCommand(jsonObject); break;
            case GAME_PHASE: gamePhaseCommand(jsonObject); break;
            case SYNC_STATE: syncStateCommand(jsonObject); break;
            case ROUND_STATUS: roundStatusCommand(jsonObject); break;
            case DETERMINE_HAND: determineHandCommand(jsonObject); break;
            default: unknownCommand(messageTuple); break;
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
                    gameClient.getCurrentRoundState().setNewFirstPlayer(action.getSenderId());
                }
                if (gameClient.isLastPlayer(action.getSenderId())) {
                    gameClient.getCurrentRoundState().updateLastPlayer();
                    updateLast = true;
                }
                String winningId = gameClient.isOnlyOnePlayer();
                if (winningId != null) { 
                    // Have not looked at this part yet.
                    if (isDealer()) {
                        gameClient.getCurrentRoundState().addWinningId(winningId);
                        sendRoundEndedCommand();
                    }
                } else { // game is not over
                    if (isDealer()) { // if dealer
                        if (updateLast) { // the folded player was the last player in the round, so continue
                            gameClient.initNextPhase();
                        } else {
                            gameClient.sendPlayerTurnCommand(action.getSenderId());
                        }
                    }
                }
                printToScreen();
                printActionMade(action);
                break;
            case Raise:
                gameClient.getCurrentRoundState().calcPlayerRaise(action.getSenderId(), action.getAmount());
                gameClient.getCurrentRoundState().setLastRaise(action.getSenderId());
                if (isDealer()) {
                    gameClient.sendPlayerTurnCommand(action.getSenderId());
                }
                printToScreen();
                printActionMade(action);
                break;
            case Check:
                dealerPickNextPlayerOrNewPhase(action.getSenderId());
                printToScreen();
                printActionMade(action);
                break;
            case Call:
                gameClient.getCurrentRoundState().calcPlayerCall(action.getSenderId());
                dealerPickNextPlayerOrNewPhase(action.getSenderId());
                printToScreen();
                printActionMade(action);
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
                    try {
                        gameClient.gameSpace.put("ConnectionStatus", connectionStatusResponse.toJson());
                    } catch (InterruptedException e) {
                        System.out.println("GameCommands.connectionStatusCommand");
                        System.out.println("jsonObject = " + jsonObject);
                        System.out.println("e = " + e);
                        e.printStackTrace();
                    }
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
                if(isEveryoneReady()){
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
                if(!getOwnId().equals(getMPId())){ 
                    Hand hand = getPlayerHand();
                    gameClient.sendCommand(getDealerId(), DETERMINE_HAND, hand.toJson());
                }
            default:
                break;
                
        }
    }

    // only used by the dealer, when they recieve a "determineeHand" command from a peer
    public void determineHandCommand(String jsonObject) {
        Hand peerHand = Hand.fromJson(jsonObject);
        Hand winningHand = gameClient.getCurrentRoundState().getWinningHand(); // assume not null, since dealer sets first
        if (peerHand.compareTo(winningHand) > 0) {
            gameClient.getCurrentRoundState().setWinningHand(peerHand);
            gameClient.getCurrentRoundState().setWinningId(peerHand.getId());
        } else if (peerHand.compareTo(winningHand) == 0) {
            gameClient.getCurrentRoundState().addWinningId(peerHand.getId());
        }
        gameClient.getCurrentRoundState().incrementHandComparingCount();
        if(gameClient.getCurrentRoundState().getHandComparingCount() == gameClient.getCurrentRoundState().getPlayers().size()){
            sendRoundEndedCommand();
        }   
    }



    // recived by all peers
    public void roundStatusCommand(String jsonObject) {
        RoundStatus roundStatus = RoundStatus.fromJson(jsonObject);
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
                gameClient.gameState.addRoundStateToHistory();
                if(getOwnId().equals(gameClient.getCurrentRoundState().getDealer())){
                    RoundStatus newRoundRs = new RoundStatus(getOwnId(), RoundStatusType.NewRoundStarted);
                    gameClient.sendGlobalCommand(gameClient.peer.getPeerIds(), ROUND_STATUS, newRoundRs.toJson());
                    try {
                        gameClient.gameSpace.put(ROUND_STATUS, newRoundRs.toJson());
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


    public boolean isDealer(){
        return getOwnId().equals(getDealerId());
    }

    public void sendRoundEndedCommand() {
        RoundStatus rs = new RoundStatus(getOwnId(), RoundStatusType.RoundEnded, gameClient.getCurrentRoundState().getWinningId());
        gameClient.sendGlobalCommand(gameClient.peer.getPeerIds(), ROUND_STATUS, rs.toJson());
        try { // Send new round status update to dealer
            gameClient.gameSpace.put(ROUND_STATUS, rs.toJson());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    public boolean isFirstPlayer(String peerId) {
        return gameClient.getCurrentRoundState().getFirstPlayerId().equals(peerId);
    }

    public void syncStateCommand(String jsonObject){
        SyncState syncState = SyncState.fromJson(jsonObject);
    }

	private void unknownCommand(Tuple messageTuple) {
        System.err.println(UNKNOWN_COMMAND_MESSAGE +
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

    public void printActionMade(Action action){
        System.out.println("Last Move: " + action.getSenderId() +" did a " + action.getAction() +(action.getAmount()!=0 ? action.getAmount():"" ));
        if (gameClient.getCurrentRoundState().getIsMyTurn()) {
            System.out.println("It is your turn to make a move");
        }
    }
}


