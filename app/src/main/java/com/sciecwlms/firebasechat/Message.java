package com.sciecwlms.firebasechat;

/**
 * Created by ramdani on 9/18/16.
 */

public class Message {
    public String sender;
    public String message;

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
