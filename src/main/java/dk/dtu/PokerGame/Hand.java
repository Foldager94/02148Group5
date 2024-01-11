package dk.dtu.PokerGame;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Hand {


    public Hand(List<Card> holeCards, List<Card> communityCards) {
        cards = new ArrayList<Card>();
        for (Suit suit : Suit.values()) {
            for (int val = 2; val < 15; val++) {
                cards.add(new Card(val, suit));
            }
        }
        shuffle();
    }    

    public static void shuffle() {
        usedCards = 0;
		Collections.shuffle(cards);
    }

    public List<Card> draw(int nCards) {
        List<Card> drawnCards = cards.subList(cards.size() - nCards - usedCards, cards.size() - usedCards);
        usedCards += nCards;
        return drawnCards;
    }

    public static int getRemainingDeckSize() {
        return 52 - usedCards;
    }

}
