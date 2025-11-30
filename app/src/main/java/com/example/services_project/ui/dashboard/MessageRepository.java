package com.example.services_project.ui.dashboard;

import android.content.Context;
import android.util.Log;

import com.example.services_project.data.DatabaseHelper; // Assurez-vous que l'import de DatabaseHelper est correct
import com.example.services_project.model.Message;
import com.example.services_project.model.User;

import java.util.List;

/**
 * Repository pour gérer toutes les opérations de messagerie (utilisateurs et messages)
 * en interagissant avec DatabaseHelper.
 * Cette classe agit comme un intermédiaire propre entre le ViewModel et la source de données.
 */
public class MessageRepository {

    private static final String TAG = "MessageRepository";
    private final DatabaseHelper dbHelper;

    public MessageRepository(Context context) {
        // Initialise DatabaseHelper
        this.dbHelper = new DatabaseHelper(context);
    }

    // -------------------------------------------------------------------------
    // 1. Récupérer tous les utilisateurs (pour la liste des contacts)
    // -------------------------------------------------------------------------

    /**
     * Récupère la liste de tous les utilisateurs de l'application, en excluant l'utilisateur courant.
     * @param currentUserId L'ID de l'utilisateur actuellement connecté.
     * @return List<User> de tous les autres utilisateurs.
     */
    public List<User> getAllUsers(int currentUserId) {
        // Logique pour exclure l'utilisateur courant, gérée par DatabaseHelper
        return dbHelper.getAllUsers(currentUserId);
    }

    // -------------------------------------------------------------------------
    // 2. Récupérer un utilisateur par ID
    // -------------------------------------------------------------------------

    /**
     * Récupère un utilisateur spécifique par son ID.
     * @param userId L'ID de l'utilisateur à récupérer.
     * @return L'objet User correspondant, ou null.
     */
    public User getUserById(int userId) {
        return dbHelper.getUserById(userId);
    }


    // -------------------------------------------------------------------------
    // 3. Envoyer un message (Insertion)
    // -------------------------------------------------------------------------

    /**
     * Insère un nouveau message dans la base de données.
     * @param message L'objet Message à insérer.
     * @return true si l'insertion a réussi, false sinon.
     */
    public boolean sendMessage(Message message) {
        long result = dbHelper.insertMessage(message);
        if (result == -1) {
            // Log plus détaillé en cas d'échec
            Log.e(TAG, "Erreur lors de l'envoi du message. SENDER: " + message.getSenderId() +
                    ", RECEIVER: " + message.getReceiverId() +
                    ", CONTENT: " + message.getContent());
            return false;
        }
        return true;
    }

    // -------------------------------------------------------------------------
    // 4. Récupérer la conversation entre deux utilisateurs
    // -------------------------------------------------------------------------

    /**
     * Récupère l'historique complet des messages entre deux utilisateurs.
     * C'est dans dbHelper.getMessages que la requête SQL filtre strictement
     * la conversation pour éviter de voir les messages des autres (problème B-C).
     * @param user1Id L'ID du premier utilisateur (Utilisateur Courant).
     * @param user2Id L'ID du second utilisateur (Interlocuteur).
     * @return List<Message> représentant la conversation.
     */
    public List<Message> getMessages(int user1Id, int user2Id) {
        return dbHelper.getMessages(user1Id, user2Id);
    }

    // -------------------------------------------------------------------------
    // 5. Récupérer les conversations récentes
    // -------------------------------------------------------------------------

    /**
     * Retourne la liste des utilisateurs avec qui l'utilisateur courant peut converser,
     * en utilisant la logique de filtrage du DatabaseHelper.
     * @param currentUserId L'ID de l'utilisateur connecté.
     * @return List<User> des utilisateurs pour la boîte de réception.
     */
    public List<User> getRecentConversations(int currentUserId) {
        // Cette ligne appelle dbHelper.getAllUsers(currentUserId) qui utilise WHERE id != ?
        // pour exclure l'utilisateur connecté.
        return dbHelper.getAllUsers(currentUserId);
    }
}