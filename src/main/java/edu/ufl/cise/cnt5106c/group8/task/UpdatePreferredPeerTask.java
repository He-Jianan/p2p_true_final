package edu.ufl.cise.cnt5106c.group8.task;

import edu.ufl.cise.cnt5106c.group8.enums.MessageTypeEnum;
import edu.ufl.cise.cnt5106c.group8.manager.MessageManager;
import edu.ufl.cise.cnt5106c.group8.model.ActualMessage;
import edu.ufl.cise.cnt5106c.group8.model.Peer;
import edu.ufl.cise.cnt5106c.group8.model.PeerInfo;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class UpdatePreferredPeerTask extends TimerTask {

    private Peer localPeer;

    public UpdatePreferredPeerTask(Peer localPeer) {
        this.localPeer = localPeer;
    }

    @Override
    public void run() {
        System.out.println(localPeer.getPeerId() + " is updating preferred peer list...");
        List<String> preferredPeerList = new ArrayList<>();
        int numberOfPreferredNeighbors = localPeer.getCommon().getNumberOfPreferredNeighbors();
        if (localPeer.isHasFile()) {
            List<String> interestedPeerIdList = new ArrayList<>();
            ConcurrentMap<String, Boolean> remoteInterestedLocalMap = localPeer.getRemoteInterestedLocalMap();
            for (Map.Entry<String, Boolean> entry: remoteInterestedLocalMap.entrySet()) {
                String interestedPeerId = entry.getKey();
                interestedPeerIdList.add(interestedPeerId);
            }
            Collections.shuffle(interestedPeerIdList);
            int index = 0;
            while (preferredPeerList.size() < numberOfPreferredNeighbors) {
                preferredPeerList.add(interestedPeerIdList.get(index));
                index++;
            }
        } else {
            ConcurrentMap<String, Integer> downloadRateMap = localPeer.getDownloadRateMap();
            List<List<Integer>> downloadRateList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry: downloadRateMap.entrySet()) {
                List<Integer> subList = new ArrayList<>();
                String key = entry.getKey();
                int peerId = Integer.parseInt(key);
                subList.add(peerId);
                int downloadRate = entry.getValue();
                subList.add(downloadRate);
                downloadRateList.add(subList);
            }
            downloadRateList.sort((a, b) -> b.get(1) - a.get(1));
            for (int i = 0; i < numberOfPreferredNeighbors; i++) {
                preferredPeerList.add(String.valueOf(downloadRateList.get(i).get(0)));
            }

            //reset download rate
            for (Map.Entry<String, Integer> entry: downloadRateMap.entrySet()) {
                String key = entry.getKey();
                downloadRateMap.put(key, 0);
            }
        }
        List<PeerInfo> peerInfoList = localPeer.getPeerInfoList();
        for (PeerInfo peerInfo : peerInfoList) {
            String remotePeerId = peerInfo.getPeerId();
            ConcurrentMap<String, Boolean> localChokeRemoteMap = localPeer.getLocalChokeRemoteMap();
            if (localPeer.getConnectStatusMap().get(remotePeerId)) {
                if (preferredPeerList.contains(remotePeerId) && localChokeRemoteMap.get(remotePeerId)) {
                    ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap = localPeer.getMessageQueueMap();
                    messageQueueMap.get(remotePeerId).add(new MessageManager(new ActualMessage(MessageTypeEnum.UNCHOKE, null), false));
                    localPeer.setMessageQueueMap(messageQueueMap);

                    localChokeRemoteMap.put(remotePeerId, false);
                    localPeer.setLocalChokeRemoteMap(localChokeRemoteMap);
                    continue;
                }
                if (!preferredPeerList.contains(remotePeerId) && !localChokeRemoteMap.get(remotePeerId)) {
                    ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap = localPeer.getMessageQueueMap();
                    messageQueueMap.get(remotePeerId).add(new MessageManager(new ActualMessage(MessageTypeEnum.CHOKE, null), false));
                    localPeer.setMessageQueueMap(messageQueueMap);

                    localChokeRemoteMap.put(remotePeerId, true);
                    localPeer.setLocalChokeRemoteMap(localChokeRemoteMap);

                }
            }
        }
        System.out.println("The preferred peer list of " + localPeer.getPeerId() +  " is " + preferredPeerList);
    }
}
