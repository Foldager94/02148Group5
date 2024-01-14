package dk.dtu.game;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jspace.Tuple;

import com.google.gson.Gson;

import dk.dtu.game.commands.SendHoleCards;
import dk.dtu.game.commands.Action;
import dk.dtu.game.commands.enums.ActionType;

import dk.dtu.game.commands.enums.ConnectionStatusType;
import dk.dtu.game.commands.enums.GamePhaseType;
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
        return readyCount == gameClient.peer.chat.chats.size();
    }
    

    public void commandHandler(Tuple messageTuple, RoundState roundState) throws InterruptedException {
        String command = messageTuple.getElementAt(String.class, 0);
        String jsonObject = messageTuple.getElementAt(String.class, 1);
       
        switch (command) {
            case "Action": actionCommand(jsonObject, roundState); break;
            case "ConnectionStatus": connectionStatusCommand(jsonObject, roundState); break;
            case "GamePhase": gamePhaseCommand(jsonObject, roundState); break;
            case "SyncState": syncStateCommand(jsonObject, roundState); break;
            case "RoundStatus": roundStatusCommand(jsonObject, roundState); break;
            default: unknownCommand(messageTuple); break;
            // Mangler command for showdown, newRoundStarted, RoundEnded og Broke
        }
    }

    // function to hande an incoming action from another player
    public void actionCommand(String jsonObject, RoundState roundState){
        Action action = Action.fromJson(jsonObject);
        switch(action.getAction()){
            case Fold:
                gameClient.getCurrentRoundState().setPlayerFolded(action.getSenderId());
                if (gameClient.isOnlyPlayer()) {
                    System.out.println("You are the last player, you have won! (TODO)");
                }
                if(!isLastPlayer(action.getSenderId())){
                    if(isMyTurn(action.getSenderId())) { // check if its now the players turn
                        System.out.println("Make a Move");
                    }
                } else if(isLastPlayer(action.getSenderId())){
                    System.out.println("Ready for dealer to turn card(s)");
                    if(getOwnId().equals(getDealerId())){
                        gameClient.initNextPhase();
                    }
                }
                break;
            case Raise:
                gameClient.getCurrentRoundState().calcPlayerRaise(action.getSenderId(), action.getAmount());
                if(isMyTurn(action.getSenderId())) { // check if its now the players turn
                    System.out.println("Make a Move");
                }
            break;

            case Check:
                if(!isLastPlayer(action.getSenderId())){
                    if(isMyTurn(action.getSenderId())) { // check if its now the players turn
                        System.out.println("Make a Move");
                    }
                } else if(isLastPlayer(action.getSenderId())){
                    System.out.println("Ready for Flop");
                    if(getOwnId().equals(getDealerId())){
                        gameClient.initNextPhase();
                    }
                }
                
                break;
            case Call:
                
                gameClient.getCurrentRoundState().calcPlayerCall(action.getSenderId(), action.getAmount());
                if(!isLastPlayer(action.getSenderId())){
                    if(isMyTurn(action.getSenderId())) { // check if its now the players turn
                        System.out.println("Make a Move");
                    }
                } else if(isLastPlayer(action.getSenderId())){
                    System.out.println("Ready for Flop");
                    if(getOwnId().equals(getDealerId())){
                        gameClient.initNextPhase();
                    }
                }
                break;
            default:
                break;
        }
    }

    public void connectionStatusCommand(String jsonObject, RoundState roundState) throws InterruptedException {
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

                if(getOwnId().equals(getMPId())){ break; }
                connectionStatusResponse = new ConnectionStatus(
                        getOwnId(),
                        ConnectionStatusType.ConnectionsEstablished
                );
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
                    TimeUnit.SECONDS.sleep(10);
                    gameClient.startNewRound();
                }
                break;
            default:
                System.err.println("connectionStatus type unknown. Received: " + connectionStatus.getConnectionStatus());

        }
    } 


    public void gamePhaseCommand(String jsonObject, RoundState roundState){
        GamePhase gamePhase = GamePhase.fromJson(jsonObject);
        switch(gamePhase.getGamePhase()){
            case PreFlop:
                setHoleCards(gamePhase.getCards());
                printToScreen("PreFlop");
                calcBlindBets();
                if(isFirstPlayer()) {
                    System.out.println("Make a bet");
                }
                break;
            case Flop:
                addCardsToCommunityCards(gamePhase.getCards());
                printToScreen("Flop");
                if(isFirstPlayer()) {
                    System.out.println("Make a bet");
                }
                break;
            case Turn:
                addCardsToCommunityCards(gamePhase.getCards());
                printToScreen("Turn");
                if(isFirstPlayer()) {
                    System.out.println("Make a bet");
                }
                break;
            case River:
                addCardsToCommunityCards(gamePhase.getCards());
                printToScreen("River");
                if(isFirstPlayer()) {
                    System.out.println("Make a bet");
                }
                break;
            default:
                break;
                
        }
    }

    public boolean isLastPlayer(String previusePeerId){
        String nextPlayerId = gameClient.getCurrentRoundState().
        nextPlayer(previusePeerId);
        String lastPlayer;
        //String lastRaiseId = gameClient.getCurrentRoundState().getLastRaise();
        if(gameClient.getCurrentRoundState().getLastRaise() != null){
            lastPlayer = gameClient.getCurrentRoundState().getLastRaise();
        }else{
            lastPlayer = gameClient.getCurrentRoundState().getFirstPlayerId();
        }
        return nextPlayerId.equals(lastPlayer);
    }

    
    public boolean isMyTurn(String previusePeerId) {
        return getOwnId().equals(gameClient.getCurrentRoundState().
        nextPlayer(previusePeerId));
    }

    public boolean isFirstPlayer(){
        return gameClient.getCurrentRoundState().getFirstPlayerId().equals(getOwnId());
    }

    public void roundStatusCommand(String jsonObject, RoundState roundState){
        RoundStatus roundStatus = RoundStatus.fromJson(jsonObject);
        //RoundStatus roundStatusResponse;
        switch (roundStatus.getRoundStatus()) {
            case NewRoundStarted:
                createNewRoundState();
                break;

            case RoundEnded: 

                break;
    
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

    public void printToScreen(String GamePhase){
        clearScreen();
        System.out.println(GamePhase);
        System.out.println("First player is " + gameClient.getCurrentRoundState().getFirstPlayerId());
        System.out.println("Smallblind: " + gameClient.getCurrentRoundState().getSmallBlind() + " | Bigblind: "  +gameClient.getCurrentRoundState().getBigBlind());
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

