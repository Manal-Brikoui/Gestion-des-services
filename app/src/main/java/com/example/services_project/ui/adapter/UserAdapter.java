package com.example.services_project.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast; // Import ajouté pour le log de débogage si nécessaire

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
     * Interface pour gérer les clics sur les éléments de la liste et
     * pour demander le nombre de messages non lus (pour le badge).
     */
    public interface OnUserClickListener {
        void onUserClick(User user);

        /**
         * Demande le compte des messages non lus envoyés par l'utilisateur cible au Fragment.
         * @param targetUserId L'ID de l'utilisateur dans la liste (l'expéditeur).
         * @return Le nombre de messages non lus.
         */
        int getUnreadCount(int targetUserId);
    }

    public UserAdapter(Context context, List<User> usersList, OnUserClickListener listener) {
        this.context = context;
        this.usersList = usersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Assurez-vous d'avoir créé R.layout.item_user
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = usersList.get(position);

        // 1. Définir le nom complet
        holder.textUserName.setText(user.getFullName());

        // 2. Définir l'initiale pour l'avatar
        holder.textInitial.setText(user.getInitial());

        // 3. 🔔 GESTION DU BADGE DE MESSAGES NON LUS (NOUVEAU)
        if (listener != null) {
            // Appelle la méthode dans UsersListFragment qui va interroger le ViewModel
            int unreadCount = listener.getUnreadCount(user.getId());

            if (unreadCount > 0) {
                holder.textUnreadCount.setText(String.valueOf(unreadCount));
                holder.textUnreadCount.setVisibility(View.VISIBLE); // Afficher le badge
            } else {
                holder.textUnreadCount.setVisibility(View.GONE); // Cacher le badge
            }
        } else {
            // Sécurité si le listener n'est pas défini
            holder.textUnreadCount.setVisibility(View.GONE);
        }

        // 4. Gérer le clic
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
        TextView textUnreadCount; // <-- NOUVEAU : Le TextView pour le badge

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textInitial = itemView.findViewById(R.id.textInitial);
            textUserName = itemView.findViewById(R.id.textUserName);
            // 🎯 NOUVEL ID : Assurez-vous que R.id.textUnreadCount existe dans item_user.xml
            textUnreadCount = itemView.findViewById(R.id.textUnreadCount);
        }
    }
}