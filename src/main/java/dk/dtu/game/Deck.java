package dk.dtu.game;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Deck {

    private static List<Card> cards; // can be seen as a "stack" of cards
    private static int usedCards = 0; // keep track of how many of the cards have been used

    public Deck() {
        cards = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (int val = 2; val < 15; val++) {
                cards.add(new Card(val, suit));
            }
        }
        shuffle();
    }    

    public void shuffle() {
        usedCards = 0;
		Collections.shuffle(cards);
    }

    public List<Card> getCardsByIndex(int index) {
        return cards.subList(cards.size() - (2 * (index + 1)), cards.size() - (2 * index));
    }

    public List<Card> draw(int nCards) {
        List<Card> drawnCards = cards.subList(cards.size()-nCards-usedCards, cards.size()-usedCards);
        usedCards += nCards;
        return drawnCards;
    }

}



