package dk.dtu.game.commands;
import java.util.List;

import dk.dtu.game.commands.enums.RoundStatusType;


public class RoundStatus extends Command {
    RoundStatusType roundStatus;
    List<String> winners;

    public RoundStatus(String senderId, RoundStatusType roundStatus){
        this.senderId = senderId;
        this.roundStatus = roundStatus;
    }
    
    public RoundStatus(String senderId, RoundStatusType roundStatus, List<String> winners){
        this.senderId = senderId;
        this.roundStatus = roundStatus;
        this.winners = winners;
    }

    public RoundStatusType getRoundStatus() {

        return roundStatus;
    }
    public List<String> getWinners(){
        return winners;
    }


    public static RoundStatus fromJson(String json) {
        return Command.fromJson(json, RoundStatus.class);
    }
    
}

