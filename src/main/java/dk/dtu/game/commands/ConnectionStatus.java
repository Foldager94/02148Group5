package dk.dtu.game.commands;

import dk.dtu.game.commands.enums.ConnectionStatusType;

public class ConnectionStatus extends Command {
    ConnectionStatusType connectionStatus;

    public ConnectionStatus(String senderdId, ConnectionStatusType connectionStatus){
        this.senderId = senderdId;
        this.connectionStatus=connectionStatus;
    }

    public ConnectionStatusType getConnectionStatus(){
        return connectionStatus;
    }
    
    public static ConnectionStatus fromJson(String json) {
        return Command.fromJson(json, ConnectionStatus.class);
    }
}
