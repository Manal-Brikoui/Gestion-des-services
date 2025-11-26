package com.example.services_project.ui.register;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import com.example.services_project.model.User;

public class RegisterViewModel extends ViewModel {

    private RegisterRepository repository;

    // Initialisation du repository
    public void init(Context context) {
        repository = new RegisterRepository(context);
    }

    // Méthode pour enregistrer un utilisateur complet
    public boolean register(User user) {
        if (user == null) return false;

        // Appel au repository pour insérer le user dans la DB
        return repository.insertUser(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword()
        );
    }
}
