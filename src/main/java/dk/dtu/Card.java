package dk.dtu;

public class Card {
    private int value;
    private Suit suit;

    public Card(int value, Suit suit) {
        this.value = value;
        this.suit = suit;
    }
    public int getValue(){
        return value;
    }
    public void setValue(int value){
        this.value = value;
    }
    public Suit getSuit(){
        return suit;
    }

    public void setSuit(Suit suit){
        this.suit = suit;
    }
    public String toString(){
        String valueStr;
        switch (value) {
           
            case 1:
                valueStr = "Ace";
                break;
            
            case 2: valueStr= "Two";
            break;

            case 3: valueStr= "Three";
            break;

            case 4: valueStr= "Four";
            break;

            case 5: valueStr= "Five";
            break;

            case 6: valueStr= "Six";
            break;

            case 7: valueStr= "Seven";
            break;

            case 8: valueStr= "Eight";
            break;

            case 9: valueStr= "Nine";
            break;

            case 10: valueStr= "ten";
            break;
    
            case 11:
                valueStr = "Jack";
                break;
            case 12:
                valueStr = "Queen";
                break;
            case 13:
                valueStr = "King";
                break;
            case 14:
                valueStr= "Ace";
            break;
            
            default:
                valueStr = String.valueOf(value);
        }
        
        return valueStr + " of " + suit;
    }
}

