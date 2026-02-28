package com.example.services_project.model;

public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String content;   // Contenu du message
    private String timestamp; // Date et heure d'envoi
    private boolean isRead;


    public Message(int id, int senderId, int receiverId, String content, String timestamp, boolean isRead) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }


    public Message(int senderId, int receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.isRead = false;
    }

    public Message() {}
    // Getters
    public int getId() {
        return id;
    }
    public int getSenderId() {
        return senderId;
    }
    public int getReceiverId() {
        return receiverId;
    }
    public String getContent() {
        return content;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }


    // Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public void setRead(boolean read) {
        isRead = read;
    }
}