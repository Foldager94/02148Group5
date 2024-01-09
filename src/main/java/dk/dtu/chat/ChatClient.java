package dk.dtu.chat;

import java.util.List;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import dk.dtu.network.Peer;

import java.io.IOException;

public class ChatClient {
    public SequentialSpace chat;
    public SpaceRepository chats;
    public Peer peer;

    
    public ChatClient(Peer peer) {
        chat = new SequentialSpace();
        chats = new SpaceRepository();
        this.peer = peer;
    }

    public void startMessageReciever() { 
        new Thread(() -> {
            while (true) {
                try {
                    Object[] messageTuple = chat.get(
                        new FormalField(String.class), // sender id
                        new FormalField(String.class), // message
                        new FormalField(Boolean.class) // isAllChat
                    );
                    String sender = (String) messageTuple[0]; // its id
                    String message = (String) messageTuple[1];
                    System.out.println(sender + ": " + message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }).start();
    }
    
    public void sendMessage(String message, String recieverID, Boolean isAllChat) {
        try {
            chats.get(recieverID).put(peer.id, message, isAllChat);
        }
        catch(Exception e) {}
    }

    public void sendGlobalMessage(String message, List<String> ids) {
        for (String recieverID : ids) {
            sendMessage(message, recieverID, true);
        }
    }

    public void addChatToRepo(String peerId, String peerUri){
        try {
            chats.add(peerId, new RemoteSpace(peerUri + "/chat?keep"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public SequentialSpace getChat(){
        return chat;
    }

    public Space getPeerChat(String peerId){
        return chats.get(peerId);
    }

    public void chatHandler(String command) {
        String[] commandTag = command.split(" ");
        if (commandTag[0] == "/p") {
            sendMessage(commandTag[2], commandTag[1], false);
        } else if (commandTag[0] == "/c") {
            sendGlobalMessage(commandTag[1], peer.getPeerIds());
        }
    }
}