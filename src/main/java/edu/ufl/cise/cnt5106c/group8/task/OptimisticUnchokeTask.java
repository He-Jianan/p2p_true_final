package edu.ufl.cise.cnt5106c.group8.task;

import edu.ufl.cise.cnt5106c.group8.enums.MessageTypeEnum;
import edu.ufl.cise.cnt5106c.group8.manager.MessageManager;
import edu.ufl.cise.cnt5106c.group8.model.ActualMessage;
import edu.ufl.cise.cnt5106c.group8.model.Peer;
import edu.ufl.cise.cnt5106c.group8.model.PeerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class OptimisticUnchokeTask extends TimerTask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Peer localPeer;

    public OptimisticUnchokeTask(Peer localPeer) {
        this.localPeer = localPeer;
    }

    @Override
    public void run() {
        System.out.println(localPeer.getPeerId() + " is unchoking optismistic peer....");
        List<String> chokedPeerList = new ArrayList<>();
        ConcurrentMap<String, Boolean> localChokeRemoteMap = localPeer.getLocalChokeRemoteMap();
        ConcurrentMap<String, Boolean> remoteInterestedLocalMap = localPeer.getRemoteInterestedLocalMap();
        List<PeerInfo> peerInfoList = localPeer.getPeerInfoList();
        for (PeerInfo peerInfo : peerInfoList) {
            String remotePeerId = peerInfo.getPeerId();
            if (localChokeRemoteMap.get(remotePeerId) && remoteInterestedLocalMap.get(remotePeerId)) {
                chokedPeerList.add(remotePeerId);
            }
        }
        if (chokedPeerList.size() > 0) {
            int randomIndex = (int) (Math.random() * chokedPeerList.size());
            String optimisticUnchokePeerId = chokedPeerList.get(randomIndex);
            ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap = localPeer.getMessageQueueMap();
            messageQueueMap.get(optimisticUnchokePeerId).add(new MessageManager(new ActualMessage(MessageTypeEnum.UNCHOKE, null), false));
            localPeer.setMessageQueueMap(messageQueueMap);

            localChokeRemoteMap.put(optimisticUnchokePeerId, false);
            localPeer.setLocalChokeRemoteMap(localChokeRemoteMap);
            System.out.println(optimisticUnchokePeerId + " is optimistically unchoked.");
            logger.info("Peer [" + localPeer.getPeerId() + "] has the optimistically unchoked neighbor [" + optimisticUnchokePeerId + "]");
        }
    }
}
