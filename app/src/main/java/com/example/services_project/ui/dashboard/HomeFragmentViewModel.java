package com.example.services_project.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.services_project.model.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragmentViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Service>> services = new MutableLiveData<>();
    private final HomeFragmentRepository repository;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public HomeFragmentViewModel(@NonNull Application application) {
        super(application);
        repository = new HomeFragmentRepository(application);
        loadServices();
    }

    // CHARGER LES SERVICES SANS BLOQUER L’UI
    private void loadServices() {
        executor.execute(() -> {
            List<Service> list = repository.getAllServices();
            services.postValue(list); // postValue = thread safe
        });
    }

    public LiveData<List<Service>> getServices() {
        return services;
    }


    public void insertService(Service service) {
        executor.execute(() -> {
            repository.insertService(service);
            List<Service> list = repository.getAllServices();
            services.postValue(list);
        });
    }
}
