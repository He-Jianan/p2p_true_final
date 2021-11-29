package edu.ufl.cise.cnt5106c.group8.model;

public class PeerInfo {
    private String PeerId;

    private String hostname;

    private int port;

    private boolean hasFile;

    public String getPeerId() {
        return PeerId;
    }

    public void setPeerId(String peerId) {
        PeerId = peerId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isHasFile() {
        return hasFile;
    }

    public void setHasFile(boolean hasFile) {
        this.hasFile = hasFile;
    }
}
