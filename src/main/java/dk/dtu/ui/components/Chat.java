package dk.dtu.ui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import dk.dtu.chat.ChatClient;
import dk.dtu.network.Peer;
import dk.dtu.ui.controllers.ChatController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Chat {
    private ChatController chatController;
    public final VBox chatBox = new VBox(5); // 5 is the padding or smth like that
    public List<Label> messages = new ArrayList<Label>();
    private Pane container = new Pane();
    private ScrollPane chatContainer = new ScrollPane();
    private int index = 0;
    private TextField messageInput = new TextField();
    private final int WIDTH = 800;
    private Peer peer;
    
    public Chat(ChatController chatClient, Peer peer) {
        this.chatController = chatClient;
        this.peer = peer;
        container.setPrefSize(WIDTH, 300);;
        chatContainer.setPrefSize(WIDTH, 200);
        chatContainer.getStyleClass().add("chat-container");
        chatBox.setPrefSize(WIDTH - 12, 200 - 2);
        chatContainer.setContent(chatBox); 
        chatContainer.setPadding(new Insets(2, 2, 2, 2));
        chatContainer.vvalueProperty().bind(chatBox.heightProperty());
        chatBox.getStyleClass().add("chatbox");

		messageInput.setLayoutX(0);
		messageInput.setLayoutY(205);

		messageInput.setPromptText("Message");
		messageInput.getStyleClass().add("message-field");
		messageInput.setOnKeyPressed( event -> {
			if (event.getCode() == KeyCode.ENTER) {
				sendMessage();
			}
		});
		Button sendMessage = new Button("Send");
		sendMessage.setLayoutX(600);
		sendMessage.setLayoutY(205);
		sendMessage.getStyleClass().add("send-message");
        sendMessage.setOnAction(event -> {

		    sendMessage();
		});
        container.getChildren().addAll(chatContainer, messageInput, sendMessage);
        chatClient.setChat(this);
    }

    public void sendMessage() {
        chatController.sendChat(peer.name, peer.id, messageInput.getText());
        messageInput.setText("");
    }



    public Pane getView() {
        return container;
    }

}
