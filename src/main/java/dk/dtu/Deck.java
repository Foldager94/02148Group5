package dk.dtu;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Deck {

    private static List<Card> cards; // can be seen as a "stack" of cards
    private static int usedCards = 0; // keep track of how many of the cards have been used

    public Deck() {
        cards = new ArrayList<Card>();
        for (Suit suit : Suit.values()) {
            for (int val = 1; val < 14; val++) {
                cards.add(new Card(val, suit));
            }
        }
        shuffle();
    }    

    public static void shuffle() {
        usedCards = 0;
		Collections.shuffle(cards);
    }

    public static List<Card> draw(int nCards) {
        List<Card> drawnCards = cards.subList(cards.size() - nCards - usedCards, cards.size() - usedCards);
        usedCards += nCards;
        return drawnCards;
    }

    public static int  getRemainingDeckSize () {
        return 52-usedCards;
    }

}
