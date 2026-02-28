package com.example.services_project.ui.login;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import com.example.services_project.model.User;

public class LoginViewModel extends ViewModel {

    private LoginRepository repository;

    // Initialisation du repository
    public void init(Context context) {
        if (repository == null) {   // Évite la réinitialisation
            repository = new LoginRepository(context);
        }
    }

    // Connexion
    public boolean login(String email, String password) {
        return repository.login(email, password);
    }

    // Récupérer l'utilisateur complet par email
    public User getUser(String email) {
        return repository.getUser(email);
    }

    // Modifier le mot de passe de l'utilisateur correspondant à l'email
    public boolean changePassword(String email, String newPassword) {
        return repository.changePassword(email, newPassword);
    }
}
