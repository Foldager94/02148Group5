package dk.dtu.ui.components;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class PlayersListView {
    private ListView<HBoxCell> listView;
    private ObservableList<HBoxCell> myObservableList;
    private int height;
    private boolean host;

    public PlayersListView(int MAX_PLAYERS, boolean host) {
        this.host = host;
        List<HBoxCell> list = new ArrayList<>();
        height = 30 * MAX_PLAYERS;

        listView = new ListView<HBoxCell>();
        myObservableList = FXCollections.observableList(list);
        listView.setItems(myObservableList);
        listView.setMaxHeight(height); // 24 px pr row
        listView.getStyleClass().add("list-view");
    }

    public void addName(String name) {
        myObservableList.add(new HBoxCell(name, host));
    }
    
    public int getHeight() {
        return height;
    }

    public ListView<HBoxCell> getView() {
        return listView;
    }

}