package dk.dtu.game;
import java.util.List;

import org.jspace.Tuple;

import com.google.gson.Gson;

import dk.dtu.game.round.RoundState;
import dk.dtu.game.commands.SendHoleCards;

public class GameCommands{
    GameClient gameClient;

    public GameCommands(GameClient gameClient){
        this.gameClient = gameClient;
    }

    public void commandHandler(Tuple messageTuple, RoundState roundState) {
        String command = messageTuple.getElementAt(String.class, 0);
        String jsonObject = messageTuple.getElementAt(String.class, 1);
        switch (command) {
            case "PreFlop": preFlopCommand(jsonObject, roundState); break;
            case "Flop": flopCommand(); break;
            case "Turn": turnCommand(); break;
            case "River": riverCommand(); break;
            case "Showdown": showdownCommand(); break;
            case "BettingRound": bettingRoundCommand(); break;
            case "SendHoleCards": sendHoleCardsCommand(jsonObject, roundState); break;
            case "NewRoundStarted": newRoundStartedCommand(); break;
            case "RoundEnded": roundEndedCommand(); break;
            case "Ping": pingCommand(); break;
            case "Pong": pongCommand(); break;
            case "RequestHoleCards": requestCardsCommand(); break;
            case "MessageRecived": messageReceivedCommand(); break;
            case "Fold": foldCommand(); break;
            case "Bet": betCommand(); break;
            case "Raise": raiseCommand(); break;
            case "Check": checkCommand(); break;
            case "Call": callCommand(); break;
            case "Broke": brokeCommand(); break;
            case "RoundStateUpdated": roundStateUpdatedCommand(); break;
            case "RoundStateSync": roundStateSyncCommand(); break;
            case "RoundStateSyncApproved": roundStateSyncApprovedCommand(); break;
            case "RoundStateSyncDisapproved": roundStateSyncDisapprovedCommand(); break;
            default: unknownCommand(); break;
        }
    }


    private void preFlopCommand(String jsonObject, RoundState roundState) {
        // Implementer PreFlop logik her
        String dealerId = roundState.getDealer();

        // request 2 cards
        gameClient.sendCommand(dealerId, "RequestHoleCards", "{'senderId':'"+gameClient.peer.id+"'}");
        // await 2 cards

        // send message that the 2 cards have been received
        // update roundState holeCards
        // calculate Blinds
        // Update roundState
        // calculate pot from small/big blind fee
        // Update roundState Pot
        // calculate small/big blind balance
        // Update round state players balance
        // Send message to dealer that roundState is updated and are ready to continue
    }
    private void sendHoleCardsCommand(String jsonObject, RoundState roundState) {
        SendHoleCards data = SendHoleCards.fromJson(jsonObject);
        roundState.getPlayer().getHoleCards().addAll(data.getHoleCards());
        // Implementer SendCards logik her
    }


    private void flopCommand() {
        // Implementer Flop logik her
    }

    private void turnCommand() {
        // Implementer Turn logik her
    }

    private void riverCommand() {
        // Implementer River logik her
    }

    private void showdownCommand() {
        // Implementer Showdown logik her
    }

    private void bettingRoundCommand() {
        // Implementer BettingRound logik her
    }


    private void newRoundStartedCommand() {
        // Implementer NewRoundStarted logik her
    }

    private void roundEndedCommand() {
        // Implementer RoundEnded logik her
    }

    private void pingCommand() {
        // Implementer Ping logik her
    }

    private void pongCommand() {
        // Implementer Pong logik her
    }

    private void requestCardsCommand() {
        // Implementer RequestCards logik her
    }

    private void messageReceivedCommand() {
        // Implementer MessageRecived logik her
    }

    private void foldCommand() {
        // Implementer Fold logik her
    }

    private void betCommand() {
        // Implementer Bet logik her
    }

    private void raiseCommand() {
        // Implementer Raise logik her
    }

    private void checkCommand() {
        // Implementer Check logik her
    }

    private void callCommand() {
        // Implementer Call logik her
    }

    private void brokeCommand() {
        // Implementer Broke logik her
    }

    private void roundStateUpdatedCommand() {
        // Implementer RoundStateUpdated logik her
    }

    private void roundStateSyncCommand() {
        // Implementer RoundStateSync logik her
    }

    private void roundStateSyncApprovedCommand() {
        // Implementer RoundStateSyncApproved logik her
    }

    private void roundStateSyncDisapprovedCommand() {
        // Implementer RoundStateSyncDisapproved logik her
    }

    private void unknownCommand() {
        System.err.println("GameClient: Command unknown");
    }
    

    
}