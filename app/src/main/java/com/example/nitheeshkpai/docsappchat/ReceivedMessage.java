package com.example.nitheeshkpai.docsappchat;

/**
 * Created by nitheeshkpai on 2/18/18.
 */

public class ReceivedMessage {

    private String chatBotName;
    private Integer chatBotID;
    private String message;

    public void setChatBotName(String name) {
        chatBotName = name;
    }

    public void setChatBotID(int id) {
        chatBotID = id;
    }

    public void setMessage(String msg) {
        message = msg;
    }

    public String getChatBotName() {
        return chatBotName;
    }

    public String getMessage() {
        return message;
    }

    public Integer getChatBotID() {
        return chatBotID;
    }
}
