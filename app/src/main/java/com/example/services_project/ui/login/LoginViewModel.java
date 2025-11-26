package com.example.services_project.ui.login;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import com.example.services_project.model.User;

public class LoginViewModel extends ViewModel {
    private LoginRepository repository;

    public void init(Context context){
        repository = new LoginRepository(context);
    }

    // Connexion
    public boolean login(String email, String password){
        return repository.login(email, password);
    }

    // Récupérer l'utilisateur complet pour la session
    public User getUser(String email){
        return repository.getUser(email);
    }
}
