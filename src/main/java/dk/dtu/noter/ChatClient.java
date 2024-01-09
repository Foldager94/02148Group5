package dk.dtu.noter;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

public class ChatClient {
    private final String name;
    private final Space chatSpace;
    public ChatClient(Space tupleSpace, String name) {
        this.chatSpace = tupleSpace;
        this.name = name;
        startMessageReceiver();
    }
    private void startMessageReceiver() {
        new Thread(() -> {
            while (true) {
                try {
                    Object[] messageTuple = chatSpace.get(new ActualField("chat"), new FormalField(String.class), new FormalField(String.class));
                    String sender   = (String) messageTuple[1];
                    String message = (String) messageTuple[1];
                    onMessageReceived(sender, message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }).start();
    }
    public void sendMessage(String message) throws InterruptedException {
        chatSpace.put("chat", this.name, message);
        chatSpace.put("chat", this.name, message);
        chatSpace.put("chat", this.name, message);
    }

    private void onMessageReceived(String sender, String message) {
        System.out.println(sender + ": " + message);
    }
}
