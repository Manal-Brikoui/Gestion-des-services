package com.example.services_project.ui.login;

import android.content.Context;
import com.example.services_project.data.DatabaseHelper;
import com.example.services_project.model.User;

public class LoginRepository {
    private final DatabaseHelper dbHelper;

    public LoginRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // 🔹 Vérifie si l'email et le mot de passe correspondent
    public boolean login(String email, String password) {
        return dbHelper.checkUser(email, password);
    }

    // 🔹 Récupérer l'utilisateur complet par email
    public User getUser(String email) {
        return dbHelper.getUser(email);
    }

    // 🔹 Mettre à jour le mot de passe pour un utilisateur donné
    public boolean changePassword(String email, String newPassword) {
        return dbHelper.updatePassword(email, newPassword);
    }
}
