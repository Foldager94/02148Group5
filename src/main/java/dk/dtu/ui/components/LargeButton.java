package dk.dtu.ui.components;

import javafx.scene.control.Button;


public class LargeButton extends Button {
    private int width = 200;
    private String label;

    public LargeButton(String label) {
        this.label = label;
    }

    public Button getButton() {
        Button button = new Button(label);
        button.setMinWidth(width);
        button.setBackground(null);
        button.getStyleClass().add("button");
        return button;
    }
}
