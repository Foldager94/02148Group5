package dk.dtu.game;

// card images from https://opengameart.org/content/playing-cards-vector-png

public class Card implements Comparable<Card> {
    private int value;
    private Suit suit;

    public Card(int value, Suit suit) {
        this.value = value;
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Suit getSuit(){
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    public String getImageUrl() {
        return String.valueOf(value) + "_of_" + suit + ".png";
    }
    
        
     public String toString() {
        String valueStr;
        switch (this.value) {
            case 2: valueStr = "Two";
                break;
            case 3: valueStr = "Three";
                break;
            case 4: valueStr = "Four";
                break;
            case 5: valueStr = "Five";
                break;
            case 6: valueStr = "Six";
                break;
            case 7: valueStr = "Seven";
                break;
            case 8: valueStr = "Eight";
                break;
            case 9: valueStr = "Nine";
                break;
            case 10: valueStr = "Ten";
                break;
            case 11: valueStr = "Jack";
                break;
            case 12: valueStr = "Queen";
                break;
            case 13: valueStr = "King";
                break;
            case 14: valueStr= "Ace";
                break;
            default: valueStr = String.valueOf(value);
                break;
        }
        return valueStr + " of " + suit;
    }

    @Override
    public int compareTo(Card otherCard) {
        return (Integer.compare(this.value, otherCard.value));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(getClass())) {
            Card temp = (Card) obj;
            if ((temp.value == this.value) && (temp.suit == this.suit)) {
                return true;
            }
        }
        return false;
    }
}
