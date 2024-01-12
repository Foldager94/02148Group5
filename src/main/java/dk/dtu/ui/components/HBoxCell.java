package dk.dtu.ui.components;

import java.awt.Point;
import java.awt.TextField;
import java.io.File;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class HBoxCell extends HBox {
    Label label = new Label();

    HBoxCell(String labelText, boolean host) {
        super();

        label.setText(labelText);
        label.setMaxWidth(Double.MAX_VALUE);
        label.getStyleClass().add("list-label");
        HBox.setHgrow(label, Priority.ALWAYS);
        this.getChildren().add(label);
        if (host) {
            Button button = new Button();
            button.setText("Kick");
            button.getStyleClass().add("kick-button");
            this.getChildren().add(button);
        }
    }
}