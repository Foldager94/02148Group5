package dk.dtu.game.round;

import dk.dtu.game.Player;
import java.util.List;
import dk.dtu.game.Card;

public class RoundState {
    private String roundId;
    private String peerId;
    private Player player;
    private String smallBlind;
    private String bigBlind;
    private String dealer;
    private List<Player> players;
    private List<Card> communityCards;

    public RoundState(String roundId, String peerId, List<Player> players, String smallBlind, String bigBlind, String dealer){
        this.roundId = roundId;
        this.peerId = peerId;
        this.players = players;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.dealer = dealer;
    }
    public List<Player> getPlayers(){
        return players;
    }

    public Player getPlayer(){
        return player;
    }

    public String getroundId() {
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



  





}