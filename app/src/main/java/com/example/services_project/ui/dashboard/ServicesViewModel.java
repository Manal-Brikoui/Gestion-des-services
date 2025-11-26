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

    // LiveData pour stocker les notifications (candidatures reçues par l'utilisateur)
    private final MutableLiveData<List<Candidate>> notificationsLiveData = new MutableLiveData<>(new ArrayList<>());

    private final HomeFragmentRepository repository;
    private final UserSessionManager sessionManager;

    // Map pour stocker la LiveData de la liste des candidats pour chaque service ID
    private final Map<Integer, MutableLiveData<List<Candidate>>> candidatesMap = new HashMap<>();

    public ServicesViewModel(@NonNull Application application) {
        super(application);
        repository = new HomeFragmentRepository(application.getApplicationContext());
        sessionManager = new UserSessionManager(application.getApplicationContext());
        loadPostedServices();
        loadNotifications(); // Chargement initial des notifications
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
     * Ajoute un candidat et déclenche le rafraîchissement des données du service correspondant
     * et des notifications.
     */
    public void addApplicant(int serviceId, Candidate candidate) {
        candidate.setServiceId(serviceId);
        new Thread(() -> {
            // 1. Ajout du candidat dans la base de données
            repository.addCandidate(candidate);

            // 2. Rafraîchir la LiveData correspondante pour notifier le ApplicantsDialogFragment
            loadCandidates(serviceId);

            // 3. Rafraîchir la LiveData des notifications pour mettre à jour NotificationsFragment
            loadNotifications();
        }).start();
    }

    // ---------------- Notifications (NOUVEAU) ----------------

    /**
     * Retourne la LiveData pour les notifications (candidatures reçues pour les services de l'utilisateur).
     */
    public LiveData<List<Candidate>> getNotificationsLiveData() {
        return notificationsLiveData;
    }

    /**
     * Charge la liste complète des candidatures pour tous les services de l'utilisateur.
     */
    public void loadNotifications() {
        new Thread(() -> {
            int userId = getCurrentUserId();
            // Utilise la nouvelle méthode du Repository pour obtenir toutes les candidatures pour les services de cet utilisateur.
            List<Candidate> notifications = repository.getAllCandidatesForUserServices(userId);

            // Met à jour la LiveData des notifications.
            notificationsLiveData.postValue(notifications);
        }).start();
    }
}