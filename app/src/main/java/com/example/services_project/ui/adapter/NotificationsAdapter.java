package com.example.services_project.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.services_project.R;
import com.example.services_project.model.Candidate;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private List<Candidate> notifications;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Candidate candidate);
    }

    public NotificationsAdapter(List<Candidate> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Candidate candidate = notifications.get(position);
        holder.bind(candidate, listener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateList(List<Candidate> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView textNotificationMessage;
        TextView textNotificationDate;
        // Décommenté/Ajouté pour le titre du service
        TextView textServiceTitle;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textNotificationMessage = itemView.findViewById(R.id.textNotificationMessage);
            textNotificationDate = itemView.findViewById(R.id.textNotificationDate);
            // Associer le TextView à l'ID dans item_notification.xml
            // Assurez-vous que l'ID textServiceTitle existe dans votre layout!
            textServiceTitle = itemView.findViewById(R.id.textServiceTitle);
        }

        public void bind(final Candidate candidate, final OnNotificationClickListener listener) {

            // 1. Définir le message principal
            String fullName = candidate.getFirstName() + " " + candidate.getLastName();
            textNotificationMessage.setText("Demande de service de " + fullName);

            // 2. Définir la date
            String dateTime = candidate.getDateTime();
            String dateOnly = dateTime.split(" ")[0];
            textNotificationDate.setText(dateOnly);

            // 3. Définir le titre du service (Maintenant dynamique!)
            String serviceTitle = candidate.getServiceTitle();
            if (serviceTitle != null && !serviceTitle.isEmpty()) {
                textServiceTitle.setText("Pour le service : " + serviceTitle);
                textServiceTitle.setVisibility(View.VISIBLE);
            } else {
                // Si le titre est manquant (problème de données), on le cache
                textServiceTitle.setVisibility(View.GONE);
            }

            // 4. Définir l'écouteur de clic
            itemView.setOnClickListener(v -> listener.onNotificationClick(candidate));
        }
    }
}