package edu.ufl.cise.cnt5106c.group8.task;

import edu.ufl.cise.cnt5106c.group8.model.Peer;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class RequestTimeoutTask extends TimerTask {
    private Peer localPeer;

    public RequestTimeoutTask(Peer localPeer) {
        this.localPeer = localPeer;
    }

    @Override
    public void run() {
        System.out.println("Request time out check.....");
        List<Integer> previousList = localPeer.getPreviousList();
        List<Integer> requestList = localPeer.getRequestList();
        synchronized (requestList) {
            for (int index : previousList) {
                if (requestList.contains(index)) {
                    requestList.remove(index);
                }
            }
        }

        localPeer.setPreviousList(requestList);
    }
}
