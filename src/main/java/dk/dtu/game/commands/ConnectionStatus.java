package dk.dtu.game.commands;

import dk.dtu.game.commands.enums.ConnectionStatusType;

public class ConnectionStatus extends Command {
    ConnectionStatusType connectionStatus;
    private String senderName;


    public ConnectionStatus(String senderdId, ConnectionStatusType connectionStatus, String sName){
        this.senderName = sName;
        this.senderId = senderdId;
        this.connectionStatus=connectionStatus;
    }

    public String getSenderName() {
        return senderName;
    }

    public ConnectionStatusType getConnectionStatus(){
        return connectionStatus;
    }
    
    public static ConnectionStatus fromJson(String json) {
        return Command.fromJson(json, ConnectionStatus.class);
    }
}
