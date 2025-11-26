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

    private final MutableLiveData<List<Service>> postedServices = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Candidate>> notificationsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final HomeFragmentRepository repository; // Assurez-vous que cette classe est accessible
    private final UserSessionManager sessionManager;
    private final Map<Integer, MutableLiveData<List<Candidate>>> candidatesMap = new HashMap<>();

    public ServicesViewModel(@NonNull Application application) {
        super(application);
        // Initialisation des dépendances
        repository = new HomeFragmentRepository(application.getApplicationContext());
        sessionManager = new UserSessionManager(application.getApplicationContext());
        loadPostedServices();
        loadNotifications();
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
        if (!candidatesMap.containsKey(serviceId)) {
            MutableLiveData<List<Candidate>> liveData = new MutableLiveData<>();
            candidatesMap.put(serviceId, liveData);
            loadCandidates(serviceId);
        }
        return candidatesMap.get(serviceId);
    }

    /**
     * Récupère la liste des candidats depuis le Repository et met à jour la LiveData.
     */
    private void loadCandidates(int serviceId) {
        new Thread(() -> {
            // Suppose que getCandidatesForService est implémenté dans HomeFragmentRepository
            List<Candidate> list = repository.getCandidatesForService(serviceId);
            MutableLiveData<List<Candidate>> liveData = candidatesMap.get(serviceId);
            if (liveData != null) {
                liveData.postValue(list);
            }
        }).start();
    }

    /**
     * Ajoute un candidat et déclenche le rafraîchissement.
     */
    public void addApplicant(int serviceId, Candidate candidate) {
        candidate.setServiceId(serviceId);
        new Thread(() -> {
            // Suppose que addCandidate est implémenté dans HomeFragmentRepository
            repository.addCandidate(candidate);

            loadCandidates(serviceId);
            loadNotifications();
        }).start();
    }

    // ---------------- Notifications (Titre du service dynamique) ----------------

    public LiveData<List<Candidate>> getNotificationsLiveData() {
        return notificationsLiveData;
    }

    /**
     * Charge la liste des candidatures et enrichit chaque Candidate avec le titre du Service associé.
     */
    public void loadNotifications() {
        new Thread(() -> {
            int userId = getCurrentUserId();
            List<Service> allServices = repository.getAllServices();
            Map<Integer, String> serviceTitlesMap = new HashMap<>();

            // 1. Créer une Map des titres de service de l'utilisateur
            for (Service s : allServices) {
                if (s.getUserId() == userId) {
                    serviceTitlesMap.put(s.getId(), s.getTitle());
                }
            }

            // 2. Récupérer les candidatures brutes
            List<Candidate> rawCandidates = repository.getAllCandidatesForUserServices(userId);
            List<Candidate> finalNotifications = new ArrayList<>();

            // 3. Fusionner (Assigner le titre du service)
            for (Candidate candidate : rawCandidates) {
                int serviceId = candidate.getServiceId();
                String title = serviceTitlesMap.get(serviceId);

                if (title != null) {
                    // Assignation du titre dynamique
                    candidate.setServiceTitle(title);
                } else {
                    candidate.setServiceTitle("Service Inconnu");
                }
                finalNotifications.add(candidate);
            }

            // 4. Mettre à jour la LiveData
            notificationsLiveData.postValue(finalNotifications);
        }).start();
    }
}