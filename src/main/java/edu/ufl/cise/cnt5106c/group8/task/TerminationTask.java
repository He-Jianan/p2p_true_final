package edu.ufl.cise.cnt5106c.group8.task;

import edu.ufl.cise.cnt5106c.group8.model.Peer;
import edu.ufl.cise.cnt5106c.group8.model.PeerInfo;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;

public class TerminationTask extends TimerTask {
    private Peer localPeer;

    public TerminationTask(Peer localPeer) {
        this.localPeer = localPeer;
    }

    @Override
    public void run() {
        ConcurrentMap<String, ConcurrentMap<Integer, Boolean>> pieceIndexMap = localPeer.getPieceIndexMap();
        if (checkTermination(pieceIndexMap)) {
            System.out.println("Every peer has the whole file.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }

    }

    private boolean checkTermination (ConcurrentMap<String, ConcurrentMap<Integer, Boolean>> pieceIndexMap) {
        if (!localPeer.isHasFile()) {
            return false;
        }
        List<PeerInfo> peerInfoList = localPeer.getPeerInfoList();
        for (PeerInfo peerInfo : peerInfoList) {
            String neighborPeerId = peerInfo.getPeerId();
            ConcurrentMap<Integer, Boolean> neighborPieceIndexMap = pieceIndexMap.get(neighborPeerId);
            for (Map.Entry<Integer, Boolean> entry: neighborPieceIndexMap.entrySet()) {
                if (!entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }
}
