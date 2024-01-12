package dk.dtu.game.commands;
import dk.dtu.game.Card;
import java.util.List;
import dk.dtu.game.commands.enums.GamePhaseType;


public class GamePhase extends Command {
    GamePhaseType gamePhase;
    List<Card> cards;

    public GamePhase(String senderId, GamePhaseType gamePhase, List<Card> cards){
        this.senderId = senderId;
        this.gamePhase = gamePhase;
        this.cards = cards;
    }

    public GamePhaseType getGamePhase() {

        return gamePhase;
    }

    public List<Card> getCards(){
        return cards;
    }

    public static GamePhase fromJson(String json) {
        return Command.fromJson(json, GamePhase.class);
    }
    
}

