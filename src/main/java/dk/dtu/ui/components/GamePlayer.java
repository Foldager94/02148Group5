package dk.dtu.ui.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dk.dtu.game.Card;
import dk.dtu.game.GameClient;
import dk.dtu.game.Player;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class GamePlayer {
    private Pane container = new Pane();

    public GamePlayer(Player player, GameClient gameClient, List<Card> cards, boolean isYou) {

        Label name = new Label(isYou ? "You" : (player.getName() + "#" + player.getId()));
        name.setLayoutY(20);
        name.getStyleClass().add("small-text-center");

        Label balanceText = new Label("Balance: " + String.valueOf(player.getBalance()));
        balanceText.getStyleClass().add("small-text-center");
        balanceText.setLayoutY(45);
        int playerIndex = gameClient.gameState.findPlayerIndexById(player.getId());
        int playerBet = gameClient.getCurrentRoundState().getBets().get(playerIndex);
        Label betText = new Label("Bet: " + String.valueOf(playerBet));
        betText.getStyleClass().add("small-text-center");
        betText.setLayoutY(70);
        if (cards != null) {
            int i = 0;
            for (Card card : cards) {
                try {
                    if (!player.getInRound()) {
                        Label foldedLabel = new Label("Folded");
                        foldedLabel.getStyleClass().add("text-folded");
                        foldedLabel.setLayoutY(125);
                        Platform.runLater(() -> {
                            foldedLabel.setLayoutX(100 - foldedLabel.getBoundsInLocal().getWidth() / 2);
                        });
                        container.getChildren().add(foldedLabel);
                    } else {
                        InputStream stream = new FileInputStream("src\\resources\\images\\cards\\" + card.getImageUrl());
                        Image image = new Image(stream);
                        ImageView imageView = new ImageView();
                        imageView.setImage(image);
                        imageView.setLayoutX(45 + i * 60);
                        imageView.setLayoutY(100);
                        imageView.setFitWidth(50);
                        imageView.setPreserveRatio(true);
                        container.getChildren().add(imageView);
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Error finding image");
                }
                i++;
            }
        } else {
            try {
                if (player.getInRound()) {
                    InputStream stream = new FileInputStream("src\\resources\\images\\cards\\back.png");
                    Image image = new Image(stream);
                    ImageView imageViewL = new ImageView();
                    ImageView imageViewR = new ImageView();
                    imageViewL.setImage(image);
                    imageViewL.setLayoutX(45);
                    imageViewL.setLayoutY(100);
                    imageViewL.setFitWidth(50);
                    imageViewL.setPreserveRatio(true);
                    imageViewR.setImage(image);
                    imageViewR.setLayoutX(45 + 60);
                    imageViewR.setLayoutY(100);
                    imageViewR.setFitWidth(50);
                    imageViewR.setPreserveRatio(true);
                    container.getChildren().addAll(imageViewL, imageViewR);
                } else {
                    Label foldedLabel = new Label("Folded");
                    foldedLabel.getStyleClass().add("text-folded");
                    foldedLabel.setLayoutY(125);
                    Platform.runLater(() -> {
                        foldedLabel.setLayoutX(100 - foldedLabel.getBoundsInLocal().getWidth() / 2);
                    });
                    container.getChildren().add(foldedLabel);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error finding image");
            }
        }
        container.getChildren().addAll(name, balanceText, betText);
        
        Platform.runLater(() -> {
            name.setLayoutX(100 - name.getBoundsInLocal().getWidth() / 2);
            balanceText.setLayoutX(100 - balanceText.getBoundsInLocal().getWidth() / 2);
            betText.setLayoutX(100 - betText.getBoundsInLocal().getWidth() / 2);
		});
    }

    public Pane getView() {
        return container;
    }
}
