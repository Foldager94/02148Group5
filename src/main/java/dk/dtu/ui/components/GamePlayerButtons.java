package dk.dtu.ui.components;

import dk.dtu.game.GameClient;
import dk.dtu.ui.GameScreen;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class GamePlayerButtons {
    private Pane container = new Pane();

    public GamePlayerButtons(GameClient gameClient, GameScreen gameScreen) {
        Button callBtn = new Button("Call");
        Button checkBtn = new Button("Check");
        Button foldBtn = new Button("Fold");
        Button raiseBtn = new Button("Raise");
        TextField raiseField = new TextField();

        checkBtn.setLayoutX(0);
        checkBtn.setLayoutY(0);
        checkBtn.getStyleClass().add("control-button");
        checkBtn.setOnAction(event -> {
		    String res = gameClient.gameCommandHandler("/g Check");
            if (res != null) gameScreen.showError(res);
		});

        callBtn.setLayoutX(110);
        callBtn.setLayoutY(0);
        callBtn.getStyleClass().add("control-button");
        callBtn.setOnAction(event -> {
            String res = gameClient.gameCommandHandler("/g Call");
            if (res != null) gameScreen.showError(res);
        });

        raiseBtn.setLayoutX(0);
        raiseBtn.setLayoutY(55);
        raiseBtn.getStyleClass().add("control-button");
        raiseBtn.setOnAction(event -> {
            String raiseAmount = raiseField.getText();
            int amount = Integer.valueOf(raiseAmount);
            if (amount <= 0) {
                gameScreen.showError("Raise must be positive");
                return;
            }
            if (!raiseAmount.strip().isEmpty()) {
                String res = gameClient.gameCommandHandler("/g Raise " + raiseField.getText());
                if (res != null) gameScreen.showError(res);
                raiseField.setText("");
            }
        });

        foldBtn.setLayoutX(0);
        foldBtn.setLayoutY(110);
        foldBtn.getStyleClass().add("control-fold-button");
        foldBtn.setOnAction(event -> {
            String res = gameClient.gameCommandHandler("/g Fold");
            if (res != null) gameScreen.showError(res);
		});

        raiseField.setLayoutX(110);
        raiseField.setLayoutY(55);
        raiseField.getStyleClass().add("control-field");
        raiseField.setPromptText("00000");

        container.getChildren().addAll(callBtn, checkBtn, foldBtn, raiseBtn, raiseField);
    }

    public Pane getView() {
        return container;
    }
}


