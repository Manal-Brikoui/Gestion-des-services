package com.example.services_project.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.services_project.R;
import com.example.services_project.model.Service;
import com.example.services_project.ui.adapter.MyServicesAdapter;

import java.util.ArrayList;

public class ServicesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyServicesAdapter adapter;
    private TextView emptyMessage;
    private ServicesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_services, container, false);

        recyclerView = root.findViewById(R.id.recyclerViewPostedServices);
        emptyMessage = root.findViewById(R.id.textEmptyMessage);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyServicesAdapter(
                requireContext(),
                new ArrayList<>(),
                this::openEditServiceForm,
                this::deleteUserService,
                service -> {
                    // Ouvrir le dialog des candidats pour ce service
                    ApplicantsDialogFragment dialog = ApplicantsDialogFragment.newInstance(service.getId());
                    dialog.show(getParentFragmentManager(), "ApplicantsDialog");
                }
        );

        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(ServicesViewModel.class);

        viewModel.getPostedServices().observe(getViewLifecycleOwner(), services -> {
            if (services != null && !services.isEmpty()) {
                adapter.updateList(services);
                recyclerView.setVisibility(View.VISIBLE);
                emptyMessage.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }

    // Ouvrir formulaire de modification
    private void openEditServiceForm(Service service) {
        if (service.getUserId() != viewModel.getCurrentUserId()) return;

        AddFragment fragment = new AddFragment();

        Bundle args = new Bundle();
        args.putBoolean("isEditMode", true);
        args.putInt("serviceId", service.getId());
        args.putInt("userId", service.getUserId()); 
        args.putString("title", service.getTitle());
        args.putString("description", service.getDescription());
        args.putString("category", service.getCategory());
        args.putString("location", service.getLocation());
        args.putString("price", service.getPrice());
        args.putString("moreDetails", service.getMoreDetails());
        args.putString("imageUri", service.getImageUri() != null ? service.getImageUri() : "");

        fragment.setArguments(args);
        fragment.show(getParentFragmentManager(), "edit_service");
    }

    // Supprimer service
    private void deleteUserService(Service service) {
        if (service.getUserId() == viewModel.getCurrentUserId()) {
            viewModel.deleteService(service);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadPostedServices();
        }
    }
}
