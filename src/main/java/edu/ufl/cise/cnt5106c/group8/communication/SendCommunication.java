package edu.ufl.cise.cnt5106c.group8.communication;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import edu.ufl.cise.cnt5106c.group8.handler.MessageHandler;
import edu.ufl.cise.cnt5106c.group8.manager.MessageManager;
import edu.ufl.cise.cnt5106c.group8.model.Peer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class SendCommunication extends Thread{

    private Socket socket;

    private String remotePeerId;

    private Peer localPeer;

    private ObjectOutputStream out;

    private Map<String, LinkedBlockingQueue<MessageManager>> messageQueueMap;

    public SendCommunication(Socket socket, String remotePeerId, Peer localPeer, Map<String, LinkedBlockingQueue<MessageManager>> messageQueueMap) throws IOException {
        this.socket = socket;
        this.remotePeerId = remotePeerId;
        this.localPeer = localPeer;
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        this.messageQueueMap = messageQueueMap;

    }

    public void send(Object message) throws IOException {
        out.writeObject(message);
        out.flush();
    }


    @Override
    public void run() {
        while (true) {
            try {
//                Object message = messageQueueMap.get(remotePeerId).take();
//                MessageHandler messageHandler = new MessageHandler(message, remotePeerId, localPeer, false);
//                messageHandler.handle(this);

                MessageManager messageManager = messageQueueMap.get(remotePeerId).take();
                Object message = messageManager.getMessage();
                boolean fromRemote = messageManager.isFromRemote();
                MessageHandler messageHandler = new MessageHandler(message, remotePeerId, localPeer, fromRemote);
                messageHandler.handle(this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
