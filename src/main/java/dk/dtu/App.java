package dk.dtu;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import dk.dtu.ui.LobbyScreen;
import dk.dtu.ui.StartScreen;
import javafx.application.Application;

public class App {
    
    public static void main(String[] args) {
        Application.launch(StartScreen.class, args);
    }    
}
