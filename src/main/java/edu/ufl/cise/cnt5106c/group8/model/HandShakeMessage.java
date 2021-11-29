package edu.ufl.cise.cnt5106c.group8.model;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HandShakeMessage {
    private final String HEADER = "P2PFILESHARINGPROJ";

    private final byte[] ZEROBITS = new byte[10];

    private String peerId;

    public String getHEADER() {
        return HEADER;
    }

    public byte[] getZEROBITS() {
        return ZEROBITS;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public HandShakeMessage(String peerId) {
        this.peerId = peerId;
    }

    @Override
    public String toString() {
        return "HandShakeMessage{" +
                "HEADER='" + HEADER + '\'' +
                ", ZEROBITS=" + Arrays.toString(ZEROBITS) +
                ", peerId='" + peerId + '\'' +
                '}';
    }

    public byte[] createHandShakeMessage() {
        String handShakeMessage = HEADER + ZEROBITS + peerId;
        return handShakeMessage.getBytes(StandardCharsets.UTF_8);
    }
}
