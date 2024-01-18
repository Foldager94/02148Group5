package dk.dtu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.InitialContext;

import com.google.gson.Gson;

public class Hand implements Comparable<Hand> {
    private List<Card> cards;
    private List<Card> holeCards;
    private List<Integer> hand;
    private String id;

    public Hand(List<Card> allCards, String id) {
        this.id = id;
        Collections.sort(allCards);
        hand = determineHand(allCards);
    }

    public Hand(List<Card> communityCards, List<Card> holeCards, String id)  {
        this.holeCards = holeCards;
        this.id = id;
        cards = new ArrayList<>();
        cards.addAll(communityCards);
        cards.addAll(holeCards);
        Collections.sort(cards);
        hand = determineHand(cards);
    }

    public List<Card> getHoleCards() {
        return holeCards;
    }

    public List<Integer> determineHand(List<Card> cards) {
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
        handAttempt = isFullHouse(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isFlush(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isStraight(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isThreeOfAKind(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isTwoPair(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        handAttempt = isOnePair(cards);
        if (handAttempt != null) {
            return handAttempt;
        }
        return isHighCard(cards);
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
        for (int val = 14 ; val > 1; val--) {
            count = 0;
            for (Card card : cards) {
                if (card.getValue() == val) {
                    count++;
                }
                if (count == 3) { 
                    int threeVal = val;
                    for (int i = 6; i > 2; i--) {
                        int pairVal = cards.get(i).getValue();
                        if (threeVal != pairVal && (pairVal == cards.get(i - 1).getValue())) {
                            return List.of(6, threeVal, pairVal);
                        }
                    }                    
                } 
            }
        }
        return null;
    }
    
    public List<Integer> isFlush(List<Card> cards) {
        for (Suit suit : Suit.values()) { // get all cards of each suit
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
    
    public List<Integer> isStraight(List<Card> cards) {
        for (int i = 0; i < 3; i++) { // for the first 3 cards, check the 4 next for a straight
            Boolean checkForStraight = true;
            int prevVal = cards.get(i).getValue(); // get val of first card
            for (int j = i + 1; j < i + 4; j++) { // for next 3 cards, check if they are one higher than the last
                if (cards.get(j).getValue() == prevVal + 1) {
                    prevVal++;
                } else {
                    checkForStraight = false;
                    break;
                }
            }
            if (checkForStraight && (cards.get(i + 4).getValue() == prevVal + 1)) { // check the last card
                return List.of(4, cards.get(i + 4).getValue());
            }
        }
        return null;
    }
    public List<Integer> isThreeOfAKind(List<Card> cards) {
        int count;
        for (int val = 2 ; val < 15; val++) {
            count = 0;
            for (Card card : cards) {
                if (card.getValue() == val) count++;
            }
            if (count == 3) {
                final Integer valFin = val;
                List<Integer> hand = new ArrayList<>();
                hand.add(3);
                hand.add(val);
                List<Integer> highestCards = cards.stream().filter(card -> card.getValue() != valFin).sorted(Comparator.reverseOrder()).map(card -> card.getValue()).collect(Collectors.toList());
                hand.addAll(highestCards.subList(0, 2));
                return hand;
            }
        }
        return null;
    }
    
    public List<Integer> isTwoPair(List<Card> cards) {
        for (int i = 0; i < 6; i++) { // look for fist pair
            if (cards.get(i).getValue() == cards.get(i + 1).getValue()) {
                for (int j = i + 1; j < 6; j++) { // look for second pair
                    if (cards.get(j).getValue() == cards.get(j + 1).getValue()) {
                        final Integer valH1 = cards.get(j).getValue();
                        final Integer valH2 = cards.get(i).getValue();
                        List<Integer> hand = new ArrayList<>();
                        hand.add(2);
                        hand.add(valH1);
                        hand.add(valH2);
                        List<Integer> highestCards = cards.stream().filter(card -> (card.getValue() != valH1) && (card.getValue() != valH2)).sorted(Comparator.reverseOrder()).map(card -> card.getValue()).collect(Collectors.toCollection(ArrayList::new));
                        hand.add(highestCards.get(0));
                        return hand;                    
                    }
                }
            }
        }
        return null;
    }
    public List<Integer> isOnePair(List<Card> cards) {
        for (int i = 0; i < 6; i++) { // Check the 7 cards that a player can make combinations with.
            if (cards.get(i).getValue() == cards.get(i + 1).getValue()) { // Check if card has same val as next card (pair)
                final Integer valPair = cards.get(i).getValue();
                List<Integer> hand = new ArrayList<>();
                hand.add(1);
                hand.add(valPair);
                List<Integer> highestCards = cards.stream().filter(card -> card.getValue() != valPair).sorted(Comparator.reverseOrder()).map(card -> card.getValue()).collect(Collectors.toList());
                hand.addAll(highestCards.subList(0, 3)); // add 3 highest cards excluding the pair
                return hand;  
            }
        }
        return null;
    }
    public List<Integer> isHighCard(List<Card> cards) {
        List<Integer> hand = new ArrayList<>();
        hand.add(0);
        List<Integer> fiveHigh = cards.stream().sorted(Comparator.reverseOrder()).map(card -> card.getValue()).collect(Collectors.toList());
        hand.addAll(fiveHigh.subList(0, 5));
        return hand;
    }
    
    public String getId() {
        return id;
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

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Hand fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Hand.class);
    }
}
