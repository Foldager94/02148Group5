package dk.dtu.noter;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;

public class ServerMain {
    public static void main(String[] args) {
        try {
            System.out.println(InetAddress.getLocalHost());

            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            String uri = "tcp://127.0.0.1:9001/?keep";

            URI myUri = new URI(uri);
            String gateUri = "tcp://" + myUri.getHost() + ":" + myUri.getPort() +  "?keep" ;
            // Create a repository
            SpaceRepository repository = new SpaceRepository();
            // Create a local spaces
            SequentialSpace chat = new SequentialSpace();
            SequentialSpace players = new SequentialSpace();
            SequentialSpace game = new SequentialSpace();

            // Add the spaces to the repository
            repository.add("chat", chat);
            repository.add("players", players);
            String[][] a = {};
            Map<String, String> b = new HashMap<String, String>();
            players.put("playerNames", b);
            repository.add("game", game);
            // System.out.println("Opening repository gate at " + gateUri + "...");
            repository.addGate(gateUri);
            fetchPlayers(players);
            
            // Keep reading chat messages and printing them
            //while (true) {
            //    Object[] t = chat.get(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
            //    System.out.println(t[1] + ": " + t[2]);
            //}

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void fetchPlayers(SequentialSpace players) {
        System.out.println("Ready to receive player name requests...");
        while (true) {
            try {
                // retrieve new player name
                System.err.println("Retrieving request name");
                Object[] newPlayer = players.get(new ActualField("insertName"), new FormalField(String.class), new FormalField(String.class));
                System.out.println("Received new name request");
                String uuid = newPlayer[1].toString();
                String name = newPlayer[2].toString();
                // retrieve array of current player names
                Object[] currentPlayers = players.get(new ActualField("playerNames"), new FormalField(Map.class));
                Map<String, String> playerNames = (Map<String, String>) currentPlayers[1];
                System.out.println("info: " + uuid + " " + name);

                if (playerNames.containsKey(name)) {
                    players.put("isNameOk", uuid, false);
                    System.out.println("Name is not good");
                } else {
                    playerNames.put(name, uuid);
                    players.put("isNameOk", uuid, true);
                    System.out.println("Name is good");
                }             
                players.put("playerNames", playerNames);   
            } catch (Exception e) {}
        }
    }
}
