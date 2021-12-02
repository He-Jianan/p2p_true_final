package edu.ufl.cise.cnt5106c.group8.handler;

import edu.ufl.cise.cnt5106c.group8.communication.SendCommunication;
import edu.ufl.cise.cnt5106c.group8.enums.MessageTypeEnum;
import edu.ufl.cise.cnt5106c.group8.manager.FileManager;
import edu.ufl.cise.cnt5106c.group8.manager.MessageManager;
import edu.ufl.cise.cnt5106c.group8.model.ActualMessage;
import edu.ufl.cise.cnt5106c.group8.model.HandShakeMessage;
import edu.ufl.cise.cnt5106c.group8.model.Peer;
import edu.ufl.cise.cnt5106c.group8.model.PieceMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler{
    /**
     * message get from message queue
     */
    private Object message;

    private String remotePeerId;

    private Peer localPeer;

    /**
     * judge the message is to send or has received
     */
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
        System.out.println(localPeer.getPeerId() + " is handling handshake message from " + remotePeerId);
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
        char[] bitField = localPeer.getBitField();

        List<Integer> requestList = localPeer.getRequestList();
        ConcurrentMap<String, ConcurrentMap<Integer, Boolean>> remoteBitFieldMap = localPeer.getPieceIndexMap();
        ConcurrentMap<Integer, Boolean> currBitField = remoteBitFieldMap.get(remotePeerId);
        ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap = localPeer.getMessageQueueMap();
        for (int idx = 0; idx < bitField.length; ++idx) {
            if (bitField[idx] == '0' && currBitField.getOrDefault(idx, Boolean.FALSE)) {
                if (!requestList.contains(idx)) {
                    requestList.add(idx);
                    System.out.println(idx + " is added into request list");
                    localPeer.setRequestList(requestList);
                    messageQueueMap.get(remotePeerId).add(new MessageManager(new ActualMessage(MessageTypeEnum.REQUEST, String.valueOf(idx)), false));
                    System.out.println(localPeer.getPeerId() + " is requesting file piece with index: " + idx);
                    break;
                }
            }
        }
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
        pieceIndexMap.put(remotePeerId, map);
        localPeer.setPieceIndexMap(pieceIndexMap);
        if (!localPeer.getLocalInterestedRemoteMap().get(remotePeerId) && localPeer.getBitField()[pieceIndex] == '0') {
            ConcurrentMap<String, Boolean> localInterestedRemoteMap = localPeer.getLocalInterestedRemoteMap();
            localInterestedRemoteMap.put(remotePeerId, true);
            ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap = localPeer.getMessageQueueMap();
            messageQueueMap.get(remotePeerId).add(new MessageManager(new ActualMessage(MessageTypeEnum.INTERESTED, null), false));
            System.out.println(localPeer.getPeerId() + " is sending [interested] message to " + remotePeerId);
            localPeer.setMessageQueueMap(messageQueueMap);
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
                System.out.println(localPeer.getPeerId() + " is sending [interested] message to " + remotePeerId);
                localPeer.setMessageQueueMap(messageQueueMap);
            }
        }
    }

    public void handleRequestMessage(ActualMessage message) {
        String payload = message.getMessagePayload();
        int requestIndex = Integer.parseInt(payload);
        System.out.println(remotePeerId + " is requesting for piece with index " + requestIndex);
        if (!localPeer.getLocalChokeRemoteMap().get(remotePeerId)) {
            ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap = localPeer.getMessageQueueMap();
            messageQueueMap.get(remotePeerId).add(new MessageManager(new PieceMessage(MessageTypeEnum.PIECE, new String(localPeer.getFilePieceMap().get(requestIndex)), String.valueOf(requestIndex)), false));
            System.out.println(localPeer.getPeerId() + " is sending file piece with index " + requestIndex + " to " + remotePeerId);
            localPeer.setMessageQueueMap(messageQueueMap);
        }
    }

    public void handlePieceMessage(ActualMessage message) {
        System.out.println(localPeer + " received piece message from " + remotePeerId);
        PieceMessage pieceMessage = (PieceMessage) message;
        String rawIndex = pieceMessage.getIndex();
        int index = Integer.parseInt(rawIndex);
        String currPiece = message.getMessagePayload();
        byte[] currBytes = currPiece.getBytes();
        ConcurrentMap<Integer, byte[]> filePieceMap = localPeer.getFilePieceMap();
        filePieceMap.put(index, currBytes);
        localPeer.setFilePieceMap(filePieceMap);
        char[] bitField = localPeer.getBitField();
        bitField[index] = '1';
        localPeer.setBitField(bitField);
        ConcurrentMap<String, Integer> downloadRateMap = localPeer.getDownloadRateMap();
        int downloadRate = downloadRateMap.get(remotePeerId);
        downloadRateMap.put(remotePeerId, downloadRate + 1);
        localPeer.setDownloadRateMap(downloadRateMap);

        ConcurrentMap<String, Boolean> currInterestMap = localPeer.getLocalInterestedRemoteMap();
        ConcurrentMap<String, Boolean> currChokeMap = localPeer.getLocalChokeRemoteMap();
        Map<String, Boolean> currConnMap = localPeer.getConnectStatusMap();
        ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap = localPeer.getMessageQueueMap();

        List<Integer> requestList = localPeer.getRequestList();
        ConcurrentMap<String, ConcurrentMap<Integer, Boolean>> pieceIndexMap = localPeer.getPieceIndexMap();
        ConcurrentMap<Integer, Boolean> currRemoteMap = pieceIndexMap.get(remotePeerId);

        requestList.remove(index);
        System.out.println(index + " is removed from request list");
        localPeer.setRequestList(requestList);

        for (int i = 0; i < bitField.length; ++ i) {
            if (bitField[i] == '0' && currRemoteMap.get(i)) {
                if (!requestList.contains(i)) {
                    requestList.add(i);
                    System.out.println(i + " is added into request list");
                    messageQueueMap.get(remotePeerId).add(new MessageManager(new ActualMessage(MessageTypeEnum.REQUEST, String.valueOf(i)), false));
                    System.out.println(localPeer.getPeerId() + " is requesting file piece with index: " + i);
                }
            }
        }

        for (Map.Entry<String, Boolean> entry : currInterestMap.entrySet()) {
            String remoteId = entry.getKey();
            if ((!currChokeMap.get(remoteId)) && (currConnMap.get(remoteId))) {
                messageQueueMap.get(remoteId).add(new MessageManager(new ActualMessage(MessageTypeEnum.HAVE, rawIndex), false));
                System.out.println(localPeer.getPeerId() + " is sending [HAVE] message to " + remoteId);
                localPeer.setMessageQueueMap(messageQueueMap);
            }
        }

        if (hasCompleteFile(bitField) && !localPeer.isHasFile()) {
            try {
                System.out.println(localPeer.getPeerId() + " has the completed file. Now start to assemble file");
                FileManager.assembleFile(filePieceMap, localPeer.getCommon().getFileName());
                localPeer.setHasFile(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private boolean hasCompleteFile(char[] bitField) {
        int totalPieces = localPeer.getCommon().getTotalPieces();
        for (int i = 0; i < totalPieces; i++) {
            if (bitField[i] == '0') {
                return false;
            }
        }
        return true;
    }

}
