package dk.dtu.ui;

import java.awt.Point;
import java.awt.TextField;
import java.io.File;

import dk.dtu.game.GameClient;
import dk.dtu.game.Player;
import dk.dtu.game.round.RoundState;
import dk.dtu.ui.components.Chat;
import dk.dtu.ui.components.CommunityCards;
import dk.dtu.ui.components.GamePlayer;
import dk.dtu.ui.components.GamePlayerButtons;
import dk.dtu.ui.controllers.ChatController;
import dk.dtu.ui.util.ScreenSize;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class GameScreen {
	private ScreenSize screenSize;

	private static Pane root = new Pane();
	private GameClient gameClient;
	private String peerId;
	private Label yourTurn = new Label("It is your turn");
	private boolean isYourTurn = false;
	private Pane gameButtons;
	private Label errorText;
	private int errors = 0;
	private Chat chat;

	public GameScreen(ScreenSize screenSize, GameClient gameClient) {
		this.screenSize = screenSize;
		this.gameClient = gameClient;
		gameButtons = (new GamePlayerButtons(gameClient, this)).getView();
		yourTurn.setLayoutX(400);
		yourTurn.setLayoutY(300);
		yourTurn.getStyleClass().add("small-text");
		gameButtons.setLayoutX(screenSize.getWidth() - 250);
		gameButtons.setLayoutY(screenSize.getHeight() - 200);
		errorText = new Label("");
        errorText.setLayoutY(screenSize.getHeight() - 220);
        errorText.getStyleClass().add("error");
	}

	public void makeGraphics() {
		if (chat == null) {
			chat = new Chat((ChatController)(gameClient.peer.chat), gameClient.peer, 300);
		}
		RoundState rs = gameClient.getCurrentRoundState();
		root.getChildren().clear(); // remove previos graphics
		root.getChildren().add(errorText);
		int i = 0;
		Player p = rs.getOwnPlayerObject();
		String nextId = rs.nextPlayer(p.getId());
		while (!nextId.equals(p.getId())) {
			GamePlayer gp = new GamePlayer(rs.getPlayer(nextId), gameClient, null, false, rs.getWinningIds(), rs.getTotalHoleCards());
			Pane view = gp.getView();
			view.setLayoutX(100 + i * 270);
			view.setLayoutY(20);
			i++;
			root.getChildren().add(view);
			nextId = rs.nextPlayer(nextId);
		}
		GamePlayer gpOwn = new GamePlayer(rs.getOwnPlayerObject(), gameClient, rs.getOwnPlayerObject().getHoleCards(), true, rs.getWinningIds(), null);
		Pane view = gpOwn.getView();
		view.setLayoutX(370);
		view.setLayoutY(screenSize.getHeight() - 200);
		root.getChildren().add(view);

		CommunityCards cc = new CommunityCards(rs.getCommunityCards());
		Pane ccView = cc.getView();
		ccView.setLayoutY(270);
		root.getChildren().add(cc.getView());
		root.getStyleClass().add("pane");

		Pane chatContainer = chat.getView();
		chatContainer.setLayoutX(10);
		chatContainer.setLayoutY(420);
		
		root.getChildren().add(chatContainer);
	}

	public void setIsYourTurn() {
		isYourTurn = true;
		Platform.runLater(() -> {
			root.getChildren().add(gameButtons);
		});
	}

    public void showError(String errorMsg) {
		System.out.println("Showing an error. Spooky!");
        errorText.setText(errorMsg);
        double centerX = screenSize.getWidth() / 2 - errorText.getBoundsInLocal().getWidth() / 2;
        errorText.setLayoutX(centerX);
        errors++;
        delay(3000, () -> {
            if (errors-- == 1) errorText.setText("");
        });
    }

	private static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try { Thread.sleep(millis); }
                catch (InterruptedException e) { }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }

	public Parent getView() {
		return root;
	}
}
