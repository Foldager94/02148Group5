package dk.dtu.PokerGame;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Player {
    String name;
    int balance;
    boolean inGame;
    boolean inRound;
    List<Card> holeCards = new ArrayList<Card>(2);
    public Player(String name, int balance, List<Card> holeCards) {
        this.name = name;
        this.balance = balance;
        this.holeCards = holeCards;
        this.inGame = true;
        this.inRound=true;
    }

    public String getName() {
        return this.name;
    }

    public void addToBalance(int amount) {
        this.balance += amount;
    }

    public void removeFromBalance(int amount) {
        this.balance -= amount;
    }

    public void setHoleCards(List<Card> holeCards) {
		this.holeCards = holeCards;
	}

    public List<Card> getHoleCards() {
        return this.holeCards;
    }
    public boolean isInGame() {
      return this.balance> 0 ? true: false;
    }

    public void fold()  {
        this.inRound=false;
    }

    // public boolean bet(int balance) {

    // }





}

