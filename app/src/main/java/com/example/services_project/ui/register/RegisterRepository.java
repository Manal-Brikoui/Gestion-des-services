package com.example.services_project.ui.register;

import android.content.Context;
import com.example.services_project.data.DatabaseHelper;
import com.example.services_project.model.User;

public class RegisterRepository {

    private final DatabaseHelper dbHelper;

    public RegisterRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // ⚠️ Méthode pour enregistrer un User complet
    public boolean insertUser(String firstName, String lastName, String email, String password) {
        return dbHelper.insertUser(firstName, lastName, email, password);
    }

    // Méthode surchargée pour recevoir un objet User
    public boolean register(User user) {
        if (user == null) return false;
        return insertUser(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword()
        );
    }

    // Vérifier si email déjà existant
    public boolean checkEmail(String email) {
        return dbHelper.getUser(email) != null;
    }

    // 🔹 Nouvelle méthode : changer le mot de passe d'un utilisateur
    public boolean changePassword(String email, String newPassword) {
        return dbHelper.updatePassword(email, newPassword);
    }
}
