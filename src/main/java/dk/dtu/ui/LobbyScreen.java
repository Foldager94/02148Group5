package dk.dtu.ui;

import java.util.List;

import dk.dtu.network.Peer;
import dk.dtu.ui.components.Chat;
import dk.dtu.ui.components.HBoxCell;
import dk.dtu.ui.components.PlayersListView;
import dk.dtu.ui.controllers.ChatController;
import dk.dtu.ui.controllers.PeerController;
import dk.dtu.ui.util.ScreenSize;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

public class LobbyScreen {
	private ScreenSize screenSize;
	private Chat chat;
	private Pane chatContainer;
	private PlayersListView playersListView;
	private Peer peer;

	private static Pane root = new Pane();

	public LobbyScreen(ScreenSize screenSize, Peer peer, PlayersListView listView) {
		this.screenSize = screenSize;
		this.peer = peer;
		this.playersListView = listView;
		initGraphics(false);
	}

	public void initGraphics(Boolean host) {
        Label header = new Label(host ? "Creating lobby" : "Joined lobby");
		header.setLayoutY(50);

		header.getStyleClass().add("header");
		ListView<HBoxCell> listView = playersListView.getView();

		listView.setLayoutX(50);
		listView.setLayoutY(150);

		chat = new Chat(getChat(peer), peer);
		chatContainer = chat.getView();
		chatContainer.setLayoutX(50);
		chatContainer.setLayoutY(150 + playersListView.getHeight() + 10);

		root.getChildren().addAll(listView, header, chatContainer);
		root.getStyleClass().add("pane");

		Platform.runLater(() -> { // wait for css to apply, before centering
			header.setLayoutX(this.screenSize.getWidth() / 2 - header.getLayoutBounds().getWidth() / 2);
		});
	}

	public Peer getPeer() {
		return peer;
	}

	public ScreenSize getScreenSize() {
		return screenSize;
	}

	public ChatController getChat(Peer peer) {
		return (ChatController)(peer.chat);
	}

	public Pane getRoot() {
		return root;
	}


	public Parent getView() {
		return root;
	}
}
