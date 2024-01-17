package dk.dtu;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import java.io.BufferedReader;
import dk.dtu.network.MasterPeer;
import dk.dtu.network.Peer;
import dk.dtu.game.Hand;
import dk.dtu.game.Card;
import dk.dtu.game.Suit;
import dk.dtu.game.Deck;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import dk.dtu.ui.LobbyScreen;
import dk.dtu.ui.StartScreen;
import javafx.application.Application;

public class App {
    
    public static void main(String[] args) {
        Application.launch(StartScreen.class, args);
    }   
}