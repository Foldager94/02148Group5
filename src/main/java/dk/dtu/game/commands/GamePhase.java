package dk.dtu.game.commands;
import dk.dtu.game.Card;
import java.util.List;
import java.util.Map;

import dk.dtu.game.commands.enums.GamePhaseType;
public class GamePhase extends Command {
    GamePhaseType gamePhase;
    List<Card> cards;
    public GamePhase(String senderId, GamePhaseType gamePhase, List<Card> cards){
        this.senderId = senderId;
        this.gamePhase = gamePhase;
        this.cards = cards;
    }

    private Map<String, List<Card>> totalHoleCards;
    private List<String> winningIds;
    public GamePhase(String senderId, GamePhaseType gamePhase, List<Card> cards, Map<String, List<Card>> totalHoleCards, List<String> winningIds){
        this.senderId = senderId;
        this.gamePhase = gamePhase;
        this.cards = cards;
        this.totalHoleCards = totalHoleCards;
        this.winningIds = winningIds;
    }

    public Map<String, List<Card>> getTotalHoleCards() {
        return totalHoleCards;
    }

    public List<String> getWinningIds() {
        return winningIds;
    }
 
    public GamePhaseType getGamePhase() {
        return gamePhase;
    }

    public List<Card> getCards() {
        return cards;
    }

    public static GamePhase fromJson(String json) {
        return Command.fromJson(json, GamePhase.class);
    }
}

