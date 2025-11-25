package com.example.services_project.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences prefs;

    public UserSessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Sauvegarder l'ID du user connecté
    public void saveUserId(int userId) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply();
    }

    // Obtenir l'ID du user connecté
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }
}
