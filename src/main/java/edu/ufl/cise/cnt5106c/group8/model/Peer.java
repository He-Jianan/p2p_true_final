package edu.ufl.cise.cnt5106c.group8.model;

import edu.ufl.cise.cnt5106c.group8.manager.MessageManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Peer{
    private String peerId;

    private String hostname;

    private int port;

    private boolean hasFile;

    /**
     * Information of "Common.cfg"
     */
    private Common common;

    /**
     * Connect status of neighbor peers
     */
    private ConcurrentMap<String, Boolean> connectStatusMap;

    /**
     * Bitfield of local peer
     */
    private char[] bitField;

    /**
     * Message queues of all peers
     */
    private ConcurrentMap<String, LinkedBlockingQueue<MessageManager>> messageQueueMap;

    /**
     * Shows if remote peer has choked local peer
     */
    private ConcurrentMap<String, Boolean> remoteChokeLocalMap;

    /**
     * Shows if remote peer is inrerested in local peer
     */
    private ConcurrentMap<String, Boolean> remoteInterestedLocalMap;

    /**
     * The piece status in every neighbor peer
     */
    private ConcurrentMap<String, ConcurrentMap<Integer, Boolean>> pieceIndexMap;

    /**
     * Shows if local peer has choked remote peer
     */
    private ConcurrentMap<String, Boolean> localChokeRemoteMap;

    /**
     * Shows if local peer is interested in remote peer
     */
    private ConcurrentMap<String, Boolean> localInterestedRemoteMap;

    /**
     * Shows the pieces that local peer has
     */
    private ConcurrentMap<Integer, byte[]> filePieceMap;

    /**
     * Download rate of neighbor peers
     */
    private ConcurrentMap<String, Integer> downloadRateMap;

    /**
     * The basic information of neighbor peers
     */
    private List<PeerInfo> peerInfoList;

    /**
     * The piece index that the local peer is requesting
     */
    private List<Integer> requestList;

    /**
     * The piece index that the local peer is requesting before the timeout interval
     */
    private List<Integer> previousList;

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

    public ConcurrentMap<String, Integer> getDownloadRateMap() {
        return downloadRateMap;
    }

    public void setDownloadRateMap(ConcurrentMap<String, Integer> downloadRateMap) {
        this.downloadRateMap = downloadRateMap;
    }

    public List<PeerInfo> getPeerInfoList() {
        return peerInfoList;
    }

    public void setPeerInfoList(List<PeerInfo> peerInfoList) {
        this.peerInfoList = peerInfoList;
    }

    public List<Integer> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<Integer> requestList) {
        this.requestList = requestList;
    }

    public List<Integer> getPreviousList() {
        return previousList;
    }

    public void setPreviousList(List<Integer> previousList) {
        this.previousList = previousList;
    }
}
