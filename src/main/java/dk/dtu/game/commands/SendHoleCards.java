package dk.dtu.game.commands;

import java.util.List;
import com.google.gson.Gson;

import dk.dtu.game.Card;

public class SendHoleCards {
    private String senderId;
    private List<Card> holeCards;
    
    public SendHoleCards(String senderId, List<Card> holeCards){
        this.senderId = senderId;
        this.holeCards = holeCards;
    }

    public String getSenderId(){
        return senderId;
    }

    public List<Card> getHoleCards(){
        return holeCards;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static SendHoleCards fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, SendHoleCards.class);
    }
}