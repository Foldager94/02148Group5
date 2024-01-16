package dk.dtu.game.round;


import dk.dtu.game.Card;

import dk.dtu.game.Deck;
import dk.dtu.game.commands.Action;

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
        List<Card> giveHoleCards = drawCards(2);
    }

    public static void flop(){
        List<Card> communityFlop = drawCards(3);
        System.out.println(communityFlop);
    }

    public void turn(){
        List<Card> communityTurn = drawCards(1);
    }

    public void river(){
        List<Card> communityTurn = drawCards(1);
    }

    public static  List<Card>  drawCards(int count){
           return deck.draw(count);
    }
    public static void main(String [] args) throws IOException {
        flop();

    }
    
    // public static void makeAction(String command, String amount){

    //   return new Action();
    // }

    // public static void gameCommandHandler(String command){
    //     String[] commandTag = command.split(" ");
    //     if(commandTag.length < 2){
    //         System.out.println("No command sent");
    //         return;
    //     }
    //     switch(commandTag[1]){
    //         case "Fold", "Check": 
    //             makeAction(commandTag[1],"0"); 
    //             break;
    //         case "Raise": 
    //             if(commandTag.length < 3){
    //                 System.out.println("Raise needs an amount");
    //                 return;
    //             }
    //             makeAction(commandTag[1], commandTag[2]);  
    //             break;
    //         case "Call":
    //             makeAction(commandTag[1],"0"); 
    //             break;
    //         default: System.out.println("Unknown game command: Use Fold, Raise, Call or Check");
    //     }
    // }
}