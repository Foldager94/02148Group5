package dk.dtu.game.commands;

import dk.dtu.game.commands.enums.ActionType;

public class Action extends Command {
    private ActionType action;
    private int amount;
    
    public Action (String senderId, ActionType action, int amount){
        this.senderId = senderId;
        this.action = action;
        this.amount = amount;
    }

    public ActionType getAction(){
        return action;
    }

    public int getAmount(){
        return amount;
    }

    public static Action fromJson(String json) {
        return Command.fromJson(json, Action.class);
    }
}