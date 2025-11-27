package com.example.services_project.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.services_project.data.DatabaseHelper;
import com.example.services_project.model.User;

public class UserSessionManager {

    private static final String PREF_NAME = "UserSession";

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public UserSessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ----------------------------------------------------------
    // ✔️ Sauvegarder le profil complet de l'utilisateur
    // ----------------------------------------------------------
    public void saveLoggedUser(User user) {
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_FIRST_NAME, user.getFirstName());
        editor.putString(KEY_LAST_NAME, user.getLastName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PASSWORD, user.getPassword());
        editor.apply();
    }

    // ----------------------------------------------------------
    // ✔️ Récupérer toutes les infos de l'utilisateur
    // ----------------------------------------------------------
    public User getLoggedUser() {
        int id = prefs.getInt(KEY_USER_ID, -1);
        String firstName = prefs.getString(KEY_FIRST_NAME, null);
        String lastName = prefs.getString(KEY_LAST_NAME, null);
        String email = prefs.getString(KEY_EMAIL, null);
        String password = prefs.getString(KEY_PASSWORD, null);

        if (id == -1 || email == null) {
            return null; // Aucun utilisateur connecté
        }

        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    // ----------------------------------------------------------
    // ✔️ Sauvegarder uniquement l'ID utilisateur
    // ----------------------------------------------------------
    public void saveUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId).apply();
    }

    // ----------------------------------------------------------
    // ✔️ Récupérer uniquement l’ID utilisateur
    // ----------------------------------------------------------
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    // ----------------------------------------------------------
    // ✔️ Déconnexion : vider la session
    // ----------------------------------------------------------
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    // ----------------------------------------------------------
    // 🔹 Réinitialiser le mot de passe pour l'utilisateur connecté
    // ----------------------------------------------------------
    public boolean changePasswordForLoggedUser(String newPassword, Context context) {
        User user = getLoggedUser();
        if(user == null) return false; // Aucun utilisateur connecté

        // Mise à jour dans la base de données
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        boolean success = dbHelper.updatePassword(user.getEmail(), newPassword);

        if(success){
            // Mise à jour de la session
            user.setPassword(newPassword);
            saveLoggedUser(user);
        }

        return success;
    }

}
