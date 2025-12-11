package com.example.services_project.ui.dashboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.services_project.R;
import com.example.services_project.model.Service;
import com.example.services_project.ui.adapter.ServiceAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeFragmentViewModel viewModel;
    private ServiceAdapter adapter;

    private EditText searchBar;
    private ImageView filterIcon;
    private ImageView messageIcon;

    private final List<Service> allServices = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewServices);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchBar = root.findViewById(R.id.searchBar);
        filterIcon = root.findViewById(R.id.filterIcon);
        messageIcon = root.findViewById(R.id.messageIcon);

        // INIT ADAPTER (vide)
        adapter = new ServiceAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // INIT VIEWMODEL
        viewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);

        // OBSERVER DATABASE
        viewModel.getServices().observe(getViewLifecycleOwner(), services -> {
            if (services == null) return;

            allServices.clear();
            allServices.addAll(services);

            adapter.updateList(allServices);
        });

        // BARRE DE RECHERCHE
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterByText(s.toString());
            }
        });

        // FILTRE PAR CATÉGORIE
        filterIcon.setOnClickListener(v -> showFilterDialog());

        //  BOUTON MESSAGERIE
        messageIcon.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.dashboard_container, new UsersListFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return root;
    }

    // FILTRAGE PAR MOT-CLÉ
    private void filterByText(String query) {
        if (query == null) query = "";

        String searchLower = query.toLowerCase();

        List<Service> filtered = new ArrayList<>();

        for (Service s : allServices) {
            if (s.getTitle().toLowerCase().contains(searchLower) ||
                    s.getCategory().toLowerCase().contains(searchLower) ||
                    s.getDescription().toLowerCase().contains(searchLower)) {

                filtered.add(s);
            }
        }

        adapter.updateList(filtered);
    }

    // FILTRAGE PAR CATÉGORIE (
    private void showFilterDialog() {

        final String[] categories = {
                "TOUS", "COIFFURE", "PLOMBERIE", "MASSAGE",
                "ÉLECTRICIEN", "PÉDIATRIE", "INFORMATIQUE",
                "DESIGN", "CUISINE"
        };

        new AlertDialog.Builder(requireContext())
                .setTitle("Filtrer par catégorie")
                .setItems(categories, (dialog, index) -> {

                    String selected = categories[index];

                    if (selected.equals("TOUS")) {
                        adapter.updateList(allServices);
                        return;
                    }

                    List<Service> filtered = new ArrayList<>();
                    for (Service s : allServices) {
                        if (s.getCategory().equalsIgnoreCase(selected)) {
                            filtered.add(s);
                        }
                    }

                    adapter.updateList(filtered);
                })
                .show();
    }
}