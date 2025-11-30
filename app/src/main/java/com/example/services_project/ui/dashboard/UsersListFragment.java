package com.example.services_project.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.services_project.R;
import com.example.services_project.model.User;
import com.example.services_project.ui.adapter.UserAdapter; // Nécessite cette classe pour fonctionner

import java.util.ArrayList;

/**
 * Fragment pour afficher la liste de tous les utilisateurs disponibles pour la messagerie.
 * Implémente l'interface OnUserClickListener pour gérer la navigation vers ChatActivity.
 */
public class UsersListFragment extends Fragment implements UserAdapter.OnUserClickListener {

    private static final String TAG = "UsersListFragment";

    private MessageViewModel viewModel;
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;

    // --- Constantes de Session (Alignées avec UserSessionManager) ---
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

        // Définir l'ID dans le ViewModel (pour le filtrage dans la DB)
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

        recyclerView = root.findViewById(R.id.recyclerViewUsersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // L'adaptateur est correctement initialisé ici
        userAdapter = new UserAdapter(requireContext(), new ArrayList<>(), this);
        recyclerView.setAdapter(userAdapter);

        // L'Observer met à jour l'adaptateur
        viewModel.getUsersList().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                userAdapter.updateList(users);
            }
        });

        // Charger la liste des utilisateurs UNIQUEMENT si l'ID est valide
        if (currentUserId != DEFAULT_USER_ID) {
            viewModel.loadAllUsers();
        }

        return root;
    }

    @Override
    public void onUserClick(User user) {
        // Redirection vers l'activité de chat
        if (user.getId() != currentUserId) {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("TARGET_USER_ID", user.getId());
            intent.putExtra("TARGET_USER_NAME", user.getFullName());
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "Erreur : Vous ne devriez pas vous voir ici.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Récupère l'ID utilisateur réel à partir de SharedPreferences.
     */
    private int getCurrentUserIdFromSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_AUTH_FILE, Context.MODE_PRIVATE);
        return prefs.getInt(PREF_USER_ID_KEY, DEFAULT_USER_ID);
    }
}