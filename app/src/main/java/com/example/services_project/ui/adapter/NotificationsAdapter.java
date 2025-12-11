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
    private final int currentUserId;

    public interface OnNotificationClickListener {
        void onNotificationClick(Candidate candidate);
    }
    public NotificationsAdapter(List<Candidate> notifications, OnNotificationClickListener listener, int currentUserId) {
        this.notifications = notifications;
        this.listener = listener;
        this.currentUserId = currentUserId; //  Initialisation
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
        holder.bind(candidate, listener, currentUserId);
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
        TextView textServiceTitle;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textNotificationMessage = itemView.findViewById(R.id.textNotificationMessage);
            textNotificationDate = itemView.findViewById(R.id.textNotificationDate);
            textServiceTitle = itemView.findViewById(R.id.textServiceTitle);
        }

        public void bind(final Candidate candidate, final OnNotificationClickListener listener, int currentUserId) {

            String serviceTitle = candidate.getServiceTitle() != null ? candidate.getServiceTitle() : "Service Inconnu";
            String titleToDisplay;
            String status = candidate.getStatus();


            if (candidate.getApplicantId() == currentUserId) {
                // C'est une notification de RÉPONSE à la demande de l'utilisateur (CLIENT)
                String statusText;
                if ("ACCEPTED".equals(status)) {
                    statusText = "acceptée";
                } else if ("REJECTED".equals(status)) {
                    statusText = "refusée";
                } else {
                    statusText = "en attente";
                }

                titleToDisplay = "Votre demande pour le service " + serviceTitle + " est " + statusText;
                textServiceTitle.setText("Statut: " + statusText.toUpperCase());

            } else {

                // C'est une notification de CANDIDATURE REÇUE (OWNER)
                String fullName = candidate.getFirstName() + " " + candidate.getLastName();

                titleToDisplay = fullName + " a postulé pour votre service " + serviceTitle;

                textServiceTitle.setText("Statut: " + status);
            }

            //  Définir le message/titre personnalisé
            textNotificationMessage.setText(titleToDisplay);
            String notificationDateTime = candidate.getApplicationDate(); // MODIFICATION CLÉ

            if (notificationDateTime != null) {
                String dateOnly;
                if (notificationDateTime.contains(" ")) {
                    dateOnly = notificationDateTime.split(" ")[0];
                } else {
                    dateOnly = notificationDateTime;
                }
                textNotificationDate.setText(dateOnly);
            } else {
                textNotificationDate.setText("Date inconnue");
            }
            itemView.setOnClickListener(v -> listener.onNotificationClick(candidate));
        }
    }
}