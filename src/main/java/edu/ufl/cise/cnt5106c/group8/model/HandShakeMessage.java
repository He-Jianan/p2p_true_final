package edu.ufl.cise.cnt5106c.group8.model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HandShakeMessage implements Serializable {
    private static final long serialVersionUID = 42L;

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
}
