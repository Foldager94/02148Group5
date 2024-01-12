package dk.dtu.noter;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.UUID;

public class ClientMain {
    
    public static void main(String[] args) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            // Set the URI of the chat space
            // Default value
            //System.out.print("Enter URI of the chat server or press enter for default: ");
            String uri = "tcp://localhost:9001";
            //  String uri = "tcp://127.0.0.1:9001/chat?keep";
            // Connect to the remote chat space
            //System.out.println("Connecting to spaces " + uri + "...");
            RemoteSpace chat = new RemoteSpace(uri+"/chat?keep");
            RemoteSpace players = new RemoteSpace(uri +"/players?keep"); // Make a connection to the sequential space "players" in ServerMain
            RemoteSpace game = new RemoteSpace(uri+"/game?keep");
            UUID uuid = UUID.randomUUID();
            //System.out.print("Enter your name: ");
            String name;
            while (true) {
                name = input.readLine();
                players.put("insertName", uuid.toString(), name);
                //System.err.println("Name inserted");
                Object[] isOk = players.get(new ActualField("isNameOk"), new ActualField(uuid.toString()), new FormalField(Boolean.class));
                Boolean ok = (boolean)isOk[2];
                //System.out.println(ok);
                if (ok) { break; }
                System.out.println("Name is already chosen, choose a new name:");   
            }

            ChatClient chatClient = new ChatClient(chat, name);
            // Read user name from the console

            // Keep sending whatever the user types
            // System.out.println("Start chatting...");
            while(true) {
                String message = input.readLine();
                chatClient.sendMessage(message);
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
