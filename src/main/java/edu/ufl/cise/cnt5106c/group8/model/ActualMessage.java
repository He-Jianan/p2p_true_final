package edu.ufl.cise.cnt5106c.group8.model;

import edu.ufl.cise.cnt5106c.group8.enums.MessageTypeEnum;

public class ActualMessage {
    private int messageLength;

    private MessageTypeEnum messageType;

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