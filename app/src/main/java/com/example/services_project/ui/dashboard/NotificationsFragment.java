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



// Initialisation du RecyclerView

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NotificationsAdapter(new ArrayList<>(), this);

        recyclerView.setAdapter(adapter);



// Initialisation du ViewModel

        viewModel = new ViewModelProvider(requireActivity()).get(ServicesViewModel.class);



// Observer les notifications

        viewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(), this::updateUI);



// S'assurer que les notifications sont chargées lors de l'ouverture du fragment

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



// Gère le clic sur une notification

    @Override

    public void onNotificationClick(Candidate candidate) {

// Lorsque l'utilisateur clique, ouvrir ApplicantsDialogFragment pour ce service ID

        if (candidate.getServiceId() > 0) {

            ApplicantsDialogFragment dialog = ApplicantsDialogFragment.newInstance(candidate.getServiceId());

            dialog.show(getParentFragmentManager(), "ApplicantsDialog");

        } else {

            Log.e("NOTIFICATIONS_FRAG", "ID de service invalide pour la notification.");

        }

    }

}