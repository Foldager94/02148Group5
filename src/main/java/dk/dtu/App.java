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
        public static void main2( String[] args ) throws IOException
    {
        // // testSpecificHand();
        // for (int i = 0; i < 10; i++) {
        //     testRandomHand();
        //     System.out.println();
        // }


        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Input your name:");
        String name = input.readLine();
        System.out.println("Hello "+name+" do you want to host (h) or join (j) a poker game?");

        System.out.println("Press h or j");
        while (true){
            String c = input.readLine();
            switch (c) {
                case "h":
                    System.out.println("hosting");
                    initMp(name);
                    break;
                case "j":
                    System.out.println("Input port:");
                    String port = input.readLine();
                    System.out.println("joining");
                    initP(name, port);
                    break;
                default:
                    System.out.println("Error: Pick between h or j");
            }
        }
    }


    private static void initMp(String name) throws IOException{
        MasterPeer mp = new MasterPeer(name);
        mp.awaitLobbyRequest();
        mp.awaitReadyFlags();
        mp.getIntroduction();
        mp.startMessageReceiver();
        while(true) {
            mp.commandHandler();
        }
    }
    private static void initP(String name, String port) throws IOException {
        Peer p = new Peer(name, port);
        p.connectToMP();
        p.sendIntroduction();
        p.getIntroduction();
        p.startMessageReceiver();
        while(true) {
            p.commandHandler();
        }
    }

    private static void printPeerData(Peer peer){
        System.out.println(peer.ip);
        System.out.println(peer.port);
    }

    public static void testRandomHand() {
        Deck deck = new Deck();
        List<Card> cards = deck.draw(7);
        // Hand hand = new Hand()
        System.out.println(cards.toString());
        List<Integer> hand = new Hand(cards, "").getHand();
        System.out.println(hand.toString());
    }

    public static void testSpecificHand() {
        List<Card> cardList  = new ArrayList<Card>();
        cardList.add(new Card(9, Suit.Hearts));
        cardList.add(new Card(10, Suit.Hearts));
        cardList.add(new Card(10, Suit.Hearts));
        cardList.add(new Card(10, Suit.Hearts));
        cardList.add(new Card(11, Suit.Spades));
        cardList.add(new Card(5, Suit.Clubs));
        cardList.add(new Card(2, Suit.Clubs));
        System.out.println(cardList.toString());
        List<Integer> hand = new Hand(cardList, "").getHand();
        System.out.println(hand.toString());
    }
}