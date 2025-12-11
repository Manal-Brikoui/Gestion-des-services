package com.example.services_project.ui.dashboard;

import android.content.Context;
import android.util.Log;

import com.example.services_project.data.DatabaseHelper;
import com.example.services_project.model.Message;
import com.example.services_project.model.User;

import java.util.List;

public class MessageRepository {

    private static final String TAG = "MessageRepository";
    private final DatabaseHelper dbHelper;

    public MessageRepository(Context context) {
        // Initialise DatabaseHelper
        this.dbHelper = new DatabaseHelper(context);
    }

    public List<User> getAllUsers(int currentUserId) {
        return dbHelper.getAllUsers(currentUserId);
    }

    // Récupérer un utilisateur par ID
    public User getUserById(int userId) {
        return dbHelper.getUserById(userId);
    }

    //  Envoyer un message
    public boolean sendMessage(Message message) {
        long result = dbHelper.insertMessage(message);
        if (result == -1) {
            Log.e(TAG, "Erreur lors de l'envoi du message. SENDER: " + message.getSenderId() +
                    ", RECEIVER: " + message.getReceiverId() +
                    ", CONTENT: " + message.getContent());
            return false;
        }
        return true;
    }

    //Récupérer la conversation entre deux utilisateurs
    public List<Message> getMessages(int user1Id, int user2Id) {
        return dbHelper.getMessages(user1Id, user2Id);
    }

    //  Récupérer les conversations récentes

    public List<User> getRecentConversations(int currentUserId) {
        return dbHelper.getAllUsers(currentUserId);
    }
    // GESTION DES MESSAGES NON LUS
    public int getUnreadMessageCount(int senderId, int receiverId) {
        return dbHelper.getUnreadMessageCount(senderId, receiverId);
    }
    public int markMessagesAsRead(int senderId, int receiverId) {
        return dbHelper.markMessagesAsRead(senderId, receiverId);
    }
}