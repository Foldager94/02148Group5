package dk.dtu.game.commands;

import dk.dtu.game.commands.enums.SyncStateType;

public class SyncState extends Command {
    SyncStateType SyncState;

    public SyncState(String senderdId, SyncStateType SyncState){
        this.senderId = senderdId;
        this.SyncState=SyncState;
    }

    public SyncStateType getConnectionStatus(){
        return SyncState;
    }
    public static SyncState fromJson(String json) {
        return Command.fromJson(json, SyncState.class);
    }
}