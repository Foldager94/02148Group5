package dk.dtu.chat;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;
import org.jspace.Tuple;

import java.util.Arrays;
import java.util.List;

import java.io.IOException;

import dk.dtu.network.Peer;

public class ChatClient {
    private final String GLOBAL = "Global";
    private final String PRIVATE = "Private";
    
    public SequentialSpace chat;
    public SpaceRepository chats;
    public Peer peer;

    public ChatClient(Peer peer) {
        chat = new SequentialSpace();
        chats = new SpaceRepository();
        this.peer = peer;
    }

    public void startMessageReceiver() {
        new Thread(() -> {
            while (true) {
                try {
                    Tuple messageTuple = new Tuple(chat.get(
                        new FormalField(String.class), // sender id
                        new FormalField(String.class), // message
                        new FormalField(Boolean.class) // isAllChat
                    ));
                    String senderId = messageTuple.getElementAt(String.class, 0); // its id

                   if(!peer.isPeerKnown(senderId)){
                       sendMessage("IntroduceYourSelf", senderId, false);
                       continue;
                   }

                    if(peer.isPeerMuted(senderId)){
                        sendPeerIsMutedMsg(senderId);
                        continue;
                    }
                    
                    String message = messageTuple.getElementAt(String.class, 1);

                    if(message.equals("StartGame")){
                        System.out.println("\033[31mSystem: Game is starting.\033[0m");
                        peer.game.initGame();
                        continue;
                    }

                    if(message.equals("IntroduceYourSelf")){
                        peer.sendIntroductionMsg(senderId);
                        continue;
                    }

                    String senderName = peer.getPeerName(senderId);
                    String privateOrPublic = messageTuple.getElementAt(Boolean.class, 2) ? GLOBAL : PRIVATE;
                    showChat(privateOrPublic, senderName, senderId, message);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }).start();
    }

    public void showChat(String privateOrPublic, String senderName, String senderId, String message) {
        System.out.println(privateOrPublic + " "+ senderName + "#"+ senderId + ": " + message);
    }
    
    public void sendPeerIsMutedMsg(String peerId){
        try {
            String message = peer.name + "has muted you";
            chats.get(peerId).put(peer.id, message, false);
        } catch (InterruptedException e) {
            System.err.println("Could not send IsMutedMsg");
            throw new RuntimeException(e);
        }
    }

    
    public void sendMessage(String message, String ReceiverID, Boolean isAllChat) {
        try {
            if(peer.isPeerMuted(ReceiverID)){
                return;
            }
            chats.get(ReceiverID).put(peer.id, message, isAllChat);
        }
        catch(Exception e) {System.out.println(e.getMessage());}
    }

    public void sendGlobalMessage(String message, List<String> ids) {
        for (String ReceiverID : ids) {
            if(ReceiverID.equals(peer.id)){
                continue;
            }
            sendMessage(message, ReceiverID, true);
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
        } else if (commandTag[0].equals("/start")){
            if(peer.id.equals("0")){
                sendGlobalMessage("StartGame", peer.getPeerIds());
                peer.game.initGame();
                return;
            }
            System.out.println("System: You are not allowed to start the game");
        }
    }
}

    

