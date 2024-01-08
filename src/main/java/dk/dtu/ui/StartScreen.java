package dk.dtu.ui;

import java.awt.Point;

import dk.dtu.ui.util.ScreenSize;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class StartScreen {
	private ScreenSize screenSize;
    private static Group root = new Group();

	public StartScreen(ScreenSize screenSize) {
		this.screenSize = screenSize;
		initGraphics();
	}

	public void initGraphics() {
        Button createButton = new Button();
        Button joinButton = new Button();
        createButton.setText("Create lobby ababababb");
        // createButton.setOnAction(this::handleClick);
        createButton.setLayoutX(screenSize.getWidth() / 2 - createButton.getLayoutBounds().getWidth() / 2);
        createButton.setLayoutY(20);
        
        joinButton.setText("Join lobby abaabab");
        // joinButton.setOnAction(this::handleClick);
        joinButton.setLayoutX(screenSize.getWidth() / 2 - joinButton.getLayoutBounds().getWidth() / 2);
        joinButton.setLayoutY(60);
        System.out.println(joinButton.getLayoutBounds().getWidth() / 2);
        System.out.println(createButton.getLayoutBounds().getWidth() / 2);

        root.getChildren().add(createButton);
        root.getChildren().add(joinButton);
	}

    public Group getGroup() {
        return root;
    }
}
