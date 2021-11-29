package edu.ufl.cise.cnt5106c.group8.handler;

import edu.ufl.cise.cnt5106c.group8.communication.SendCommunication;
import edu.ufl.cise.cnt5106c.group8.enums.MessageTypeEnum;
import edu.ufl.cise.cnt5106c.group8.manager.MessageManager;
import edu.ufl.cise.cnt5106c.group8.model.ActualMessage;
import edu.ufl.cise.cnt5106c.group8.model.HandShakeMessage;
import edu.ufl.cise.cnt5106c.group8.model.Peer;
import edu.ufl.cise.cnt5106c.group8.model.PieceMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler{
    private Object message;

    private String remotePeerId;

    private Peer localPeer;

    private boolean isFromRemote;

    public MessageHandler(Object message, String remotePeerId, Peer localPeer, boolean isFromRemote) {
        this.message = message;
        this.remotePeerId = remotePeerId;
        this.localPeer = localPeer;
        this.isFromRemote = isFromRemote;
    }


    public void handle(SendCommunication sc) throws IOException, InterruptedException {
        if (isFromRemote) {
            if (message.getClass().equals(HandShakeMessage.class)) {
                HandShakeMessage handShakeMessage = (HandShakeMessage) message;
                System.out.println("Handling the handshake message from " + remotePeerId + " to " + localPeer.getPeerId());
                handleHandshakeMessage(handShakeMessage);
            } else if (message.getClass().equals(ActualMessage.class)) {
                ActualMessage actualMessage = (ActualMessage) message;
                switch (actualMessage.getMessageType()) {
                    case CHOKE:
                        handleChokeMessage(actualMessage);
                        break;
                    case UNCHOKE:
                        handleUnChokeMessage(actualMessage);
                        break;
                    case INTERESTED:
                        handleInterestedMessage(actualMessage);
                        break;
                    case NOT_INTERESTED:
                        handleNotInterestedMessage(actualMessage);
                        break;
                    case HAVE:
                        handleHaveMessage(actualMessage);
                        break;
                    case BITFIELD:
                        handleBitfieldMessage(actualMessage);
                        break;
                    case REQUEST:
                        handleRequestMessage(actualMessage);
                        break;
                    case PIECE:
                        handlePieceMessage(actualMessage);
                        break;
                }
            }
        } else {
            sc.send(message);
        }
    }

    public void handleHandshakeMessage(HandShakeMessage message) throws InterruptedException {
        if (message.getPeerId().equals(remotePeerId) && message.getHEADER().equals("P2PFILESHARINGPROJ")) {
            if (localPeer.isHasFile()) {
                ActualMessage bitFieldMessage = new ActualMessage(MessageTypeEnum.BITFIELD, String.valueOf(localPeer.getBitField()));
                MessageManager messageManager = new MessageManager(bitFieldMessage, false);
                ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap = localPeer.getMessageQueueMap();
                messageQueueMap.get(remotePeerId).put(messageManager);
                localPeer.setMessageQueueMap(messageQueueMap);
            }
        }
    }

    public void handleChokeMessage(ActualMessage message) {
        System.out.println(localPeer.getPeerId() + " is choked by " + remotePeerId);
        ConcurrentMap<String, Boolean> chokeMap = localPeer.getRemoteChokeLocalMap();
        chokeMap.put(remotePeerId, true);
        localPeer.setRemoteChokeLocalMap(chokeMap);
    }

    public void handleUnChokeMessage(ActualMessage message) {
        System.out.println(localPeer.getPeerId() + " is unChoked by " + remotePeerId);
        ConcurrentMap<String, Boolean> chokeMap = localPeer.getRemoteChokeLocalMap();
        chokeMap.put(remotePeerId, false);
        localPeer.setRemoteChokeLocalMap(chokeMap);
        //TODO: Send a request message

    }

    public void handleInterestedMessage(ActualMessage message) {
        System.out.println(remotePeerId + " is interested in " + localPeer.getPeerId());
        ConcurrentMap<String, Boolean> interestMap = localPeer.getRemoteInterestedLocalMap();
        interestMap.put(remotePeerId, true);
        localPeer.setRemoteInterestedLocalMap(interestMap);
    }

    public void handleNotInterestedMessage(ActualMessage message) {
        System.out.println(remotePeerId + " is not interested in " + localPeer.getPeerId());
        ConcurrentMap<String, Boolean> interestMap = localPeer.getRemoteInterestedLocalMap();
        interestMap.put(remotePeerId, false);
        localPeer.setRemoteInterestedLocalMap(interestMap);
    }

    public void handleHaveMessage(ActualMessage message) {
        int pieceIndex = Integer.parseInt(message.getMessagePayload());
        System.out.println(remotePeerId + " has piece with index: " + pieceIndex);
        ConcurrentMap<String, ConcurrentMap<Integer, Boolean>> pieceIndexMap = localPeer.getPieceIndexMap();
        ConcurrentMap<Integer, Boolean> map = pieceIndexMap.get(remotePeerId);
        map.put(pieceIndex, true);
        localPeer.setPieceIndexMap(pieceIndexMap);
        //TODO:
        if (!localPeer.getLocalInterestedRemoteMap().get(remotePeerId) && localPeer.getBitField()[pieceIndex] == '0') {
            ConcurrentMap<String, Boolean> localInterestedRemoteMap = localPeer.getLocalInterestedRemoteMap();
            localInterestedRemoteMap.put(remotePeerId, true);
            ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap = localPeer.getMessageQueueMap();
            messageQueueMap.get(remotePeerId).add(new MessageManager(new ActualMessage(MessageTypeEnum.INTERESTED, null), false));
        }
    }

    public void handleBitfieldMessage(ActualMessage message) {
        System.out.println(localPeer + " received bitfieldMessage from " + remotePeerId);
        String payload = message.getMessagePayload();
        char[] bitField = payload.toCharArray();
        for (int i = 0; i < bitField.length; i++) {
            if (bitField[i] == '1' && localPeer.getBitField()[i] == '0') {
                ConcurrentMap<String, Boolean> localInterestedRemoteMap = localPeer.getLocalInterestedRemoteMap();
                localInterestedRemoteMap.put(remotePeerId, true);
                ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap = localPeer.getMessageQueueMap();
                messageQueueMap.get(remotePeerId).add(new MessageManager(new ActualMessage(MessageTypeEnum.INTERESTED, null), false));
            }
        }
    }

    public void handleRequestMessage(ActualMessage message) {
        String payload = message.getMessagePayload();
        int requestIndex = Integer.parseInt(payload);
        if (!localPeer.getLocalChokeRemoteMap().get(remotePeerId)) {
            ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap = localPeer.getMessageQueueMap();
            messageQueueMap.get(remotePeerId).add(new MessageManager(new PieceMessage(MessageTypeEnum.PIECE, localPeer.getPieceIndexMap().get(requestIndex).toString(), String.valueOf(requestIndex)), false));
        }
    }

    public void handlePieceMessage(ActualMessage message) {

    }

}
