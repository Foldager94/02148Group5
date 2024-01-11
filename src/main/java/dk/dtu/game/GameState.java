package dk.dtu.game;

import java.util.List;
import dk.dtu.game.round.RoundState;
import dk.dtu.game.round.RoundLogic;


// Keeps track of a list of RoundStates

public class GameState {
    private final int BLIND_LENGTH = 5;
    int round = 0;

    Player player;
    List<Player> players;
    
    List<RoundState> history;
    RoundState currentRoundState;
    
    int smallBlind = 5;
    int bigBlind = 10;
    
    public void assignRoles() {
        if (players.size() >= 3) {
            // players.get(round % players.size()).assignDealer();
            // players.get((round - 1) % players.size()).assignSmallBLind();
            // players.get((round - 2) % players.size()).bigBLind();
        } else {
        }
    }
    
    // Calculates small blind and big blind
    public void calculateBlinds() {
        if (round % BLIND_LENGTH == 0) {
            smallBlind *= 2;
            bigBlind *= 2;
        }
    }
    
    public void updateRound()  {
        round += 1;
    }

    public void addRoundStateToHistory(){
        if (currentRoundState != null) {
            history.add(currentRoundState);
        }
    }
    
    public void initNewRoundState() {
        currentRoundState = new RoundState();
    }

    public void updatePlayerList() {
        players = currentRoundState.getPlayers();
    }

    
    public void updateGameState() {
        //TODO: update the gameState after round is finished
    }
}

