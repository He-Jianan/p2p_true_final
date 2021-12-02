package edu.ufl.cise.cnt5106c.group8.manager;


import edu.ufl.cise.cnt5106c.group8.model.Peer;
import edu.ufl.cise.cnt5106c.group8.task.OptimisticUnchokeTask;
import edu.ufl.cise.cnt5106c.group8.task.RequestTimeoutTask;
import edu.ufl.cise.cnt5106c.group8.task.TerminationTask;
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
        Timer requestTimeoutTimer = new Timer();
        Timer terminationTimer = new Timer();

        TimerTask updatePreferredPeerTask = new UpdatePreferredPeerTask(localPeer);
        TimerTask optimisticUnchokeTask = new OptimisticUnchokeTask(localPeer);
        TimerTask requestTimeoutTask = new RequestTimeoutTask(localPeer);
        TimerTask terminationTask = new TerminationTask(localPeer);

        updatePreferredPeerTimer.schedule(updatePreferredPeerTask, 1000, localPeer.getCommon().getUnchokingInterval() * 1000L);
        optimisticUnchokeTimer.schedule(optimisticUnchokeTask, 1000, localPeer.getCommon().getOptimisticUnchokingInterval() * 1000L);
        requestTimeoutTimer.schedule(requestTimeoutTask, 0, 100000);
        terminationTimer.schedule(terminationTask, 0, 20000);
    }


}
