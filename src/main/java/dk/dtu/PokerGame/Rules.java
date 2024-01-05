package dk.dtu.PokerGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Rules {
    public int determineHand(List<Card> holeCards, List<Card> communityCards){
        List<Card> cards = new ArrayList<>(communityCards);
        cards.addAll(holeCards);
        Collections.sort(cards);
        if(isRoyalFlush(cards)){
            return 9;
        } else if (isStraight(cards)) {
            return 8;
        } else if (isFourOfAKind(cards)) {
            return 7;
        } else if (isFullHouse(cards)) {
            return 6;
        } else if (isFlush(cards)) {
            return 5;
        } else if (isStraight(cards)) {
            return 4;
        } else if (isThreeOfAKind(cards)) {
            return 3;
        } else if (isTwoPair(cards)) {
            return 2;
        } else if (isOnePair(cards)) {      
            return 1;
        } else {                            
            return 0;
        }
    }
    public boolean isRoyalFlush(List<Card> cards) {
        for (Suit suit : Suit.values()) {
            List<Card> royalFlush = new ArrayList<Card>();
            for (int val = 10; val < 15; val++) {
                royalFlush.add(new Card(val, suit));
            }
            if (cards.containsAll(royalFlush)) {
                return true;
            }

        }
        return false;
    }
    public boolean isStraightFlush(List<Card> cards) {
        for(Suit suit : Suit.values()){
            List<Card> suitCards = cards.stream().filter(card -> card.getSuit() == suit).collect(Collectors.toList());
            if(suitCards.size() <5){
                continue;
            }
            
            Collections.sort(suitCards);
        
            int count = 1; 
            for (int i = 1; i < suitCards.size(); i++) {
                if (suitCards.get(i).getValue() == suitCards.get(i - 1).getValue() + 1) {
                    count++;
                } else if (suitCards.get(i).getValue() == 14 && suitCards.get(0).getValue() == 2) {
                    count++;
                } 

            }
            if (count >= 5) {
                return true;
            }
        }
        return false;
    }
    public boolean isFourOfAKind(List<Card> cards) {
        int count;
        for (int val = 2 ; val < 15; val++) {
            count = 0;
            for (Card card : cards) {
                if (card.getValue() == val) { count++; }
                if (count == 4) { return true; }
            }
        }
        return false;
    }
    public boolean isFullHouse(List<Card> cards) {
        int count;
        for (int val = 2 ; val < 15; val++) {
            count = 0;
            for (Card card : cards) {
                if (card.getValue() == val) { count++; }
                if (count == 3) { 
                    int threeVal = val;
                    for (int i = 0; i < 6; i++) {
                        int pairVal = cards.get(i).getValue();
                        if (threeVal != pairVal && (pairVal == cards.get(i + 1).getValue())) {
                            return true;
                        }
                    }                    
                } 
            }
        }
        return false;
    }
    
    public boolean isFlush(List<Card> cards) {
        int count;
        for (Suit suit : Suit.values()) {
            count = 0;
            for (Card card : cards) {
                if (card.getSuit() == suit) { count++; }
                if (count == 5) {return true; } 
            }
        } 
        return false;
    }
    
    public boolean isStraight(List<Card> cards) {
        for (int i = 0; i < 3; i++) { // for the first 3 cards, check the 4 next for a straight
            int prevVal = cards.get(i).getValue(); // get val of first card
            for (int j = i + 1; j < i + 3; j++) { // for next 3 cards, check if they are one higher than the last
                if (cards.get(j).getValue() == prevVal + 1) {
                    prevVal++;
                } else {
                    break;
                }
            }
            if (cards.get(i + 4).getValue() == prevVal + 1) { // check the last card
                return true;
            }
        }
        return false;
    }
    
    public boolean isThreeOfAKind(List<Card> cards) {
        int count;
        for (int val = 2 ; val < 15; val++) {
            count = 0;
            for (Card card : cards) {
                if (card.getValue() == val) { count++; }
                if (count == 3) { return true; }
            }
        }
        return false;
    }

    public boolean isTwoPair(List<Card> cards) {
        for (int i = 0; i < 6; i++) {
            if (cards.get(i).getValue() == cards.get(i + 1).getValue()) {
                for (int j = i + 1; j < 6; j++) {
                    if (cards.get(j).getValue() == cards.get(j + 1).getValue()) {
                        return true;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean isOnePair(List<Card> cards) {
        for (int i = 0; i < 6; i++) { // Check the 7 cards that a player can make combinations with.
            if (cards.get(i).getValue() == cards.get(i + 1).getValue()) { // Check if card has same val as next card (pair)
                return true;
            }
        }
        return false;
    }
}


     // int countD; 
            // countD = 0;
            // for (Card card : cards) {
            //     if (card.getSuit() == Suit.Diamonds) { countD++; }
            //     if (countD == 5) { return true;}
            // }
            // int countH; 
            // countH = 0;
            // for (Card card : cards) {
            //     if (card.getSuit() == Suit.Hearts) { countH++; }
            //     if (countH == 5) { return true;}
            // }
            // int countC;
            // countC = 0;
            // for (Card card : cards) {
            //     if (card.getSuit() == Suit.Clubs) { countC++; }
            //     if (countC == 5) { return true;}
            // }
            // int countS;
            // countS = 0;
            // for (Card card : cards) {
            //     if (card.getSuit() == Suit.Spades) { countS++; }
            //     if (countS == 5) { return true;}
            // }
