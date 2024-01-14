package dk.dtu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.InitialContext;

public class Hand implements Comparable<Hand> {
    private List<Card> cards;
    private List<Integer> hand;

    public Hand(List<Card> communityCards, List<Card> holeCards)  {
        cards = List.copyOf(communityCards);
        cards.addAll(holeCards);
        Collections.sort(cards);
        hand = determineHand();
        
    }

    public List<Integer> determineHand() {
        List<Integer> handAttempt;
        handAttempt = isRoyalFlush(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isStraightFlush(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isFourOfAKind(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isRoyalFlush(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isRoyalFlush(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isRoyalFlush(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isRoyalFlush(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isRoyalFlush(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isRoyalFlush(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isRoyalFlush(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        return null;
    }

    public List<Integer> isRoyalFlush(List<Card> cards) {
        for (Suit suit : Suit.values()) {
            List<Card> royalFlush = new ArrayList<Card>();
            for (int val = 10; val < 15; val++) {
                royalFlush.add(new Card(val, suit));
            }
            if (cards.containsAll(royalFlush)) {
                return List.of(9);
            }

        }
        return null;
    }

    public List<Integer> isStraightFlush(List<Card> cards) {
        for(Suit suit : Suit.values()) { // get all cards of each suit
            List<Card> suitCards = cards.stream().filter(card -> card.getSuit() == suit).collect(Collectors.toList());
            if (suitCards.size() < 5){
                continue;
            }
            Collections.sort(suitCards);
            int count = 1; 
            int max = -1;
            for (int i = 1; i < suitCards.size(); i++) {
                int val = suitCards.get(i).getValue();
                if (val == suitCards.get(i - 1).getValue() + 1) {
                    count++;
                    max = val;
                } else if (val == 14 && suitCards.get(0).getValue() == 2) {
                    count++;
                    max = val;
                }
            }
            if (count >= 5) {
                return List.of(8, max);
            }
        }
        return null;
    }

    public List<Integer> isFourOfAKind(List<Card> cards) {
        int count;
        for (int val = 2 ; val < 15; val++) {
            count = 0;
            for (Card card : cards) {
                if (card.getValue() == val) { count++; }
            }
            if (count == 4) { return List.of(7, val); }
        }
        return null;
    }
    public List<Integer> isFullHouse(List<Card> cards) {
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
                            return List.of(6, threeVal, pairVal);
                        }
                    }                    
                } 
            }
        }
        return null;
    }
    
    public List<Integer> isFlush(List<Card> cards) {
        for(Suit suit : Suit.values()) { // get all cards of each suit
            List<Card> suitCards = cards.stream().filter(card -> card.getSuit() == suit).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            if (suitCards.size() < 5) {
                continue;
            }
            List<Integer> highestValues = suitCards.subList(0, 5).stream().map(Card::getValue).collect(Collectors.toList());

            List<Integer> hand = new ArrayList<>(List.of(5));
            hand.addAll(highestValues);
            
            return hand;
            }
        return null;
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

    public List<Integer> getHand() {
        return hand;
    }

    @Override
    public int compareTo(Hand otherCard) {
        if (this.hand.get(0) != otherCard.getHand().get(0)) {
            return Integer.compare(this.hand.get(0), otherCard.getHand().get(0));
        }
        for (int i = 1; i < this.hand.size(); i++) {
            if (this.hand.get(i) != otherCard.getHand().get(i)) {
                return Integer.compare(this.hand.get(i), otherCard.getHand().get(i));
            }
        }
        return 0;
    }
    
}
