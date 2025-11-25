package com.example.services_project.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.services_project.model.Candidate;
import com.example.services_project.model.Service;
import com.example.services_project.utils.UserSessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicesViewModel extends AndroidViewModel {

    // LiveData des services postés par l'utilisateur courant
    private final MutableLiveData<List<Service>> postedServices = new MutableLiveData<>(new ArrayList<>());
    private final HomeFragmentRepository repository;
    private final UserSessionManager sessionManager;

    // Map pour stocker la LiveData de la liste des candidats pour chaque service ID
    private final Map<Integer, MutableLiveData<List<Candidate>>> candidatesMap = new HashMap<>();

    public ServicesViewModel(@NonNull Application application) {
        super(application);
        // Assurez-vous d'utiliser le HomeFragmentRepository corrigé (avec logs d'erreurs)
        repository = new HomeFragmentRepository(application.getApplicationContext());
        sessionManager = new UserSessionManager(application.getApplicationContext());
        loadPostedServices();
    }

    // ---------------- Services ----------------
    public LiveData<List<Service>> getPostedServices() {
        return postedServices;
    }

    public void loadPostedServices() {
        new Thread(() -> {
            int userId = getCurrentUserId();
            List<Service> allServices = repository.getAllServices();
            List<Service> userServices = new ArrayList<>();
            for (Service s : allServices) {
                if (s.getUserId() == userId) {
                    userServices.add(s);
                }
            }
            // Utilisation de postValue car nous sommes dans un Thread séparé
            postedServices.postValue(userServices);
        }).start();
    }

    public void insertService(Service service) {
        service.setUserId(getCurrentUserId());
        new Thread(() -> {
            repository.insertService(service);
            loadPostedServices(); // Mise à jour de la liste après insertion
        }).start();
    }

    public void updateService(Service service) {
        if (service.getUserId() == getCurrentUserId()) {
            new Thread(() -> {
                repository.updateService(service);
                loadPostedServices(); // Mise à jour de la liste après modification
            }).start();
        }
    }

    public void deleteService(Service service) {
        if (service.getUserId() == getCurrentUserId()) {
            new Thread(() -> {
                repository.deleteService(service.getId());
                loadPostedServices(); // Mise à jour de la liste après suppression
            }).start();
        }
    }

    public Service getServiceById(int id) {
        List<Service> list = postedServices.getValue();
        if (list != null) {
            for (Service s : list) {
                if (s.getId() == id) return s;
            }
        }
        return null;
    }

    public int getCurrentUserId() {
        return sessionManager.getUserId();
    }

    // ---------------- Candidates ----------------

    /**
     * Récupère la LiveData des candidats pour un service donné.
     * Initialise et charge si elle n'existe pas.
     */
    public LiveData<List<Candidate>> getCandidatesLiveData(int serviceId) {
        // Crée une LiveData si elle n'existe pas pour cet ID de service
        if (!candidatesMap.containsKey(serviceId)) {
            MutableLiveData<List<Candidate>> liveData = new MutableLiveData<>();
            candidatesMap.put(serviceId, liveData);
            loadCandidates(serviceId); // Charge les données initiales
        }
        return candidatesMap.get(serviceId);
    }

    /**
     * Récupère la liste des candidats depuis le Repository et met à jour la LiveData.
     * Cette méthode est essentielle pour le rafraîchissement manuel de SQLite.
     */
    private void loadCandidates(int serviceId) {
        new Thread(() -> {
            List<Candidate> list = repository.getCandidatesForService(serviceId);
            MutableLiveData<List<Candidate>> liveData = candidatesMap.get(serviceId);
            if (liveData != null) {
                // postValue car nous sommes dans un Thread d'arrière-plan
                liveData.postValue(list);
            }
        }).start();
    }

    /**
     * Ajoute un candidat et déclenche le rafraîchissement des données du service correspondant.
     * C'est le point clé pour résoudre le problème de non-mise à jour.
     */
    public void addApplicant(int serviceId, Candidate candidate) {
        candidate.setServiceId(serviceId);
        new Thread(() -> {
            // 1. Ajout du candidat dans la base de données
            repository.addCandidate(candidate);

            // 2. Rafraîchir la LiveData correspondante pour notifier le Fragment
            // C'est cette ligne qui fait le travail de mise à jour manuelle
            loadCandidates(serviceId);
        }).start();
    }
}