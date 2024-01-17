package dk.dtu.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;
import org.jspace.Tuple;
import dk.dtu.network.Peer;
import java.io.IOException;
public class ChatClient {
    private static final String SYSTEM_COLOR_CODE = "\033[31m";
    private static final String SYSTEM_RESET_CODE = "\033[0m";
    private static final String START_GAME = "StartGame";
    private static final String INTRODUCE_YOURSELF = "IntroduceYourSelf";
    private static final String GLOBAL_CHAT = "Global";
    private static final String PRIVATE_CHAT = "Private";

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
                    handleReceivedMessages();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }).start();
    }

    private void handleReceivedMessages() throws InterruptedException {
        Tuple messageTuple = new Tuple(chat.get(
                new FormalField(String.class), // sender id
                new FormalField(String.class), // message
                new FormalField(Boolean.class)  // isAllChat
        ));
        processMessage(messageTuple);
    }

    private void processMessage(Tuple messageTuple) throws InterruptedException {
        String senderId = messageTuple.getElementAt(String.class, 0);
        String message = messageTuple.getElementAt(String.class, 1);
        boolean isAllChat = messageTuple.getElementAt(Boolean.class, 2);

        if (!peer.isPeerKnown(senderId)) {
            sendMessage(INTRODUCE_YOURSELF, senderId, false);
            return;
        }

        if (peer.isPeerMuted(senderId)) {
            sendPeerIsMutedMsg(senderId);
            return;
        }

        if (START_GAME.equals(message)) {
            System.out.println(SYSTEM_COLOR_CODE + "System: Game is starting." + SYSTEM_RESET_CODE);
            peer.game.initGame();
            return;
        }

        if (INTRODUCE_YOURSELF.equals(message)) {
            peer.sendIntroductionMsg(senderId);
            return;
        }

        String senderName = peer.getPeerName(senderId);
        String privateOrPublic = isAllChat ? GLOBAL_CHAT : PRIVATE_CHAT;
        showChat(privateOrPublic, senderName, senderId, message);
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

    

