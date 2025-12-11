package com.example.services_project.ui.dashboard;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.Window;
import android.util.Log;

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
    private ServicesViewModel viewModel;

    public static ApplicantsDialogFragment newInstance(int serviceId) {
        ApplicantsDialogFragment fragment = new ApplicantsDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SERVICE_ID, serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_applicants, container, false);
        recyclerView = view.findViewById(R.id.recyclerApplicants);
        emptyMessage = view.findViewById(R.id.textEmptyApplicants);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialisation du ViewModel ici pour l'utiliser dans le listener
        viewModel = new ViewModelProvider(requireActivity()).get(ServicesViewModel.class);

        // Instancier l'Adapter avec la logique ACCEPT / REJECT
        adapter = new ApplicantsAdapter(new ArrayList<>(), new ApplicantsAdapter.OnApplicantActionListener() {
            @Override
            public void onAccept(Candidate candidate) {
                Log.d("APPLICANTS_DIALOG", "Candidat accepté: " + candidate.getLastName());
                viewModel.acceptCandidate(candidate);
            }

            @Override
            public void onReject(Candidate candidate) {
                Log.d("APPLICANTS_DIALOG", "Candidat refusé: " + candidate.getLastName());
                viewModel.rejectCandidate(candidate);
            }
        });
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            serviceId = getArguments().getInt(ARG_SERVICE_ID);
        }

        if (serviceId > 0) {
            viewModel.getCandidatesLiveData(serviceId).observe(getViewLifecycleOwner(), this::updateUI);
        } else {
            Log.e("APPLICANTS_DIALOG", "ID de service invalide: " + serviceId);
            updateUI(new ArrayList<>()); // Afficher le message d'absence
        }

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
            adapter.updateList(applicants);
        }
    }
}