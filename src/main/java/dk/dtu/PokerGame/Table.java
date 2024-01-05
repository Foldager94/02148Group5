package dk.dtu.PokerGame;

public class Table {
    int round;
    Player[] players;
    Board board;

    public void initNewBoard() {
        this.board = new Board(this.round, this.players);
    }
    
}

