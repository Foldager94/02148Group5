package dk.dtu.network;
import dk.dtu.chat.ChatClient;
import dk.dtu.game.GameClient;
import dk.dtu.game.round.RoundLogic;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;
import org.jspace.Tuple;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Peer {

    public ChatClient chat;
    public GameClient game;
    public String name;
    //public SequentialSpace chat; // Own chat
    public SpaceRepository remoteResp; // the peers remote repository(s)
    public SequentialSpace peers; // (id, name, uri, isMuted)
    //public SpaceRepository chats;  // contains all chats to the other peers
    public String MPIP = "localhost";
    public String MPPort = "9004";
    public String MPID;
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
        initGameClient();
        initSpaces();
    }

    public void setIpAndPort() {
        try {
            // Dotenv dotenv = null;
            // dotenv = Dotenv.configure().load();
            // System.out.println(dotenv.get("MPIP"));
            ip = "localhost";
            uri = formatURI(ip, port);
            // ip = Inet4Address.getLocalHost().getHostAddress().toString();
            // port = "9002";
        } catch (Exception e) {}
    }

    public void connectToMP() {
        try {
            requests = new RemoteSpace(formatURI(MPIP, MPPort) + "/requests?keep");
            
            requests.put("Helo", name, formatURI(ip, port));

            Tuple mpResponse = new Tuple(requests.get(
                new FormalField(String.class), // return message
                new ActualField(formatURI(ip, port)) // it own uri (which is used as an identifier)
            ));
            String mpResponseMsg = mpResponse.getElementAt(String.class, 0);

            if(mpResponseMsg.equals("Lobby is full")){
                System.out.println("The lobby is full. Connections to Master Peer will be closed");
                requests.close();
                System.exit(1);
            }
            ready = new RemoteSpace(formatURI(MPIP, MPPort) + "/ready?keep");

            Tuple data = new Tuple(requests.get(
                new ActualField("Helo"), 
                new FormalField(String.class), // Mp id
                new FormalField(String.class), // peer id
                new FormalField(LinkedList.class), // peers (name, id, uri)[]
                new ActualField(uri) // own uri
            ));
            MPID = data.getElementAt(String.class, 1);
            id = data.getElementAt(String.class, 2);
            
            peers.put(id, name, uri, false); // insert itself in its peer space

            LinkedList<ArrayList<Object>> peerList = (LinkedList<ArrayList<Object>>) data.getElementAt(3);

            connectToPeers(peerList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectToPeers(List<ArrayList<Object>> peerList) throws InterruptedException{
        for (ArrayList<Object> peer : peerList) {
            System.out.println(peer.toString());
            String peerId   = (String)peer.get(0);
            String peerName = (String)peer.get(1);
            String peerUri  = (String)peer.get(2);
            Boolean isMuted = false;
            addPeer(peerId, peerName, peerUri);
            // peers.put(peerId, peerName, peerUri, isMuted);
            showTryingtoConnectToPear(peerId, peerName, peerUri);
            // chat.addChatToRepo(peerId,peerUri);
        }
    }

    public void initSpaces() {
        try {
            System.out.println(formatURI(ip, port));
            //chat = new SequentialSpace();
            remoteResp = new SpaceRepository();
            remoteResp.add("chat", chat.getChat());
            remoteResp.add("gameSpace", game.getGameSpace());
            remoteResp.addGate(formatURI(ip, port) + "/?keep");
            peers = new SequentialSpace();
        } catch(Exception e) {System.out.println(e.getMessage());}
    }

    // for each of its peers, send a introduction message to the peers chat  
    public void sendIntroduction() {  
        try {
            List<Object[]> objs = peers.queryAll(
                new FormalField(String.class), // id
                new FormalField(String.class), // name
                new FormalField(String.class), // uri
                new FormalField(Boolean.class)
            );
            for (Object[] obj : objs) {
                String peerId = (String)obj[0];
                
                if (peerId.equals(this.id)) { continue; } // ignore itself
                
                chat.getPeerChat(peerId).put("introduction", this.id, name, formatURI(ip, port));
                chat.getChat().get(new ActualField("response"));
            }
        }
        catch(Exception e) {System.out.println(e.getMessage());}
    }

    public void sendIntroductionMsg(String peerId) throws InterruptedException {
        chat.getPeerChat(peerId).put("intro", this.id, name, formatURI(ip, port));
    }

    // look in the peers chat for new introductions
    public void getIntroduction() {
        new Thread(() -> {
            try {
                while (true) {
                    Tuple data = new Tuple(chat.getChat().get(
                        new ActualField("introduction"), 
                        new FormalField(String.class), // id
                        new FormalField(String.class), // name
                        new FormalField(String.class) // uri
                    ));
                    System.out.println("Received introduction");
                    String peerId = data.getElementAt(String.class, 1);
                    String peerName = data.getElementAt(String.class, 2);
                    String peerUri = data.getElementAt(String.class, 3);
                    addPeer(peerId, peerName, peerUri);
                    showRecievedIntroduction(peerId, peerName, peerUri);
                }
            } catch (Exception e) {System.out.println(e.getMessage());}
        }).start();
    }

    public void addPeer(String peerId, String peerName, String peerUri) {
        try {
            peers.put(peerId, peerName, peerUri, false);
            chat.addChatToRepo(peerId, peerUri);
            chat.getPeerChat(peerId).put("response");
        } catch (Exception e) {}
    }

    public void showRecievedIntroduction(String peerId, String peerName, String peerUri) {
        System.out.println("Recieved introduction from " + peerName);
    }

    public void showTryingtoConnectToPear(String peerId, String peerName, String peerUri) {
        System.out.println("Trying to connect to: "  + peerName + " @" + peerUri);
    }

    public String formatURI(String ip, String port) {
        return "tcp://" +  ip + ":" + port;
    }

    public void sendReadyFlag(boolean isReady) {
        try {
            ready.put("ready", id, isReady); 
        }
        catch (Exception e) {System.out.println(e.getMessage());}
    }

    public List<String> getPeerIds() {
        List<Object[]> objs = peers.queryAll(
            new FormalField(String.class), // id
            new FormalField(String.class), // name
            new FormalField(String.class), // uri
            new FormalField(Boolean.class)
        );
        List<String> ids = new ArrayList<>();
        for(Object[] obj : objs){
            ids.add((String)obj[0]);
        }
        return ids;
    }

    public void initGameClient() {
        game = new GameClient(this);
    }

    public void initChatClient() {
        chat = new ChatClient(this);
    }

    public void startMessageReceiver() {
        chat.startMessageReceiver();
    }

    public String getPeerName(String peerId) throws InterruptedException {
        Object[] peerTuple = peers.query(new ActualField(peerId), new FormalField(String.class), new FormalField(String.class), new FormalField(Boolean.class));
        return (String)peerTuple[1];
    }

    public void updateMuteList(String peerId){
        try {
            if(peers.query(new ActualField(peerId), new FormalField(String.class), new FormalField(String.class), new FormalField(Boolean.class)) != null) {
                Tuple peer = new Tuple(peers.get(new ActualField(peerId), new FormalField(String.class), new FormalField(String.class), new FormalField(Boolean.class)));
                peers.put(peer.getElementAt(String.class, 0), peer.getElementAt(String.class, 1), peer.getElementAt(String.class, 2), !peer.getElementAt(Boolean.class, 3));
            } else {
                System.out.println("Peer with id: " + peerId + " does not exist in your space");
            }
        } catch (InterruptedException e) {
            System.err.println("Could not mute peer with peerId: " + peerId);
        }
    }

    public boolean isPeerMuted(String peerId) throws InterruptedException {
        return (boolean) peers.query(new ActualField(peerId), new FormalField(String.class), new FormalField(String.class), new FormalField(Boolean.class))[3];
    }

    public boolean isPeerKnown(String peerId) throws InterruptedException {
        return peers.query(new ActualField(peerId), new FormalField(String.class), new FormalField(String.class), new FormalField(Boolean.class)) != null;
    }

    public List<Object[]> getListOfPeers(){
        return this.peers.queryAll(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class), new FormalField(Boolean.class));
    }

    public void commandHandler() throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String command = input.readLine();
        String[] commandTag = command.split(" ");
        switch (commandTag[0]) {
            case "/p", "/c", "/start":
                chat.chatHandler(command);
                break;
            case "/m":
                updateMuteList(commandTag[1]);
                break;
            // case "/start":
            //     if(id.equals("0")){
            //         chat.sendGlobalMessage("Game is Starting", getPeerIds());
            //         break;
            //     }
            //     System.out.println("System: You are not allowed to start the game");
                //break;
            case "/g":
                // /g Check, /g Fold, /g raise <amount> /g call <amount>
                //Game handler
                game.gameCommandHandler(command);
                break;

            default:
                System.err.println("Unknown command.");
                System.err.println("/p for private message");
                System.err.println("/c for global chat message");
                System.err.println("/g for game commands");
                break;
        }
    }
}
    