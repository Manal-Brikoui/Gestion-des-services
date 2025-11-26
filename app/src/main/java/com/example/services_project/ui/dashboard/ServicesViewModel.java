package com.example.services_project.ui.dashboard;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.services_project.model.Candidate;
import com.example.services_project.model.Service;
import com.example.services_project.utils.UserSessionManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicesViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Service>> postedServices = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Candidate>> notificationsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final HomeFragmentRepository repository;
    private final UserSessionManager sessionManager;
    private final Map<Integer, MutableLiveData<List<Candidate>>> candidatesMap = new HashMap<>();

    public ServicesViewModel(@NonNull Application application) {
        super(application);
        repository = new HomeFragmentRepository(application.getApplicationContext());
        sessionManager = new UserSessionManager(application.getApplicationContext());
        loadPostedServices();
        loadNotifications();
    }

    // ---------------- SERVICES & USER ----------------

    public LiveData<List<Service>> getPostedServices() {
        return postedServices;
    }

    public int getCurrentUserId() {
        return sessionManager.getUserId();
    }

    public void loadPostedServices() {
        new Thread(() -> {
            int userId = getCurrentUserId();
            List<Service> services = repository.getServicesByUser(userId);
            postedServices.postValue(services);
        }).start();
    }

    /** * ✅ CORRIGÉ : Méthode pour insérer un nouveau service.
     * Fixe le userId avant l'insertion pour lier le service à l'utilisateur actuel.
     */
    public void insertService(Service service) {
        // ⚠️ CORRECTION CRITIQUE : Assurer que le service est lié à l'utilisateur connecté
        int currentUserId = getCurrentUserId();
        service.setUserId(currentUserId);

        new Thread(() -> {
            Log.d("VIEW_MODEL", "Insertion du service pour User ID: " + currentUserId);
            repository.insertService(service);
            loadPostedServices(); // Rafraîchit l'affichage
        }).start();
    }

    /** * ✅ AJOUTÉ : Méthode pour modifier un service existant.
     */
    public void updateService(Service service) {
        new Thread(() -> {
            repository.updateService(service);
            loadPostedServices(); // Rafraîchit l'affichage
        }).start();
    }

    public void deleteService(Service service) {
        if (service == null || service.getId() <= 0) {
            Log.e("VIEW_MODEL", "Impossible de supprimer le service : ID invalide.");
            return;
        }

        new Thread(() -> {
            repository.deleteService(service.getId());
            loadPostedServices();
        }).start();
    }

    // ---------------- Candidates ----------------

    public LiveData<List<Candidate>> getCandidatesLiveData(int serviceId) {
        if (!candidatesMap.containsKey(serviceId)) {
            MutableLiveData<List<Candidate>> liveData = new MutableLiveData<>();
            candidatesMap.put(serviceId, liveData);
            loadCandidates(serviceId);
        }
        return candidatesMap.get(serviceId);
    }

    private void loadCandidates(int serviceId) {
        new Thread(() -> {
            List<Candidate> list = repository.getCandidatesForService(serviceId);
            MutableLiveData<List<Candidate>> liveData = candidatesMap.get(serviceId);
            if (liveData != null) {
                liveData.postValue(list);
            }
        }).start();
    }

    public void addApplicant(int serviceId, Candidate candidate) {
        candidate.setServiceId(serviceId);
        candidate.setApplicantId(getCurrentUserId());

        new Thread(() -> {
            repository.addCandidate(candidate);
            loadCandidates(serviceId);
            loadNotifications();
        }).start();
    }

    public void acceptCandidate(Candidate candidate) {
        updateCandidateStatus(candidate, "ACCEPTED");
    }

    public void rejectCandidate(Candidate candidate) {
        updateCandidateStatus(candidate, "REJECTED");
    }

    private void updateCandidateStatus(Candidate candidate, String status) {
        if (candidate.getId() <= 0) {
            Log.e("VIEW_MODEL", "Impossible de mettre à jour le candidat : ID invalide.");
            return;
        }

        new Thread(() -> {
            repository.updateCandidateStatus(candidate.getId(), status);
            loadCandidates(candidate.getServiceId());
            loadNotifications();
        }).start();
    }

    // ---------------- Notifications ----------------

    public LiveData<List<Candidate>> getNotificationsLiveData() {
        return notificationsLiveData;
    }

    public void loadNotifications() {
        new Thread(() -> {
            int userId = getCurrentUserId();
            List<Service> allServices = repository.getAllServices();
            Map<Integer, String> serviceTitlesMap = new HashMap<>();

            for (Service s : allServices) {
                serviceTitlesMap.put(s.getId(), s.getTitle());
            }

            List<Candidate> finalNotifications = new ArrayList<>();

            // A. Notifications REÇUES (Prestataire/Owner)
            List<Candidate> receivedCandidates = repository.getAllCandidatesForUserServices(userId);

            for (Candidate candidate : receivedCandidates) {
                String title = serviceTitlesMap.get(candidate.getServiceId());
                candidate.setServiceTitle(title != null ? title : "Service Inconnu");
                finalNotifications.add(candidate);
            }

            // B. Notifications de RÉPONSE (Candidat/Client)
            List<Candidate> postedCandidates = repository.getCandidatesPostedByUser(userId);

            for (Candidate candidate : postedCandidates) {
                if ("ACCEPTED".equals(candidate.getStatus()) || "REJECTED".equals(candidate.getStatus())) {
                    String title = serviceTitlesMap.get(candidate.getServiceId());
                    candidate.setServiceTitle(title != null ? title : "Réponse pour service ID " + candidate.getServiceId());
                    finalNotifications.add(candidate);
                }
            }

            notificationsLiveData.postValue(finalNotifications);

        }).start();
    }
}