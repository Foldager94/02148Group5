package dk.dtu.ui;

import java.awt.Point;

import dk.dtu.ui.util.ScreenSize;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CreateLobbyScreen {
	private ScreenSize screenSize;

	public CreateLobbyScreen(ScreenSize screenSize) {
		this.screenSize = screenSize;
		initGraphics();
	}

	public void initGraphics() {
		Text joinText = new Text("Create lobby");
		joinText.setScaleX(3);
		joinText.setScaleY(3);
		joinText.setLayoutX(this.screenSize.getWidth() / 2 - joinText.getLayoutBounds().getWidth() / 2);
		joinText.setLayoutY(20);
	}
}
