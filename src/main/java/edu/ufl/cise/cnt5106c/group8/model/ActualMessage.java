package edu.ufl.cise.cnt5106c.group8.model;

import edu.ufl.cise.cnt5106c.group8.enums.MessageTypeEnum;

import java.io.Serializable;

public class ActualMessage implements Serializable {
    private static final long serialVersionUID = 8983558202217591746L;

    private int messageLength;

    /**
     * 0. choke
     * 1. unchoke
     * 2. interested
     * 3. not interested
     * 4. have
     * 5. bitfield
     * 6. request
     * 7. piece
     */
    private MessageTypeEnum messageType;

    /**
     * Payload of actual message
     */
    private String messagePayload;

    public ActualMessage(MessageTypeEnum messageType, String messagePayload) {
        this.messageType = messageType;
        this.messagePayload = messagePayload;
        if (messagePayload != null) {
            this.messageLength = messagePayload.length() + 1;
        } else {
            this.messageLength = 1;
        }
    }

    public int getMessageLength() {
        return messageLength;
    }

    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    public MessageTypeEnum getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageTypeEnum messageType) {
        this.messageType = messageType;
    }

    public String getMessagePayload() {
        return messagePayload;
    }

    public void setMessagePayload(String messagePayload) {
        this.messagePayload = messagePayload;
    }
}
