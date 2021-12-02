package edu.ufl.cise.cnt5106c.group8.manager;


public class MessageManager{
    private Object message;

    private boolean fromRemote;

    public MessageManager(Object message, boolean fromRemote) {
        this.message = message;
        this.fromRemote = fromRemote;
    }

    public Object getMessage() {
        return message;
    }


    public boolean isFromRemote() {
        return fromRemote;
    }


}
