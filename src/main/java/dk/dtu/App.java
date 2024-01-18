package dk.dtu;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import dk.dtu.network.MasterPeer;
import dk.dtu.network.Peer;

public class App {
      
        public static void main( String[] args ) throws IOException
    {

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
}
