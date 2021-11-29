package edu.ufl.cise.cnt5106c.group8.model;

import edu.ufl.cise.cnt5106c.group8.manager.MessageManager;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Peer {
    private String peerId;

    private String hostname;

    private int port;

    private boolean hasFile;

    private Common common;

    private ConcurrentMap<String, Boolean> connectStatusMap;

    private char[] bitField;

    private ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap;

    private ConcurrentMap<String, Boolean> remoteChokeLocalMap;

    private ConcurrentMap<String, Boolean> remoteInterestedLocalMap;

    private ConcurrentMap<String, ConcurrentMap<Integer, Boolean>> pieceIndexMap;

    private ConcurrentMap<String, Boolean> localChokeRemoteMap;

    private ConcurrentMap<String, Boolean> localInterestedRemoteMap;

    private ConcurrentMap<Integer, byte[]> filePieceMap;

    public Peer(String peerId) {
        this.peerId = peerId;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
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

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public Map<String, Boolean> getConnectStatusMap() {
        return connectStatusMap;
    }

    public void setConnectStatusMap(ConcurrentMap<String, Boolean> connectStatusMap) {
        this.connectStatusMap = connectStatusMap;
    }

    public char[] getBitField() {
        return bitField;
    }

    public void setBitField(char[] bitField) {
        this.bitField = bitField;
    }

    public ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> getMessageQueueMap() {
        return messageQueueMap;
    }

    public void setMessageQueueMap(ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap) {
        this.messageQueueMap = messageQueueMap;
    }

    public ConcurrentMap<String, Boolean> getRemoteChokeLocalMap() {
        return remoteChokeLocalMap;
    }

    public void setRemoteChokeLocalMap(ConcurrentMap<String, Boolean> remoteChokeLocalMap) {
        this.remoteChokeLocalMap = remoteChokeLocalMap;
    }

    public ConcurrentMap<String, Boolean> getRemoteInterestedLocalMap() {
        return remoteInterestedLocalMap;
    }

    public void setRemoteInterestedLocalMap(ConcurrentMap<String, Boolean> remoteInterestedLocalMap) {
        this.remoteInterestedLocalMap = remoteInterestedLocalMap;
    }

    public ConcurrentMap<String, ConcurrentMap<Integer, Boolean>> getPieceIndexMap() {
        return pieceIndexMap;
    }

    public void setPieceIndexMap(ConcurrentMap<String, ConcurrentMap<Integer, Boolean>> pieceIndexMap) {
        this.pieceIndexMap = pieceIndexMap;
    }

    public ConcurrentMap<String, Boolean> getLocalChokeRemoteMap() {
        return localChokeRemoteMap;
    }

    public void setLocalChokeRemoteMap(ConcurrentMap<String, Boolean> localChokeRemoteMap) {
        this.localChokeRemoteMap = localChokeRemoteMap;
    }

    public ConcurrentMap<String, Boolean> getLocalInterestedRemoteMap() {
        return localInterestedRemoteMap;
    }

    public void setLocalInterestedRemoteMap(ConcurrentMap<String, Boolean> localInterestedRemoteMap) {
        this.localInterestedRemoteMap = localInterestedRemoteMap;
    }

    public ConcurrentMap<Integer, byte[]> getFilePieceMap() {
        return filePieceMap;
    }

    public void setFilePieceMap(ConcurrentMap<Integer, byte[]> filePieceMap) {
        this.filePieceMap = filePieceMap;
    }
}
