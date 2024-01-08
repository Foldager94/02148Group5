package dk.dtu.network;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.UUID;


public class Peer {
    String name;
    SequentialSpace chat;
    SequentialSpace peers;

    SpaceRepository chats;

    String MPIP = "10.209.157.221";
    String MPPort = "9001";
    
    int MPID;
    String ip;
    String port;
    int id;
    RemoteSpace requests;
    
    public Peer(String name) {
        this.name = name;
    }

    private void getIpAndPort() {
        try {
            this.ip = Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.port = "9001";
    }
    private void connectToMP() {
        try {
            requests = new RemoteSpace("tcp://" +  MPIP + "/" + MPPort + "/requests?keep");
            requests.put("helo", name, ip, port);
            // recieve helo MPid ip port peers
            Object[] data = requests.get(
                new ActualField("helo"), 
                new FormalField(String.class), 
                new FormalField(String.class), 
                new FormalField(String.class), 
                new FormalField(Object.class)
            );

            
        } catch (Exception e) {}
    }

    private void initSpaces() {
        try {
            chat = new SequentialSpace();
            peers = new SequentialSpace();
        } catch(Exception e) {}
    }
    
    private void sendIntroduction() {    
        try {
            peers.put("Welcome", name);
        }
        catch(Exception e) {}

    }
    private void recieveIntroduction() {
        new Thread(() -> {
            chats.get("introduction");

    
        }).start();

    }

    private void startMessageReciever() {
        new Thread(() -> {
            while (true) {
                try {
                    Object[] messageTuple = chat.get(new FormalField(String.class), new FormalField(String.class));
                    String sender = (String) messageTuple[0];
                    String message = (String) messageTuple[1];
                    System.out.println(sender + ": " + message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }).start();
    }    

    private void sendMessage(String message, String recieverID) {
    }
}
