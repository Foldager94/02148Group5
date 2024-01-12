package dk.dtu.game.round;

import dk.dtu.game.Player;

import java.util.ArrayList;
import java.util.List;
import dk.dtu.game.Card;
import static dk.dtu.game.GameSettings.*;

public class RoundState {
    private int smallBlindPrice = START_SMALL_BLIND;
    private int bigBlindPrice = START_BIG_BLIND;
    private int roundId;
    private String peerId;
    private String smallBlind; // id
    private String bigBlind; // id
    private String dealer; // id
    private int pot;
    private List<Player> players;
    private List<Card> communityCards;

    public RoundState(int roundId, String peerId, List<Player> players, String smallBlind, String bigBlind, String dealer){
        this.roundId = roundId;
        this.peerId = peerId;
        this.players = players;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.dealer = dealer;        
        this.pot = 0;
        communityCards = new ArrayList<>();
    }
    public List<Player> getPlayers(){
        return players;
    }

    public int getroundId() {
        return roundId;
    }

    public String getSmallBlind() {
        return smallBlind;
    }
    
    public String getBigBlind() {
        return bigBlind;
    }

    public String getDealer() {
        return dealer;
    }

    public Player getPlayer(String id) {
        for (Player player : players) {
        if (player.getId().equals(id)) {
            return player; 
        }
            }
            return null;
    }

    // Calculates small blind and big blind
    public void calculateBlinds() {
        if (roundId % BLIND_LENGTH == 0) {
            smallBlindPrice *= 2;
            bigBlindPrice *= 2;
        }
        
    }
    
    public Player getOwnPlayerObject() {
        return getPlayer(peerId);
    }

    public void calculateBlindsBet() {
        calculateBlinds();
        Player SB = getPlayer(smallBlind);
        Player BB = getPlayer(bigBlind);
        pot = smallBlindPrice + bigBlindPrice;
        SB.removeFromBalance(smallBlindPrice);
        BB.removeFromBalance(bigBlindPrice);       
    }

    @Override
    public String toString() {
        return "Round ID: " + roundId + "\nPeer ID: " + peerId + "\nSmall Blind: " + smallBlind
                + "\nBig Blind: " + bigBlind + "\nDealer: " + dealer + "\nPlayers: " + players
                + "\nCommunity Cards: " + ((communityCards.isEmpty() ? "None" : communityCards) +  "\nPot: "+ String.valueOf(pot));
    }
  
}