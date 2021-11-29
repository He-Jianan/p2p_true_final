package edu.ufl.cise.cnt5106c.group8.model;

import edu.ufl.cise.cnt5106c.group8.enums.MessageTypeEnum;

public class PieceMessage extends ActualMessage{
    private String index;

    public PieceMessage(MessageTypeEnum messageType, String messagePayload, String index) {
        super(messageType, messagePayload);
        this.index = index;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }


}
