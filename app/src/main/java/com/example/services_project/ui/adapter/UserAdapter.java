package com.example.services_project.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.services_project.R;
import com.example.services_project.model.User;

import java.util.List;

/**
 * Adapter pour afficher la liste des objets User dans un RecyclerView.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private List<User> usersList;
    private final OnUserClickListener listener;

    /**
     * Interface pour gérer les clics sur les éléments de la liste.
     */
    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UserAdapter(Context context, List<User> usersList, OnUserClickListener listener) {
        this.context = context;
        this.usersList = usersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate le layout pour un élément de la liste (item_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = usersList.get(position);

        // 1. Définir le nom complet
        holder.textUserName.setText(user.getFullName());

        // 2. Définir l'initiale pour l'avatar (méthode ajoutée à User.java)
        holder.textInitial.setText(user.getInitial());

        // 3. Gérer le clic
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    /**
     * Méthode utilitaire pour mettre à jour la liste des utilisateurs.
     * @param newList Nouvelle liste d'utilisateurs.
     */
    public void updateList(List<User> newList) {
        usersList = newList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder qui contient les vues de chaque élément (item_user.xml).
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textInitial;
        TextView textUserName;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // Assurez-vous que ces IDs correspondent à ceux de item_user.xml
            textInitial = itemView.findViewById(R.id.textInitial);
            textUserName = itemView.findViewById(R.id.textUserName);
        }
    }
}