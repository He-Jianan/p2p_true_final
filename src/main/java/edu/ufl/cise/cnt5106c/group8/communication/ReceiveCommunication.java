package edu.ufl.cise.cnt5106c.group8.communication;

import edu.ufl.cise.cnt5106c.group8.manager.MessageManager;
import edu.ufl.cise.cnt5106c.group8.model.Peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class ReceiveCommunication extends Thread{
    private Socket socket;

    private String remotePeerId;

    private Peer localPeer;

    private Map<String, LinkedBlockingQueue<MessageManager>> messageQueueMap;

    private ObjectInputStream in;

    public ReceiveCommunication(Socket socket, String remotePeerId, Peer localPeer, Map<String, LinkedBlockingQueue<MessageManager>> messageQueueMap) throws IOException {
        this.socket = socket;
        this.remotePeerId = remotePeerId;
        this.localPeer = localPeer;
        this.messageQueueMap = messageQueueMap;
        in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object message = in.readObject();
                messageQueueMap.get(remotePeerId).put(new MessageManager(message, true));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Other peer has detected that every peer has the completed file. Now terminate the application.");
                System.exit(0);
            }

        }
    }
}
