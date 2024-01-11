package dk.dtu.game.round;


import dk.dtu.game.Card;

import dk.dtu.game.Deck;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

public class RoundLogic {
    public static Deck deck = new Deck();
    public void syncRoundState(){

    }
    
   // public void preFlop() {

   // }
    public void bettingRound() {
// 1. PREFLOP
// 2. FLOP.
// 3. TURN
// 4. RIVER

    }
    public void preFlop() {
        Deck deck = new Deck();
        List<Card> giveHoleCards = deck.draw(2);
    }

    public static  void flop(){
        List <Card> communityFlop = deck.draw(3);
        System.out.println(communityFlop);
    }
    public void turn(){
   
        List <Card> communityTurn= deck.draw(1);
    }
    public void river(){
        List <Card> communityTurn= deck.draw(1);
    }

    public List<Card> dealCard(int count){
        //TODO: Draw count numbers of cards from deck
        throw new NotImplementedException("Needs implementation");
    }

    public static void main(String [] args) throws IOException {
        flop();

    }
}