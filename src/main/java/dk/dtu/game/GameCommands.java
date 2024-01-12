package dk.dtu.game;
import java.util.List;

import org.jspace.Tuple;

import com.google.gson.Gson;

import dk.dtu.game.commands.SendHoleCards;
import dk.dtu.game.commands.Action;

import dk.dtu.game.commands.enums.ConnectionStatusType;
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

    public void commandHandler(Tuple messageTuple, RoundState roundState) {
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


    public void actionCommand(String jsonObject, RoundState roundState){
        Action action = Action.fromJson(jsonObject);
    }

    public void connectionStatusCommand(String jsonObject, RoundState roundState){
    
        ConnectionStatus connectionStatus = ConnectionStatus.fromJson(jsonObject);
        ConnectionStatus connectionStatusResponse;
        switch(connectionStatus.getConnectionStatus()){
            case Ping:
                connectionStatusResponse = new ConnectionStatus(gameClient.peer.id, ConnectionStatusType.Pong);
                gameClient.sendCommand(connectionStatus.getSenderId(), "ConnectionStatus", connectionStatusResponse.toJson());
                break;
            case Pong:
                gameClient.addPlayerToGameState(connectionStatus.getSenderId());
                if(gameClient.connectionEstablishedToAll()){
                    connectionStatusResponse = new ConnectionStatus(gameClient.peer.id, ConnectionStatusType.ConnectionsEstablished);
                    if(gameClient.peer.id != gameClient.peer.MPID){
                        gameClient.sendCommand(gameClient.peer.MPID, "ConnectionStatus", connectionStatusResponse.toJson());
                    }
                }
                break;
            case ConnectionsEstablished:
                readyCount++;
                if(isEveryoneReady()){
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
                gameClient.gameState.currentRoundState.getOwnPlayerObject().setHoleCards(gamePhase.getCards());
                gameClient.gameState.currentRoundState.calculateBlindsBet();
                System.out.println(gameClient.gameState.currentRoundState.toString());
                break;
            case Flop:
                break;
            case Turn:
                break;
            case River:
                break;
            default:
                break;
                

        }
    }
    
    public void roundStatusCommand(String jsonObject, RoundState roundState){
        RoundStatus roundStatus = RoundStatus.fromJson(jsonObject);
        //RoundStatus roundStatusResponse;
        switch (roundStatus.getRoundStatus()) {
            case NewRoundStarted:
                gameClient.gameState.createNewRoundState(gameClient.peer.id);
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

