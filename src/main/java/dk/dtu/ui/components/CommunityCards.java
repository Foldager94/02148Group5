package dk.dtu.ui.components;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import dk.dtu.game.Card;
import dk.dtu.game.GameClient;
import dk.dtu.game.Player;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
// 960 wqi

public class CommunityCards {
    private Pane container = new Pane();
    
    public CommunityCards(List<Card> cards) {
        container.setPrefWidth(960);
        if (cards != null) {
            int i = 0;
            for (Card card : cards) {
                try {
                    InputStream stream = new FileInputStream("src\\resources\\images\\cards\\" + card.getImageUrl());
                    Image image = new Image(stream);
                    ImageView imageView = new ImageView();
                    imageView.setImage(image);
                    imageView.setLayoutX(((960 - (80 * cards.size() + (8 * cards.size() - 1))) / 2) + i * 88);
                    imageView.setLayoutY(10);
                    imageView.setFitWidth(80);
                    imageView.setPreserveRatio(true);
                    container.getChildren().add(imageView);
                } catch (FileNotFoundException e) {
                    System.out.println("Error finding image");
                }
                i++;
            }
        } 
    }

    public Pane getView() {
        return container;
    }
}
