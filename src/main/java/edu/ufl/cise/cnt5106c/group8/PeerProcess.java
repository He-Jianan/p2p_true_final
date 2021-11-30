package edu.ufl.cise.cnt5106c.group8;

import edu.ufl.cise.cnt5106c.group8.communication.ReceiveCommunication;
import edu.ufl.cise.cnt5106c.group8.communication.SendCommunication;
import edu.ufl.cise.cnt5106c.group8.config.CommonReader;
import edu.ufl.cise.cnt5106c.group8.config.PeerInfoReader;
import edu.ufl.cise.cnt5106c.group8.manager.ChoiceManager;
import edu.ufl.cise.cnt5106c.group8.manager.FileManager;
import edu.ufl.cise.cnt5106c.group8.manager.MessageManager;
import edu.ufl.cise.cnt5106c.group8.model.Common;
import edu.ufl.cise.cnt5106c.group8.model.HandShakeMessage;
import edu.ufl.cise.cnt5106c.group8.model.Peer;
import edu.ufl.cise.cnt5106c.group8.model.PeerInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class PeerProcess {
    public static void main(String[] args) throws IOException {
        String peerId;
        if (args.length != 0) {
            peerId = args[0];
        } else{
            peerId = "1001";
        }
        Common common = CommonReader.read("Common-dev.cfg");
        List<PeerInfo> allPeerInfoList = PeerInfoReader.read("PeerInfo-dev.cfg");
        Peer peer = new Peer(peerId);
        peer.setCommon(common);
        ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap = new ConcurrentHashMap<>();
        peer.setMessageQueueMap(messageQueueMap);

        ConcurrentMap<String, Boolean> connectStatusMap = new ConcurrentHashMap<>();
        peer.setConnectStatusMap(connectStatusMap);

        ConcurrentHashMap<String, Boolean> remoteChokeLocalMap = new ConcurrentHashMap<>();
        peer.setRemoteChokeLocalMap(remoteChokeLocalMap);

        ConcurrentHashMap<String, Boolean> remoteInterestedLocalMap = new ConcurrentHashMap<>();
        peer.setRemoteInterestedLocalMap(remoteInterestedLocalMap);

        ConcurrentHashMap<String, Boolean> localChokeRemoteMap = new ConcurrentHashMap<>();
        peer.setLocalChokeRemoteMap(localChokeRemoteMap);

        ConcurrentHashMap<String, Boolean> localInterestedRemoteMap = new ConcurrentHashMap<>();
        peer.setLocalInterestedRemoteMap(localInterestedRemoteMap);

        ConcurrentMap<String, ConcurrentMap<Integer, Boolean>> pieceIndexMap = new ConcurrentHashMap<>();
        peer.setPieceIndexMap(pieceIndexMap);

        ConcurrentMap<String, Integer> downloadRateMap = new ConcurrentHashMap<>();
        peer.setDownloadRateMap(downloadRateMap);

        List<Integer> requestList = new ArrayList<>();
        peer.setRequestList(requestList);

        List<Integer> previousList = new ArrayList<>();
        peer.setPreviousList(previousList);

        List<PeerInfo> peerInfoList = new ArrayList<>();
        for (PeerInfo peerInfo : allPeerInfoList) {

            LinkedBlockingQueue<MessageManager> messageQueue = new LinkedBlockingQueue<>();
            messageQueueMap.put(peerInfo.getPeerId(), messageQueue);
            peer.setMessageQueueMap(messageQueueMap);

            if (peerInfo.getPeerId().equals(peerId)) {
                peer.setHostname(peerInfo.getHostname());
                peer.setPort(peerInfo.getPort());
                peer.setHasFile(peerInfo.isHasFile());
                char[] bitField = new char[peer.getCommon().getTotalLength()];
                if (peer.isHasFile()) {
                    Arrays.fill(bitField, 0, peer.getCommon().getTotalPieces(), '1');
                    Arrays.fill(bitField, peer.getCommon().getTotalPieces(), peer.getCommon().getTotalLength(), '0');
                    ConcurrentMap<Integer, byte[]> filePieceMap = FileManager.file2Piece(peer.getCommon().getFileName(), peer.getCommon().getPieceSize());
                    peer.setFilePieceMap(filePieceMap);
                } else {
                    Arrays.fill(bitField, '0');
                }
                peer.setBitField(bitField);
            } else {
                peerInfoList.add(peerInfo);
            }



        }
        for (PeerInfo peerInfo : peerInfoList) {



            ConcurrentMap<Integer, Boolean> map = new ConcurrentHashMap<>();
            if (peerInfo.isHasFile()) {
                for (int i = 0; i < peer.getCommon().getTotalPieces(); i++) {
                    map.put(i, true);
                }
            } else {
                for (int i = 0; i < peer.getCommon().getTotalPieces(); i++) {
                    map.put(i, false);
                }
            }

            pieceIndexMap.put(peerInfo.getPeerId(), map);
            peer.setPieceIndexMap(pieceIndexMap);
            remoteInterestedLocalMap.put(peerInfo.getPeerId(), false);
            peer.setRemoteInterestedLocalMap(remoteInterestedLocalMap);
            remoteChokeLocalMap.put(peerInfo.getPeerId(), true);
            peer.setRemoteChokeLocalMap(remoteChokeLocalMap);
            localChokeRemoteMap.put(peerInfo.getPeerId(), true);
            peer.setLocalChokeRemoteMap(localChokeRemoteMap);
            localInterestedRemoteMap.put(peerInfo.getPeerId(), false);
            peer.setLocalInterestedRemoteMap(localInterestedRemoteMap);
            downloadRateMap.put(peerInfo.getPeerId(), 0);
            peer.setDownloadRateMap(downloadRateMap);

            connectStatusMap.put(peerInfo.getPeerId(), false);
            peer.setConnectStatusMap(connectStatusMap);
        }
        peer.setPeerInfoList(peerInfoList);

        ChoiceManager choiceManager = new ChoiceManager(peer);
        choiceManager.start();


        for (PeerInfo peerInfo : peerInfoList) {
            if (Integer.parseInt(peerInfo.getPeerId()) < Integer.parseInt(peerId)) {
                try {
                    System.out.println(peerId + " is making a connection with " + peerInfo.getPeerId());
                    Socket socket = new Socket(peerInfo.getHostname(), peerInfo.getPort());



                    SendCommunication sendCommunication = new SendCommunication(socket, peerInfo.getPeerId(), peer, messageQueueMap);
                    sendCommunication.start();
                    ReceiveCommunication receiveCommunication = new ReceiveCommunication(socket, peerInfo.getPeerId(), peer, messageQueueMap);
                    receiveCommunication.start();
                    connectStatusMap.put(peerInfo.getPeerId(), true);
                    messageQueueMap.get(peerInfo.getPeerId()).put(new MessageManager(new HandShakeMessage(peerInfo.getPeerId()), false));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        messageQueueMap.put(peerId, new LinkedBlockingQueue<>());

        ServerSocket serverSocket = new ServerSocket(peer.getPort());

        int count = 1;
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                String remoteHostname = socket.getInetAddress().toString();
                System.out.println(peerId + " received connection request from " + remoteHostname);
                String remotePeerId = String.valueOf(Integer.parseInt(peerId) + count);
                SendCommunication sendCommunication = new SendCommunication(socket, remotePeerId, peer, messageQueueMap);
                sendCommunication.start();
                ReceiveCommunication receiveCommunication = new ReceiveCommunication(socket, remotePeerId, peer, messageQueueMap);
                receiveCommunication.start();
                connectStatusMap.put(remotePeerId, true);
                peer.setConnectStatusMap(connectStatusMap);
                LinkedBlockingQueue<MessageManager> messageQueue = messageQueueMap.getOrDefault(remotePeerId, new LinkedBlockingQueue<>());
                messageQueue.put(new MessageManager(new HandShakeMessage(remotePeerId), false));
                messageQueueMap.put(remotePeerId, messageQueue);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            serverSocket.close();
        }




    }
}
