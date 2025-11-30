package com.example.services_project.model;

public class Message {
    private int id;
    private int senderId;    // ID de l'expéditeur (référence à User.id)
    private int receiverId;  // ID du destinataire (référence à User.id)
    private String content;   // Contenu du message
    private String timestamp; // Date et heure d'envoi (format TEXT dans SQLite)
    private boolean isRead;   // Statut de lecture (0 ou 1 dans SQLite)

    // Constructeur complet (utile lors de la récupération depuis la DB)
    public Message(int id, int senderId, int receiverId, String content, String timestamp, boolean isRead) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Constructeur pour l'envoi (ID et Timestamp sont gérés par la DB)
    public Message(int senderId, int receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.isRead = false; // Par défaut, non lu lors de l'envoi
    }

    // Constructeur vide
    public Message() {}

    // ------------------------------------
    // Getters
    // ------------------------------------

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

    // ------------------------------------
    // Setters
    // ------------------------------------

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