package dk.dtu.PokerGame;

import java.util.List;
import java.util.ArrayList;


public class Board {
    int round;
    Player[] players;
    int pot;
    List<Card> communityCards;
    Player dealer;
    Player smallBlind;
    Player bigBlind;
    Deck deck;
    GameState state;
    public Board(int round, Player[] players) {
        this.round = round;
        this.players = players;
        this.pot = 0;
        this.state = GameState.PreFlop;
        this.communityCards = new ArrayList<Card>();
        this.deck = new Deck();
    }    
    public void setDealerAndBlinds(int round){

        if (round % 3 == 0) {
            this.players[0] = smallBlind;
            this.players[1] = bigBlind;
            this.players[2] = dealer;
        }

        else if (round % 3 == 1) {
            this.players[2] = smallBlind;
            this.players[1] = bigBlind;
            this.players[0] = dealer;
        }
         
       else if (round % 3 == 2) {
            this.players[1] = smallBlind;
            this.players[2] = bigBlind;
            this.players[0] = dealer;
        }
    }
    public void dealCards(){
        for(Player p : players) {
            p.setHoleCards(deck.draw(2));
        }

    }
    
    public void addCommunityCards() {
        switch (state) {
            case PreFlop:
                communityCards.addAll(deck.draw(3));
                break;
            case Flop:
                communityCards.addAll(deck.draw(1));
                break;
            case Turn:
                communityCards.addAll(deck.draw(1));
                break;
            default:
                break;
        }
    }
    public void setPotValue(int value) {
        this.pot=value;

    }

    public void increasePot(int value) {
        this.pot += value;
    }

    public int getPotValue() {
        return this.pot;
    }

    public int resetPotValue() {
        this.pot=0;
        return this.pot;
    }

   

}