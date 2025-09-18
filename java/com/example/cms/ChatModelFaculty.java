package com.example.cms;

public class ChatModelFaculty {
    private String messageId;
    private String message;
    private String senderId;
    private String receiverId;
    private String timestamp;
    private boolean readStatus;

    public ChatModelFaculty() {
    }

    public ChatModelFaculty(String messageId, String message, String senderId, String receiverId, String timestamp , boolean readStatus) {
        this.messageId = messageId;
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
        this.readStatus = readStatus;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
