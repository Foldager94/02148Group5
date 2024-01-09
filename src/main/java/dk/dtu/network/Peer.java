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
import java.util.List;
import java.util.UUID;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

public class Peer {
    String name;
    SequentialSpace chat; // Own chat
    SpaceRepository chatResp; // repository for the peers own chat

    SequentialSpace peers; // (id, name, uri)
    SpaceRepository chats;  // containts all chats to the other peers

    String MPIP = "10.209.157.221";
    String MPPort = "9001";
    int MPID;
    String ip;
    String port;
    String uri;
    String id;
    
    RemoteSpace requests;
    RemoteSpace ready;


    public Peer(String name) {
        this.name = name;
    }
    private void setIpAndPort() {
        try {
            Dotenv dotenv = null;
            dotenv = Dotenv.configure().load();
            System.out.println(dotenv.get("MPIP"));
            
            ip = Inet4Address.getLocalHost().getHostAddress(); // trim
        } catch (Exception e) {
            e.printStackTrace();
        }
        port = "9001";
    }

    private void connectToMP() {
        try {
            requests = new RemoteSpace(formatURI(MPIP, MPPort) + "/requests?keep");
            ready = new RemoteSpace(formatURI(MPIP, MPPort) + "/ready?keep");
            requests.put("helo", name, formatURI(ip, port));
            Object[] obj = requests.get(
                new ActualField("helo"), 
                new FormalField(String.class), // peer id
                new FormalField(String.class), // MP uri
                new FormalField(Object.class) // peers (name, id, uri)[]
            );
            id = (String)obj[1];
            String[][] peerArray = (String[][])obj[3];
            for (String[] peer : peerArray) {
                String id   = peer[0];
                String name = peer[1];
                String uri  = peer[2];
                peers.put(id, name, uri);
                chats.add(id, new RemoteSpace(uri + "/chat?keep")); 
            }
        } catch (Exception e) {}
    }

    private void initSpaces() {
        try {
            chat = new SequentialSpace();
            chatResp = new SpaceRepository();
            chatResp.add("chat", chat);
            chatResp.addGate(formatURI(ip, port) + "/chatResp?keep");
            peers = new SequentialSpace();
            
            chats = new SpaceRepository();
        } catch(Exception e) {}
    }
    // for each of it's pear, send a introduction message to the pears chat  
    private void sendIntroduction() {  
        try {
            List<Object[]> objs = peers.queryAll(
                new FormalField(String.class), // id
                new FormalField(String.class), // name
                new FormalField(String.class) // uri
            );
            for (Object[] obj : objs) {
                String id = (String)obj[1];
                chats.get(id).put("intro", id, name, ip);
                String name = (String)obj[2];
                peers.put("welcome", name);
            }
            // peers.put("Welcome", name);
        }
        catch(Exception e) {}
    }
    // look in the peers chat for new introductions
    private void getIntroduction() {
        new Thread(() -> {
            try {
                while (true) {
                    Object[] obj = chat.get(
                        new ActualField("intro"), 
                        new FormalField(String.class), // id
                        new FormalField(String.class), // name
                        new FormalField(String.class) // uri
                    );
                    String id = (String)obj[1];
                    String uri = (String)obj[3];
                    chats.add(id, new RemoteSpace(uri + "/chat?keep"));
                }
            } catch (Exception e) {}
        }).start();
    }
    
    // Create a new thread when a message is received and store
    private void startMessageReciever() { 
        new Thread(() -> {
            while (true) {
                try {
                    Object[] messageTuple = chatResp.get("chat").get(
                        new FormalField(String.class), // sender 
                        new FormalField(String.class), // message
                        new FormalField(Boolean.class) // isAllChat
                    );
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
        try {
            chats.get(recieverID).put(message, recieverID);
        }
        catch(Exception e) {}
    }

    private String formatURI(String ip, String port) {
        return "tcp://" +  ip + "/" + port;
    }

    private void sendReadyFlag(boolean isReady) {
        try {
            ready.put("ready", id, isReady); 
        }
        catch (Exception e) {}

    }    
}
