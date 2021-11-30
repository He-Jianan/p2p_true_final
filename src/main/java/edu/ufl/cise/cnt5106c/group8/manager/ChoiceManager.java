package edu.ufl.cise.cnt5106c.group8.manager;


import edu.ufl.cise.cnt5106c.group8.model.Peer;
import edu.ufl.cise.cnt5106c.group8.task.OptimisticUnchokeTask;
import edu.ufl.cise.cnt5106c.group8.task.UpdatePreferredPeerTask;

import java.util.Timer;
import java.util.TimerTask;

public class ChoiceManager extends Thread{

    private Peer localPeer;

    public ChoiceManager(Peer localPeer) {
        this.localPeer = localPeer;
    }

    @Override
    public void run() {
        Timer updatePreferredPeerTimer = new Timer();
        Timer optimisticUnchokeTimer = new Timer();

        TimerTask updatePreferredPeerTask = new UpdatePreferredPeerTask(localPeer);
        TimerTask optimisticUnchokeTask = new OptimisticUnchokeTask(localPeer);

        updatePreferredPeerTimer.schedule(updatePreferredPeerTask, localPeer.getCommon().getUnchokingInterval() * 1000L);
        optimisticUnchokeTimer.schedule(optimisticUnchokeTask, localPeer.getCommon().getOptimisticUnchokingInterval() * 1000L);
    }


}
