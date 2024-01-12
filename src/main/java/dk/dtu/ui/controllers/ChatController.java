package dk.dtu.ui.controllers;

import java.awt.TextField;
import java.util.List;

import dk.dtu.chat.ChatClient;
import dk.dtu.network.Peer;
import dk.dtu.ui.components.Chat;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ChatController extends ChatClient {
    private String name;
    private int index = 0;
    private Chat chat;


    public ChatController(Peer peer) {
        super(peer);
        this.name = peer.name;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public void sendChat(String sender, String senderId, String message) {
        sendGlobalMessage(message, peer.getPeerIds());
        addMessage(sender, senderId, message);
    }

    public void addMessage(String senderName, String senderId, String message) {
        Platform.runLater(() -> {
            if (isNotEmpty(message)) {
                Label label = new Label("> " + senderName + ": "  + message);
                label.getStyleClass().add("chat-message");
                chat.messages.add(label);
                chat.chatBox.getChildren().add(chat.messages.get(index));
                index++;
            }
        });
    }

    private boolean isNotEmpty(String message) {
        return !message.trim().isEmpty();
    }

    @Override
    public void showChat(String privateOrPublic, String senderName, String senderId, String message) {
        addMessage(senderName, senderId, message);
    }

    public String getName() {
        return name;
    }
    
}
