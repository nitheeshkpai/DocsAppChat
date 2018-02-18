package com.example.nitheeshkpai.docsappchat;

import android.text.Editable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nitheeshkpai on 2/18/18.
 */

class Message {

    @SerializedName("chatBotName")
    private String name;

    @SerializedName("chatBotID")
    private Integer userId;
    private String message;

    public Message(int currentUserID, String currentUserName, Editable text) {
        userId = currentUserID;
        name = currentUserName;
        message = text.toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(int id) {
        userId = id;
    }

    public void setMessage(String msg) {
        message = msg;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public Integer getUserId() {
        return userId;
    }
}
