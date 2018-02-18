package com.example.nitheeshkpai.docsappchat;

import android.text.Editable;

/**
 * Created by nitheeshkpai on 2/18/18.
 */

public class Message {

    private String chatBotName;
    private Integer chatBotID;
    private String message;

    public Message(int currentUserID, String currentUserName, Editable text) {
        chatBotID = currentUserID;
        chatBotName = currentUserName;
        message = text.toString();
    }

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
