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
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_services, container, false);

        recyclerView = root.findViewById(R.id.recyclerViewPostedServices);
        emptyMessage = root.findViewById(R.id.textEmptyMessage);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter avec listener pour le clic sur le crayon
        adapter = new MyServicesAdapter(requireContext(), new ArrayList<>(), service -> {
            openEditServiceForm(service);
        });
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ServicesViewModel.class);

        // Observer les services postés
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

    // Ouvre le formulaire AddFragment en mode édition
    private void openEditServiceForm(Service service) {
        AddFragment fragment = new AddFragment();

        Bundle args = new Bundle();
        args.putInt("serviceId", service.getId());
        args.putString("title", service.getTitle());
        args.putString("description", service.getDescription());
        args.putString("category", service.getCategory());
        args.putString("location", service.getLocation());
        args.putString("price", service.getPrice());
        args.putString("moreDetails", service.getMoreDetails());
        args.putString("imageUri", service.getImageUri());

        fragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)

                .addToBackStack(null)
                .commit();
    }
}
