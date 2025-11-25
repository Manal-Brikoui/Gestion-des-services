package com.example.services_project.ui.dashboard;

import android.app.Dialog; // Import manquant si vous ne l'avez pas déjà
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.Window; // Import manquant si vous ne l'avez pas déjà

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.services_project.R;
import com.example.services_project.model.Candidate;
import com.example.services_project.ui.adapter.ApplicantsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ApplicantsDialogFragment extends DialogFragment {

    private static final String ARG_SERVICE_ID = "service_id";
    private int serviceId;
    private ApplicantsAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyMessage;

    public static ApplicantsDialogFragment newInstance(int serviceId) {
        ApplicantsDialogFragment fragment = new ApplicantsDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SERVICE_ID, serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    // ⭐ AJOUT DE LA MÉTHODE onStart POUR DÉFINIR LA TAILLE DU DIALOGUE ⭐
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // Définir la largeur à MATCH_PARENT pour prendre toute la largeur disponible
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // Optionnel: ajouter un padding horizontal pour laisser une petite marge sur les côtés
                // window.setBackgroundDrawableResource(android.R.color.transparent); // Si vous voulez un fond transparent pour le dialogue lui-même
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Si vous avez un background personnalisé pour votre dialogue, vous pouvez le mettre ici
        // getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View view = inflater.inflate(R.layout.fragment_applicants, container, false);
        recyclerView = view.findViewById(R.id.recyclerApplicants);
        emptyMessage = view.findViewById(R.id.textEmptyApplicants);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Instancier l'Adapter avec un listener pour Accepter / Refuser
        adapter = new ApplicantsAdapter(new ArrayList<>(), new ApplicantsAdapter.OnApplicantActionListener() {
            @Override
            public void onAccept(Candidate candidate) {
                // TODO : Logique pour accepter le candidat (ex: mettre à jour la base de données)
            }

            @Override
            public void onReject(Candidate candidate) {
                // TODO : Logique pour refuser le candidat (ex: supprimer de la base de données)
            }
        });
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            serviceId = getArguments().getInt(ARG_SERVICE_ID);
        }

        ServicesViewModel viewModel = new ViewModelProvider(requireActivity()).get(ServicesViewModel.class);

        // Observer la LiveData pour mettre à jour la liste en temps réel
        viewModel.getCandidatesLiveData(serviceId).observe(getViewLifecycleOwner(), this::updateUI);

        return view;
    }

    private void updateUI(List<Candidate> applicants) {
        if (applicants == null || applicants.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
            emptyMessage.setText("Aucun candidat pour votre service");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);
            adapter.updateList(applicants); // Met à jour la liste sans recréer l'Adapter
        }
    }
}