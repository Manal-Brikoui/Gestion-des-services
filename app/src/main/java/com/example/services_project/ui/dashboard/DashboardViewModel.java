package com.example.services_project.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private final DashboardRepository repository;
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public DashboardViewModel() {
        repository = new DashboardRepository();
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void fetchMessage() {
        message.setValue(repository.getMessage());
    }
}
