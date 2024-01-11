package dk.dtu.game;

import java.util.List;
import java.util.ArrayList;

public class Player {
    String id;
    int balance;
    boolean inGame;
    boolean inRound;
    List<Card> holeCards = new ArrayList<Card>(2);

    public Player(String id, int balance) {
        this.id = id;
        this.balance = balance;
        this.inGame = true;
        this.inRound = true;
    }

    public String getName() {
        return this.id;
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
}
