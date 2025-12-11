package com.example.services_project.ui.dashboard;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.services_project.model.Message;
import com.example.services_project.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class MessageViewModel extends AndroidViewModel {

    private static final String TAG = "MessageViewModel";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MessageRepository repository;

    private final MutableLiveData<List<User>> usersList = new MutableLiveData<>();
    private final MutableLiveData<List<User>> filteredUsersList = new MutableLiveData<>();
    private final MutableLiveData<List<Message>> conversation = new MutableLiveData<>();

    // Cache de tous les utilisateurs pour le filtrage
    private List<User> allUsersCached = new ArrayList<>();

    private int currentUserId = -1;

    public MessageViewModel(@NonNull Application application) {
        super(application);
        this.repository = new MessageRepository(application);
    }

    public MessageRepository getRepository() {
        return repository;
    }

    // ==================== GESTION DE L'UTILISATEUR COURANT ====================

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadAllUsers();
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    // ==================== CHARGEMENT DES UTILISATEURS ====================

    public void loadAllUsers() {
        if (currentUserId == -1) {
            Log.e(TAG, "loadAllUsers: currentUserId non défini.");
            return;
        }

        executor.execute(() -> {
            List<User> userList = repository.getRecentConversations(currentUserId);

            // Mise à jour du cache
            allUsersCached = new ArrayList<>(userList);

            // Mise à jour des LiveData
            usersList.postValue(userList);
            filteredUsersList.postValue(userList);
        });
    }

    public LiveData<List<User>> getUsersList() {
        return usersList;
    }

    // ==================== FILTRAGE DES UTILISATEURS ====================

    /**
     * Retourne la liste filtrée des utilisateurs
     */
    public LiveData<List<User>> getFilteredUsersList() {
        return filteredUsersList;
    }

    /**
     * Filtre les utilisateurs en fonction d'une requête de recherche
     * @param query Le texte de recherche (nom ou email)
     */
    public void filterUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Si la recherche est vide, afficher tous les utilisateurs
            filteredUsersList.postValue(allUsersCached);
            return;
        }

        String lowerCaseQuery = query.toLowerCase().trim();

        List<User> filtered = new ArrayList<>();
        for (User user : allUsersCached) {
            // Filtrer par nom complet ou email
            String fullName = user.getFullName() != null ? user.getFullName().toLowerCase() : "";
            String email = user.getEmail() != null ? user.getEmail().toLowerCase() : "";

            if (fullName.contains(lowerCaseQuery) || email.contains(lowerCaseQuery)) {
                filtered.add(user);
            }
        }

        filteredUsersList.postValue(filtered);
    }

    // ==================== GESTION DES UTILISATEURS ====================

    public User getUserById(int userId) {
        return repository.getUserById(userId);
    }

    // ==================== GESTION DES CONVERSATIONS ====================

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

    // ==================== ENVOI DE MESSAGES ====================

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
                loadConversation(targetUserId);
            }
        });
    }

    // ==================== GESTION DES MESSAGES NON LUS ====================

    public int getUnreadMessageCount(int targetUserId) {
        if (currentUserId == -1) {
            return 0;
        }
        return repository.getUnreadMessageCount(targetUserId, currentUserId);
    }

    public void markMessagesAsRead(int targetUserId) {
        if (currentUserId == -1) return;

        executor.execute(() -> {
            int count = repository.markMessagesAsRead(targetUserId, currentUserId);

            if (count > 0) {
                Log.d(TAG, count + " messages de l'utilisateur " + targetUserId + " marqués comme lus.");

                // Recharge la liste des utilisateurs pour mettre à jour le badge
                loadAllUsers();
            }
        });
    }
}