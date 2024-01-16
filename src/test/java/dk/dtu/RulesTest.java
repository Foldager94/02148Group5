package dk.dtu;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import dk.dtu.game.Card;
import dk.dtu.game.Hand;
import dk.dtu.game.Suit;
import java.util.Collections;

public class RulesTest {    
    @Test
    public void testIsRoyalStraightFlush() {
        List<Card> cardList  = new ArrayList<Card>();
        cardList.add(new Card(10, Suit.Hearts));
        cardList.add(new Card(11, Suit.Hearts));
        cardList.add(new Card(12, Suit.Hearts));
        cardList.add(new Card(13, Suit.Hearts));
        cardList.add(new Card(14, Suit.Hearts));
        cardList.add(new Card(6, Suit.Clubs));
        cardList.add(new Card(2, Suit.Clubs));
        List<Integer> hand = new Hand(cardList).getHand();
        System.out.println(hand.toString());
        assertTrue(9 == hand.get(0) && hand.size() == 1);
    }
    @Test
    public void testIsNotRoyalStraightFlush() {
        List<Card> cardList  = new ArrayList<Card>();
        cardList.add(new Card(10, Suit.Hearts));
        cardList.add(new Card(11, Suit.Diamonds));
        cardList.add(new Card(12, Suit.Hearts));
        cardList.add(new Card(13, Suit.Hearts));
        cardList.add(new Card(14, Suit.Hearts));
        cardList.add(new Card(4, Suit.Spades));
        cardList.add(new Card(6, Suit.Clubs));
        List<Integer> hand = new Hand(cardList).getHand();
        System.out.println(hand.toString());
        assertTrue(9 != hand.get(0));

    }
    @Test
    public void testIsStraightFlush() {
        List<Card> cardList  = new ArrayList<Card>();
        cardList.add(new Card(6, Suit.Hearts));
        cardList.add(new Card(7, Suit.Hearts));
        cardList.add(new Card(8, Suit.Hearts));
        cardList.add(new Card(9, Suit.Hearts));
        cardList.add(new Card(10, Suit.Hearts));
        cardList.add(new Card(4, Suit.Spades));
        cardList.add(new Card(6, Suit.Clubs));
        List<Integer> hand = new Hand(cardList).getHand();
        System.out.println(hand.toString());
        assertTrue(9 == hand.get(0));
    }
    // }
    // @Test
    // public void testIsNotStraightFlush() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(6, Suit.Hearts));
    //     cardList.add(new Card(7, Suit.Clubs));
    //     cardList.add(new Card(8, Suit.Hearts));
    //     cardList.add(new Card(9, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Hearts));
    //     cardList.add(new Card(4, Suit.Spades));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isStraightFlush(cardList), false);


    // }
    // @Test
    // public void testIsFourOfAKind() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(4, Suit.Hearts));
    //     cardList.add(new Card(4, Suit.Clubs));
    //     cardList.add(new Card(4, Suit.Diamonds));
    //     cardList.add(new Card(4, Suit.Spades));
    //     cardList.add(new Card(9, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Hearts));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isFourOfAKind(cardList), true);

    // }
    // @Test
    // public void testIsNotFourOfAKind() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(4, Suit.Hearts));
    //     cardList.add(new Card(4, Suit.Clubs));
    //     cardList.add(new Card(3, Suit.Diamonds));
    //     cardList.add(new Card(4, Suit.Spades));
    //     cardList.add(new Card(9, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Hearts));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isFourOfAKind(cardList), false);
    // }

    // @Test
    // public void testIsFullHouse() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(13, Suit.Hearts));
    //     cardList.add(new Card(13, Suit.Clubs));
    //     cardList.add(new Card(13, Suit.Diamonds));
    //     cardList.add(new Card(5, Suit.Spades));
    //     cardList.add(new Card(5, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Hearts));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isFullHouse(cardList), true);
    // }
    
    // @Test
    // public void testIsNotFullHouse() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(4, Suit.Hearts));
    //     cardList.add(new Card(4, Suit.Clubs));
    //     cardList.add(new Card(3, Suit.Diamonds));
    //     cardList.add(new Card(4, Suit.Spades));
    //     cardList.add(new Card(9, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Hearts));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isFullHouse(cardList), false);
    // }

    // @Test
    // public void testIsFlush() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(2, Suit.Hearts));
    //     cardList.add(new Card(4, Suit.Hearts));
    //     cardList.add(new Card(6, Suit.Hearts));
    //     cardList.add(new Card(8, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Spades));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isFlush(cardList), true);
    // }

    // @Test
    // public void testIsNotFlush() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(4, Suit.Hearts));
    //     cardList.add(new Card(4, Suit.Clubs));
    //     cardList.add(new Card(3, Suit.Diamonds));
    //     cardList.add(new Card(4, Suit.Spades));
    //     cardList.add(new Card(9, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Hearts));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isFlush(cardList), false);
    // }


    // @Test
    // public void testIsStraightWheel() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(2, Suit.Hearts));
    //     cardList.add(new Card(3, Suit.Clubs));
    //     cardList.add(new Card(4, Suit.Spades));
    //     cardList.add(new Card(5, Suit.Diamonds));
    //     cardList.add(new Card(14, Suit.Hearts));
    //     cardList.add(new Card(4, Suit.Spades));
    //     cardList.add(new Card(7, Suit.Clubs));
    //     // 2 3 4 5 6
    //     Collections.sort(cardList);
    //     assertEquals(rule.isStraight(cardList), true);
    // }

    // @Test
    // public void testIsStraight() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(2, Suit.Hearts));
    //     cardList.add(new Card(3, Suit.Clubs));
    //     cardList.add(new Card(4, Suit.Spades));
    //     cardList.add(new Card(5, Suit.Diamonds));
    //     cardList.add(new Card(6, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Spades));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isStraight(cardList), true);
    // }

    // @Test
    // public void testIsNotStraight() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(4, Suit.Hearts));
    //     cardList.add(new Card(4, Suit.Clubs));
    //     cardList.add(new Card(3, Suit.Diamonds));
    //     cardList.add(new Card(4, Suit.Spades));
    //     cardList.add(new Card(9, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Hearts));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isStraight(cardList), false);
    // }
    
    // @Test
    // public void testIsThreeOfAKind() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(3, Suit.Hearts));
    //     cardList.add(new Card(3, Suit.Clubs));
    //     cardList.add(new Card(3, Suit.Spades));
    //     cardList.add(new Card(5, Suit.Diamonds));
    //     cardList.add(new Card(6, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Spades));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isThreeOfAKind(cardList), true);
    // }
    
    // @Test
    // public void testIsNotThreeOfAKind() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(4, Suit.Hearts));
    //     cardList.add(new Card(4, Suit.Clubs));
    //     cardList.add(new Card(3, Suit.Diamonds));
    //     cardList.add(new Card(2, Suit.Spades));
    //     cardList.add(new Card(9, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Hearts));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);    
    //     assertEquals(rule.isThreeOfAKind(cardList), false);
    // }

    // @Test
    // public void testIsTwoPair() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(4, Suit.Hearts));
    //     cardList.add(new Card(4, Suit.Hearts));
    //     cardList.add(new Card(3, Suit.Diamonds));
    //     cardList.add(new Card(4, Suit.Spades));
    //     cardList.add(new Card(9, Suit.Hearts));
    //     cardList.add(new Card(3, Suit.Spades));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isTwoPair(cardList), true);
    // }

    // @Test
    // public void testIsNotTwoPair() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(4, Suit.Hearts));
    //     cardList.add(new Card(7, Suit.Clubs));
    //     cardList.add(new Card(3, Suit.Diamonds));
    //     cardList.add(new Card(2, Suit.Spades));
    //     cardList.add(new Card(9, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Hearts));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isTwoPair(cardList), false);

    // }
    // @Test
    // public void testIsOnePair() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(4, Suit.Hearts));
    //     cardList.add(new Card(4, Suit.Clubs));
    //     cardList.add(new Card(3, Suit.Diamonds));
    //     cardList.add(new Card(2, Suit.Spades));
    //     cardList.add(new Card(9, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Hearts));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isOnePair(cardList), true);

    // }
    // @Test
    // public void testIsNotOnePair() {
    //     List<Card> cardList  = new ArrayList<Card>();
    //     cardList.add(new Card(2, Suit.Hearts));
    //     cardList.add(new Card(3, Suit.Clubs));
    //     cardList.add(new Card(4, Suit.Diamonds));
    //     cardList.add(new Card(5, Suit.Spades));
    //     cardList.add(new Card(6, Suit.Clubs));
    //     cardList.add(new Card(9, Suit.Hearts));
    //     cardList.add(new Card(10, Suit.Hearts));
    //     Collections.sort(cardList);
    //     assertEquals(rule.isOnePair(cardList), false);
    // }
}