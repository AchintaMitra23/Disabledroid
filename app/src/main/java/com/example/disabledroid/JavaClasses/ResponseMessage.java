package com.example.disabledroid.JavaClasses;

public class ResponseMessage {

    private String text;
    private final boolean isMe;

    public ResponseMessage(String text, boolean isMe) {
        this.text = text;
        this.isMe = isMe;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isMe() {
        return isMe;
    }

}
