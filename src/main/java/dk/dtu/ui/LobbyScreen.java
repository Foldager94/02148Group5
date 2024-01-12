package dk.dtu.ui;

import java.awt.Point;
import java.io.File;

import dk.dtu.ui.components.Chat;
import dk.dtu.ui.components.HBoxCell;
import dk.dtu.ui.components.PlayersListView;
import dk.dtu.ui.util.ScreenSize;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class LobbyScreen {
	private ScreenSize screenSize;
	private Chat chat;
	private Pane chatContainer;
	private PlayersListView playersListView;
	private Boolean host;

	private static Pane root = new Pane();

	public LobbyScreen(ScreenSize screenSize, Boolean host) {
		this.screenSize = screenSize;
		this.host = host;
		initGraphics();
	}

	public void initGraphics() {
        Label header = new Label(host ? "Creating lobby" : "Joined lobby");
		header.setLayoutY(50);


		header.getStyleClass().add("header");
		ObservableList<String> names = FXCollections.observableArrayList("Alice", "Bob", "Chalie");
		playersListView =  new PlayersListView(names, 8, host);
		ListView<HBoxCell> listView = playersListView.getView();

		listView.setLayoutX(100);
		listView.setLayoutY(150);

		chat = new Chat("Alice");
		chatContainer = chat.getView();
		chatContainer.setLayoutX(100);
		chatContainer.setLayoutY(150 + playersListView.getHeight() + 10);

		root.getChildren().addAll(listView, header, chatContainer);
		root.getStyleClass().add("pane");

		Platform.runLater(() -> { // wait for css to apply, before centering
			header.setLayoutX(this.screenSize.getWidth() / 2 - header.getLayoutBounds().getWidth() / 2);
		});
	}

	public Parent getView() {
		return root;
	}
}
