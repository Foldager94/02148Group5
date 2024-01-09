package dk.dtu.chat;

import java.util.Arrays;
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
                    String senderId = (String) messageTuple[0]; // its id
                    String senderName = peer.getPeerName(senderId);
                    String message = (String) messageTuple[1];
                    String privateOrPublic = (Boolean) messageTuple[2] ? "Global" : "Private";
                    System.out.println(privateOrPublic + " "+ senderName + "#"+ senderId + ": " + message);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
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
        catch(Exception e) {System.out.println(e.getMessage());}
    }

    public void sendGlobalMessage(String message, List<String> ids) {
        for (String recieverID : ids) {
            if(recieverID.equals(peer.id)){
                continue;
            }
            System.out.println(recieverID);
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
        String message;
        if (commandTag[0].equals("/p")) {
            message = String.join(" ", Arrays.copyOfRange(commandTag, 2, commandTag.length));
            sendMessage(message, commandTag[1], false);
        } else if (commandTag[0].equals("/c")) {
            message = String.join(" ", Arrays.copyOfRange(commandTag, 1, commandTag.length));
            sendGlobalMessage(message, peer.getPeerIds());
        }
    }
}