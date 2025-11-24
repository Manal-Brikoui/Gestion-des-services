package com.example.services_project.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.services_project.model.Service;

import java.util.ArrayList;
import java.util.List;

public class ServicesViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Service>> postedServices = new MutableLiveData<>();
    private final HomeFragmentRepository repository;

    public ServicesViewModel(@NonNull Application application) {
        super(application);
        repository = new HomeFragmentRepository(application.getApplicationContext());
        loadPostedServices();
    }

    public LiveData<List<Service>> getPostedServices() {
        return postedServices;
    }

    // Charger uniquement les services postés par l'utilisateur
    private void loadPostedServices() {
        int currentUserId = getCurrentUserId(); // ← à adapter selon ton système

        List<Service> allServices = repository.getAllServices();
        List<Service> userServices = new ArrayList<>();

        for (Service s : allServices) {
            // userId = 0 → service par défaut
            if (s.getUserId() == currentUserId) {
                userServices.add(s);
            }
        }

        postedServices.setValue(userServices);
    }

    // Méthode fictive à remplacer par ton système d’authentification
    private int getCurrentUserId() {
        // Par exemple tu peux récupérer depuis SharedPreferences ou ton user manager
        return 1; // ici juste pour test
    }
}
