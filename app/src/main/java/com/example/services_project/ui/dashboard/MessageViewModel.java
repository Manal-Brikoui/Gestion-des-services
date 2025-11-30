package com.example.services_project.ui.dashboard;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.services_project.model.Message;
import com.example.services_project.model.User;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * ViewModel pour gérer les données de messagerie et la liste des utilisateurs.
 * Interagit avec MessageRepository.
 */
public class MessageViewModel extends AndroidViewModel {

    private static final String TAG = "MessageViewModel";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MessageRepository repository;

    private final MutableLiveData<List<User>> usersList = new MutableLiveData<>();
    private final MutableLiveData<List<Message>> conversation = new MutableLiveData<>();

    private int currentUserId = -1;

    public MessageViewModel(@NonNull Application application) {
        super(application);
        this.repository = new MessageRepository(application);
    }

    // ----------------------------------------------------------------------
    // --- NOUVEL AJOUT : ACCÈS AU REPOSITORY (pour simulation/tests) ---
    // ----------------------------------------------------------------------

    /**
     * Permet d'accéder au Repository depuis l'Activity pour les opérations mock/simulation.
     * @return L'instance de MessageRepository.
     */
    public MessageRepository getRepository() {
        return repository;
    }

    // ----------------------------------------------------------------------
    // --- GESTION DE L'UTILISATEUR COURANT ---
    // ----------------------------------------------------------------------

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadAllUsers();
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    // ----------------------------------------------------------------------
    // --- GESTION DES UTILISATEURS (Contacts / Boîte de réception) ---
    // ----------------------------------------------------------------------

    public void loadAllUsers() {
        if (currentUserId == -1) {
            Log.e(TAG, "loadAllUsers: currentUserId non défini.");
            return;
        }

        executor.execute(() -> {
            List<User> userList = repository.getRecentConversations(currentUserId);
            usersList.postValue(userList);
        });
    }

    public LiveData<List<User>> getUsersList() {
        return usersList;
    }

    public User getUserById(int userId) {
        // Exécuté sur le thread principal car cette méthode est synchrone dans le Repository
        return repository.getUserById(userId);
    }

    // ----------------------------------------------------------------------
    // --- GESTION DES MESSAGES (Conversation) ---
    // ----------------------------------------------------------------------

    public void loadConversation(int targetUserId) {
        if (currentUserId == -1) return;

        executor.execute(() -> {
            List<Message> messages = repository.getMessages(currentUserId, targetUserId);
            conversation.postValue(messages);
        });
    }

    public LiveData<List<Message>> getConversation() {
        return conversation;
    }

    /**
     * Envoie un nouveau message et recharge la conversation.
     * @param targetUserId ID du destinataire.
     * @param content Contenu du message.
     */
    public void sendMessage(int targetUserId, String content) {
        if (currentUserId == -1 || content == null || content.trim().isEmpty()) return;

        final String finalContent = content.trim();

        executor.execute(() -> {
            Message newMessage = new Message(currentUserId, targetUserId, finalContent);

            Log.d(TAG, "Message SENT by User: " + currentUserId +
                    " to Target: " + targetUserId +
                    " Content: " + finalContent);

            boolean success = repository.sendMessage(newMessage);

            if (success) {
                // IMPORTANT : Recharger la conversation après l'envoi pour mettre à jour l'UI
                loadConversation(targetUserId);
            }
        });
    }

    // ----------------------------------------------------------------------
    // --- 🔔 NOUVEAU : GESTION DES MESSAGES NON LUS ---
    // ----------------------------------------------------------------------

    /**
     * Récupère le nombre de messages non lus envoyés par l'expéditeur (targetUserId) à l'utilisateur courant.
     * Cette méthode est appelée de manière synchrone par l'Adapter (via le Fragment).
     * @param targetUserId L'ID de l'utilisateur qui a envoyé les messages (l'expéditeur B).
     * @return Le nombre de messages non lus.
     */
    public int getUnreadMessageCount(int targetUserId) {
        if (currentUserId == -1) {
            return 0;
        }
        // L'appel au Repository (et au DatabaseHelper) est rapide et synchrone.
        return repository.getUnreadMessageCount(targetUserId, currentUserId);
    }

    /**
     * Marque tous les messages non lus de l'interlocuteur comme lus.
     * Doit être appelée lors de l'ouverture de ChatActivity.
     * @param targetUserId L'ID de l'interlocuteur (l'expéditeur B).
     */
    public void markMessagesAsRead(int targetUserId) {
        if (currentUserId == -1) return;

        executor.execute(() -> {
            // Le targetUserId est l'expéditeur (SENDER) et currentUserId est le destinataire (RECEIVER).
            int count = repository.markMessagesAsRead(targetUserId, currentUserId);

            if (count > 0) {
                Log.d(TAG, count + " messages de l'utilisateur " + targetUserId + " marqués comme lus.");

                // Recharge la liste des utilisateurs pour mettre à jour le badge du Fragment UsersList
                loadAllUsers();
            }
        });
    }
}