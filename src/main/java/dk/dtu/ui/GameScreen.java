package dk.dtu.ui;

import java.awt.Point;
import java.awt.TextField;
import java.io.File;

import dk.dtu.game.GameClient;
import dk.dtu.game.round.RoundState;
import dk.dtu.ui.util.ScreenSize;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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


	public GameScreen(ScreenSize screenSize, GameClient gameClient) {
		this.screenSize = screenSize;
		makeGraphics(gameClient.getCurrentRoundState(), root);
	}

	public void makeGraphics(RoundState roundState, Pane root) {
        Label header = new Label("Game screen");
		header.setLayoutY(50);


		header.getStyleClass().add("header");

		root.getChildren().add(header);
		root.getStyleClass().add("pane");
		
		Platform.runLater(() -> {
			header.setLayoutX(this.screenSize.getWidth() / 2 - header.getLayoutBounds().getWidth() / 2);
		});

	}

	public Parent getView() {
		return root;
	}
}
