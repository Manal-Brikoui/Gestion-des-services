package com.example.services_project.ui.login;

import android.content.Context;

import com.example.services_project.data.DatabaseHelper;
import com.example.services_project.model.User;

public class LoginRepository {

    private final DatabaseHelper dbHelper;

    public LoginRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    //  Vérifie si l'email et le mot de passe sont corrects
    public boolean login(String email, String password) {
        return dbHelper.checkUser(email, password);
    }

    //
    //  Récupérer un utilisateur complet via email
    public User getUser(String email) {
        return dbHelper.getUser(email);
    }

    //  Vérifier si un email existe

    public boolean emailExists(String email) {
        return dbHelper.isEmailExists(email);
    }

    // Changer mot de passe d’un utilisateur (Forgot Password)
    public boolean changePassword(String email, String newPassword) {
        return dbHelper.updatePassword(email, newPassword);
    }
}
