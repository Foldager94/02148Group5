package dk.dtu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import dk.dtu.game.round.RoundState;

// Keeps track of a list of RoundStates


public class GameState {
    int roundId = 0;
    List<Player> players;
    Deck deck = new Deck();
    List<RoundState> history;
    RoundState currentRoundState;
    public void createNewRoundState(String peerId) {
        updateRound();
        String dealer = getNewDealer();
        String smallBlind = getNewSmallBlind(dealer);
        String bigBlind = getNewBigBlind(smallBlind);
        String firstPlayer = getNewFirstPlayer(bigBlind);
        RoundState roundState = new RoundState(roundId, peerId, players, smallBlind, bigBlind, dealer, firstPlayer);
        currentRoundState = roundState;
    }

    public String getNewDealer(){
        if(currentRoundState == null){
            return "0";
        } else {
            String previuseDealer = currentRoundState.getDealer();
            int indexOfPreviuseDealer = findPlayerIndexById(previuseDealer);
            int nextIndex = indexOfPreviuseDealer+1;
            if(nextIndex < players.size()){
                return players.get(nextIndex).id;
            }
            return players.get(0).id;

        }
    }

    public String getNewSmallBlind(String dealer) {
        int dealerint= Integer.parseInt(dealer);
        int nextIndex = dealerint+1;
            if(nextIndex < players.size()){
                return players.get(nextIndex).id;
            }
            return players.get(0).id;
    }

    public String getNewBigBlind(String smallBlind) {
        int smallBlindint= Integer.parseInt(smallBlind);
        int nextIndex = smallBlindint+1;
        if(nextIndex < players.size()){
            return players.get(nextIndex).id;
        }
        return players.get(0).id;
    }
    
    public String getNewFirstPlayer(String bigBlind) {
        int bigBlindint = Integer.parseInt(bigBlind);
        int nextIndex = bigBlindint + 1;
        if(nextIndex < players.size()){
            return players.get(nextIndex).id;
        }
        return players.get(0).id;
    }


    public int findPlayerIndexById(String targetId) {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.getId().equals(targetId)) {
                return i; // Returner indekset, hvis ID matcher
            }
        }
        return -1; // Returner -1, hvis spilleren ikke blev fundet
    }
    
    public void updateRound()  {
        roundId += 1;
    }

    public void addRoundStateToHistory(){
        if (currentRoundState != null) {
            history.add(currentRoundState);
        }
    }

    public void updateGameState() {
        //TODO: update the gameState after round is finished
        updateRound();
        resetDeck();
        removeLosingPlayers();
    }
    
    public void removeLosingPlayers(){
        if (players != null) {
            players.removeIf(player -> player.getBalance() <= 0);         
        }
    }



    public void addPlayer(Player player) {
        if (players == null) {
            players = new ArrayList<Player>();
        }
        players.add(player);
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return p1.getId().compareTo(p2.getId());
            }
        });
    }

    public void resetDeck() {
        deck.shuffle();
    }

}

