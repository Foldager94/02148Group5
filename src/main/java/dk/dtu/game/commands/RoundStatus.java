package dk.dtu.game.commands;
import dk.dtu.game.commands.enums.RoundStatusType;


public class RoundStatus extends Command {
    RoundStatusType roundStatus;

    public RoundStatus(String senderId, RoundStatusType roundStatus){
        this.senderId = senderId;
        this.roundStatus = roundStatus;
    }


    public RoundStatusType getRoundStatus() {

        return roundStatus;
    }


    public static RoundStatus fromJson(String json) {
        return Command.fromJson(json, RoundStatus.class);
    }
    
}

