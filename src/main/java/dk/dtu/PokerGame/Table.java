package dk.dtu.PokerGame;

public class Table {
    Player[] players;
    Board board;

    public void initNewBoard() {
        this.board = new Board(this.round, this.players);
    }
    
    public void newRound() {
        this.round+=1;
    }


}

