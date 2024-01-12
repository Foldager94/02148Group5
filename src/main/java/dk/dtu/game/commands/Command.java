package dk.dtu.game.commands;

import com.google.gson.Gson;

public abstract class Command {
    String senderId;

    public String getSenderId(){
        return senderId;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static <T extends Command> T fromJson(String json, Class<T> cls) {
        Gson gson = new Gson();
        return gson.fromJson(json, cls);
    }
    
}