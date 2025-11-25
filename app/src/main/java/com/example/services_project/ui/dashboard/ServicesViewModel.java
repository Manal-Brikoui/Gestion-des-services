package com.example.services_project.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.services_project.model.Service;
import com.example.services_project.utils.UserSessionManager;

import java.util.ArrayList;
import java.util.List;

public class ServicesViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Service>> postedServices = new MutableLiveData<>(new ArrayList<>());
    private final HomeFragmentRepository repository;
    private final UserSessionManager sessionManager;

    public ServicesViewModel(@NonNull Application application) {
        super(application);
        repository = new HomeFragmentRepository(application.getApplicationContext());
        sessionManager = new UserSessionManager(application.getApplicationContext());
        loadPostedServices();
    }

    // Retourne les services observables
    public LiveData<List<Service>> getPostedServices() {
        return postedServices;
    }

    // Charger uniquement les services du user connecté
    public void loadPostedServices() {
        int userId = getCurrentUserId();
        List<Service> allServices = repository.getAllServices();
        List<Service> userServices = new ArrayList<>();

        for (Service s : allServices) {
            if (s.getUserId() == userId) { // Filtrage par utilisateur
                userServices.add(s);
            }
        }

        postedServices.setValue(userServices);
    }

    // Ajouter un service associé à l'utilisateur connecté
    public void insertService(Service service) {
        service.setUserId(getCurrentUserId());
        repository.insertService(service);
        loadPostedServices();
    }

    // Modifier un service uniquement si c'est le propriétaire
    public void updateService(Service service) {
        if (service.getUserId() == getCurrentUserId()) {
            repository.updateService(service);
            loadPostedServices();
        }
    }

    // Supprimer un service uniquement si c'est le propriétaire
    public void deleteService(Service service) {
        if (service.getUserId() == getCurrentUserId()) {
            repository.deleteService(service.getId());
            loadPostedServices();
        }
    }

    // Récupérer un service par son ID
    public Service getServiceById(int id) {
        List<Service> list = postedServices.getValue();
        if (list != null) {
            for (Service s : list) {
                if (s.getId() == id) return s;
            }
        }
        return null;
    }

    // ID de l'utilisateur connecté
    public int getCurrentUserId() {
        return sessionManager.getUserId();
    }
}
