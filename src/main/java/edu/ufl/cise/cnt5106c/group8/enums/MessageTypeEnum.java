package edu.ufl.cise.cnt5106c.group8.enums;


public enum MessageTypeEnum{
    CHOKE(0),

    UNCHOKE(1),

    INTERESTED(2),

    NOT_INTERESTED(3),

    HAVE(4),

    BITFIELD(5),

    REQUEST(6),

    PIECE(7);

    Integer code;

    MessageTypeEnum(Integer code) {
        this.code = code;
    }
}
