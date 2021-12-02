package edu.ufl.cise.cnt5106c.group8;

public class RemotePeerInfo {
    public String peerId;
    public String peerAddress;
    public String peerPort;

    public RemotePeerInfo(String pId, String pAddress, String pPort) {
        peerId = pId;
        peerAddress = pAddress;
        peerPort = pPort;
    }
}
