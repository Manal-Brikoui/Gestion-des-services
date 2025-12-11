package com.example.services_project.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Import nécessaire pour la flèche de retour
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.services_project.R;
import com.example.services_project.model.User;
import com.example.services_project.ui.adapter.UserAdapter;

import java.util.ArrayList;

public class UsersListFragment extends Fragment implements UserAdapter.OnUserClickListener {

    private static final String TAG = "UsersListFragment";

    private MessageViewModel viewModel;
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private TextView tvNoResults;
    private ImageView buttonBack; // Déclaration du bouton de retour

    private static final String PREF_AUTH_FILE = "AUTH_PREFS";
    private static final String PREF_USER_ID_KEY = "CURRENT_USER_ID";
    private static final int DEFAULT_USER_ID = -1;

    private int currentUserId = DEFAULT_USER_ID;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        // Récupération de l'ID réel de la session
        currentUserId = getCurrentUserIdFromSession(context);

        if (currentUserId != DEFAULT_USER_ID) {
            viewModel.setCurrentUserId(currentUserId);
            Log.d(TAG, "ID Utilisateur Courant chargé: " + currentUserId);
        } else {
            Toast.makeText(context, "Erreur : Session utilisateur introuvable.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "ID utilisateur non valide. Le chargement de la liste est bloqué.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_users_list, container, false);

        // Initialisation des vues
        recyclerView = root.findViewById(R.id.recyclerViewUsersList);
        searchView = root.findViewById(R.id.searchView);
        tvNoResults = root.findViewById(R.id.tvNoResults);
        buttonBack = root.findViewById(R.id.buttonBack); // Initialisation du bouton de retour

        // Configuration du bouton de retour
        setupBackButton(); // Appel de la nouvelle méthode

        // Configuration du RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        userAdapter = new UserAdapter(requireContext(), new ArrayList<>(), this);
        recyclerView.setAdapter(userAdapter);

        // Configuration de la SearchView
        setupSearchView();

        // Observer la liste filtrée des utilisateurs
        viewModel.getFilteredUsersList().observe(getViewLifecycleOwner(), users -> {
            if (users != null && !users.isEmpty()) {
                userAdapter.updateList(users);
                recyclerView.setVisibility(View.VISIBLE);
                tvNoResults.setVisibility(View.GONE);
            } else {
                userAdapter.updateList(new ArrayList<>());
                recyclerView.setVisibility(View.GONE);
                tvNoResults.setVisibility(View.VISIBLE);
            }
        });

        // Charger les utilisateurs si l'ID est valide
        if (currentUserId != DEFAULT_USER_ID) {
            viewModel.loadAllUsers();
        }

        return root;
    }

    /**
     * Configure le bouton de retour pour revenir au fragment précédent.
     */
    private void setupBackButton() {
        if (buttonBack != null) {
            buttonBack.setOnClickListener(v -> {
                // Tente de revenir à l'écran précédent dans la pile de fragments (HomeFragment)
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                } else {
                    // Optionnel: Gérer le cas où le fragment n'est pas dans la pile, par exemple
                    // en revenant à la page d'accueil par défaut si c'est la racine.
                    Log.w(TAG, "Tentative de popBackStack, mais la pile est vide.");
                }
            });
        }
    }

    /**
     * Configure la barre de recherche
     */
    private void setupSearchView() {
        if (searchView == null) return;

        searchView.setQueryHint("Rechercher par nom ou email...");
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Filtrer lors de la soumission
                viewModel.filterUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filtrer en temps réel pendant la saisie
                viewModel.filterUsers(newText);
                return true;
            }
        });

        // Écouteur pour le bouton de fermeture de la recherche
        searchView.setOnCloseListener(() -> {
            viewModel.filterUsers("");
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentUserId != DEFAULT_USER_ID) {
            Log.d(TAG, "onResume: Forcing user list reload to update unread badge.");
            viewModel.loadAllUsers();
        }
    }

    // ==================== GESTION DES CLICS ====================

    /**
     * Gère le clic sur un utilisateur pour lancer ChatActivity.
     */
    @Override
    public void onUserClick(User user) {
        if (user.getId() != currentUserId) {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("TARGET_USER_ID", user.getId());
            intent.putExtra("TARGET_USER_NAME", user.getFullName());
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "Erreur : Vous ne devriez pas vous voir ici.", Toast.LENGTH_SHORT).show();
        }
    }

    // ==================== GESTION DES MESSAGES NON LUS ====================

    /**
     * Appelé par l'Adapter pour obtenir le nombre de messages non lus de l'utilisateur cible.
     * @param targetUserId L'ID de l'utilisateur qui nous a envoyé des messages non lus.
     * @return Le nombre de messages non lus.
     */
    @Override
    public int getUnreadCount(int targetUserId) {
        if (currentUserId == DEFAULT_USER_ID) return 0;
        return viewModel.getUnreadMessageCount(targetUserId);
    }

    // ==================== GESTION DE LA SESSION ====================

    /**
     * Récupère l'ID utilisateur réel à partir de SharedPreferences.
     */
    private int getCurrentUserIdFromSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_AUTH_FILE, Context.MODE_PRIVATE);
        return prefs.getInt(PREF_USER_ID_KEY, DEFAULT_USER_ID);
    }
}