package com.example.services_project.ui.dashboard;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.services_project.R;
import com.example.services_project.model.Candidate;
import com.example.services_project.ui.adapter.NotificationsAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment implements NotificationsAdapter.OnNotificationClickListener {

    private RecyclerView recyclerView;
    private TextView emptyMessage;
    private NotificationsAdapter adapter;
    private ServicesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.recyclerNotifications);
        emptyMessage = view.findViewById(R.id.textEmptyNotifications);

        // Initialisation du ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(ServicesViewModel.class);

        //  Récupérer l'ID de l'utilisateur avant d'initialiser l'Adapter
        int currentUserId = viewModel.getCurrentUserId();

        // Initialisation du RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Passer l'ID de l'utilisateur à l'Adapter
        adapter = new NotificationsAdapter(new ArrayList<>(), this, currentUserId);

        recyclerView.setAdapter(adapter);

        viewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.loadNotifications();

        return view;
    }

    private void updateUI(List<Candidate> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);
            adapter.updateList(notifications);
        }
        Log.d("NOTIFICATIONS_FRAG", "Liste de notifications mise à jour. Nombre: " + (notifications != null ? notifications.size() : 0));
    }

    @Override
    public void onNotificationClick(Candidate candidate) {
        if (candidate.getServiceId() <= 0) {
            Log.e("NOTIFICATIONS_FRAG", "ID de service invalide pour la notification.");
            return;
        }

        int currentUserId = viewModel.getCurrentUserId();


        if (candidate.getApplicantId() == currentUserId) {

            String serviceTitle = candidate.getServiceTitle();
            String status = candidate.getStatus();
            String statusText;

            if ("ACCEPTED".equals(status)) {
                statusText = "acceptée";
            } else if ("REJECTED".equals(status)) {
                statusText = "refusée";
            } else {
                statusText = "en attente";
            }

            String toastMessage = "Votre demande pour le service "
                    + (serviceTitle != null ? serviceTitle : "Inconnu")
                    + " est " + statusText + ".";

            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_LONG).show();

        } else {


            ApplicantsDialogFragment dialog = ApplicantsDialogFragment.newInstance(candidate.getServiceId());
            dialog.show(getParentFragmentManager(), "ApplicantsDialog");
        }
    }
}