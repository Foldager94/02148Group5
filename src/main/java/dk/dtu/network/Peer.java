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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

public class Peer {
    public String name;
    public SequentialSpace chat; // Own chat
    public SpaceRepository remoteResp; // the peers remote repository(s)

    public SequentialSpace peers; // (id, name, uri)
    public SpaceRepository chats;  // containts all chats to the other peers

    public String MPIP = "192.168.0.104";
    public String MPPort = "9004";
    public int MPID;
    public String ip;
    public String port;
    public String uri;
    public String id;
    
    public RemoteSpace requests;
    public RemoteSpace ready;


    public Peer(String name, String port) {
        this.name = name;
        this.port = port;
        setIpAndPort();
        initSpaces();
    }

    public void setIpAndPort() {
        try {
            // Dotenv dotenv = null;
            // dotenv = Dotenv.configure().load();
            // System.out.println(dotenv.get("MPIP"));
            ip = Inet4Address.getLocalHost().getHostAddress().toString();
            // port = "9002";
        } catch (Exception e) {}
    }

    public void connectToMP() {
        try {
            requests = new RemoteSpace(formatURI(MPIP, MPPort) + "/requests?keep");
            ready = new RemoteSpace(formatURI(MPIP, MPPort) + "/ready?keep");
            requests.put("Helo", name, formatURI(ip, port));
            
            Object[] obj = requests.get(
                new ActualField("Helo"), 
                new FormalField(String.class), // Mp id
                new FormalField(String.class), // peer id
                new FormalField(LinkedList.class), // peers (name, id, uri)[]
                new FormalField(String.class)
            );
            //("Helo", this.id, peerId, peers, info[2]);
            id = (String)obj[2];
            
            peers.put(id, this.name, obj[4]);

            //String[][] peerArray = (String[][])obj[3];
            LinkedList<ArrayList> peerList = (LinkedList<ArrayList>)obj[3];
            // {Id, Name, Ip:port}
            for (ArrayList peer : peerList) {
                System.out.println(peer.toString());
                String peerId   = (String)peer.get(0);
                String peerName = (String)peer.get(1);
                String peerUri  = (String)peer.get(2);
                peers.put(peerId, peerName, peerUri);
                System.out.println("Trying to connect to: " + peerUri);
                chats.add(peerId, new RemoteSpace(peerUri + "/chat?keep")); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSpaces() {
        try {
            chat = new SequentialSpace();
            remoteResp = new SpaceRepository();
            remoteResp.add("chat", chat);
            remoteResp.addGate(formatURI(ip, port) + "/?keep");
            peers = new SequentialSpace();
            
            chats = new SpaceRepository();
        } catch(Exception e) {System.out.println(e.getStackTrace());}
    }

    // for each of its peers, send a introduction message to the peers chat  
    public void sendIntroduction() {  
        try {
            List<Object[]> objs = peers.queryAll(
                new FormalField(String.class), // id
                new FormalField(String.class), // name
                new FormalField(String.class) // uri
            );
            for (Object[] obj : objs) {
                String peerId = (String)obj[0];
                
                if (peerId == this.id) { continue; } // ignore itself
                System.out.println("Sending introduction to: " + peerId);
                chats.get(peerId).put("intro", this.id, name, formatURI(ip, port));
                String name = (String)obj[2];
                peers.put("welcome", name);
            }
            // peers.put("Welcome", name);
        }
        catch(Exception e) {}
    }

    // look in the peers chat for new introductions
    public void getIntroduction() {
        new Thread(() -> {
            try {
                while (true) {
                    Object[] obj = chat.get(
                        new ActualField("intro"), 
                        new FormalField(String.class), // id
                        new FormalField(String.class), // name
                        new FormalField(String.class) // uri
                    );
                    System.out.println("recieved introduction");
                    String peerId = (String)obj[1];
                    String peerUri = (String)obj[3];
                    System.out.println(obj[1] + " " + obj[2] + " " + obj[3]);
                    peers.put(obj[1], obj[2], obj[3]);
                    chats.add(peerId, new RemoteSpace(peerUri + "/chat?keep"));
                }
            } catch (Exception e) {}
        }).start();
    }
    
    // Create a new thread when a message is received and store
    public void startMessageReciever() { 
        new Thread(() -> {
            System.out.println("startMessageReciever is running");
            while (true) {
                try {
                    //Object[] messageTuple = remoteResp.get("chat").get(
                    Object[] messageTuple = chat.get(
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
    
    public void sendMessage(String message, String recieverID, Boolean isAllChat) {
        try {
            chats.get(recieverID).put(this.name, message, isAllChat);
        }
        catch(Exception e) {}
    }

    public void sendGlobalMessage(String message) {
        List<Object[]> objs = peers.queryAll(
            new FormalField(String.class), // id
            new FormalField(String.class), // name
            new FormalField(String.class) // uri
        );
        for (Object[] obj : objs) {
            String id = (String)obj[0];
            if (id == this.id) { continue; } // ignore itfels
            sendMessage(message, id, true);
        }
    }

    public String formatURI(String ip, String port) {
        return "tcp://" +  ip + ":" + port;
    }

    public void sendReadyFlag(boolean isReady) {
        try {
            ready.put("ready", id, isReady); 
        }
        catch (Exception e) {}
    }
}
