package dk.dtu.network;
import dk.dtu.chat.ChatClient;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Peer {

    public ChatClient chatO;
    public String name;
    //public SequentialSpace chat; // Own chat
    public SpaceRepository remoteResp; // the peers remote repository(s)

    public SequentialSpace peers; // (id, name, uri)
    //public SpaceRepository chats;  // containts all chats to the other peers

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
        initChatClient();
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
                chatO.addChatToRepo(peerId,peerUri);
                //chats.add(peerId, new RemoteSpace(peerUri + "/chat?keep")); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSpaces() {
        try {
            //chat = new SequentialSpace();
            remoteResp = new SpaceRepository();
            remoteResp.add("chat", chatO.getChat());
            remoteResp.addGate(formatURI(ip, port) + "/?keep");
            peers = new SequentialSpace();
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
                
                chatO.getPeerChat(peerId).put("intro", this.id, name, formatURI(ip, port));
            }
        }
        catch(Exception e) {}
    }

    // look in the peers chat for new introductions
    public void getIntroduction() {
        new Thread(() -> {
            try {
                while (true) {
                    Object[] obj = chatO.getChat().get(
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
                    chatO.addChatToRepo(peerId,peerUri);
                    //chats.add(peerId, new RemoteSpace(peerUri + "/chat?keep"));
                }
            } catch (Exception e) {}
        }).start();
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

    public List<String> getPeerIds(){
        List<Object[]> objs = peers.queryAll(
            new FormalField(String.class), // id
            new FormalField(String.class), // name
            new FormalField(String.class) // uri
        );

        List<String> ids = new ArrayList<>();

        for(Object[] obj : objs){
            ids.add((String)obj[0]);
        }
        return ids;
    }

    public void initChatClient(){
        chatO = new ChatClient(this);
    }

    public void startMessageReciever(){
        chatO.startMessageReciever();
    }

    public void sendGlobalMessage()  {

        
        
    }

    public void commandHandler() throws IOException{
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String command = input.readLine();
        String[] commandTag = command.split(" ");
        switch (commandTag[0]) {
            case "/p":
                chatO.chatHandler(command);
                break;
            case "/c":
            chatO.chatHandler(command);
                break;
            case "/g":
                //Game handler
            default:
                System.err.println("Unknow command.");
                System.err.println("/p for private message");
                System.err.println("/c for global chat message");
                System.err.println("/g for game commands");
                break;
        }
    }
}
