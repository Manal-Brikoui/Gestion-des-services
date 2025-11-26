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
    private final int currentUserId; // 👈 AJOUTÉ : ID de l'utilisateur connecté

    public interface OnNotificationClickListener {
        void onNotificationClick(Candidate candidate);
    }

    // ⚠️ CORRECTION : Constructeur mis à jour pour accepter l'ID de l'utilisateur
    public NotificationsAdapter(List<Candidate> notifications, OnNotificationClickListener listener, int currentUserId) {
        this.notifications = notifications;
        this.listener = listener;
        this.currentUserId = currentUserId; // 👈 Initialisation
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

        // ⚠️ CORRECTION : Passer l'ID de l'utilisateur pour la logique de personnalisation
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

        // textNotificationMessage contiendra le titre dynamique (Client/Owner)
        TextView textNotificationMessage;
        TextView textNotificationDate;
        // Ce TextView est utile pour afficher des détails supplémentaires ou le statut
        TextView textServiceTitle;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textNotificationMessage = itemView.findViewById(R.id.textNotificationMessage);
            textNotificationDate = itemView.findViewById(R.id.textNotificationDate);
            // Si vous avez un champ séparé pour le titre/statut, utilisez-le
            textServiceTitle = itemView.findViewById(R.id.textServiceTitle);
            // Si le statut est affiché ailleurs, ajustez l'ID ici
        }

        // ⚠️ CORRECTION : bind mis à jour pour recevoir l'ID de l'utilisateur
        public void bind(final Candidate candidate, final OnNotificationClickListener listener, int currentUserId) {

            String serviceTitle = candidate.getServiceTitle() != null ? candidate.getServiceTitle() : "Service Inconnu";
            String titleToDisplay;
            String status = candidate.getStatus();

            // ----------------------------------------------------
            // ⭐️ LOGIQUE DE PERSONNALISATION DU TITRE (INCHANGÉE)
            // ----------------------------------------------------
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

                // Format Client : "Votre demande de service [Nom] est [Statut]"
                titleToDisplay = "Votre demande pour le service " + serviceTitle + " est " + statusText;

                // Afficher le statut dans un champ séparé si possible
                textServiceTitle.setText("Statut: " + statusText.toUpperCase());

            } else {

                // C'est une notification de CANDIDATURE REÇUE (OWNER)
                String fullName = candidate.getFirstName() + " " + candidate.getLastName();

                // Format Owner : "[Nom du Candidat] a postulé pour votre service [Nom]"
                titleToDisplay = fullName + " a postulé pour votre service " + serviceTitle;

                // Afficher le statut (qui est souvent PENDING ici)
                textServiceTitle.setText("Statut: " + status);
            }

            // 1. Définir le message/titre personnalisé
            textNotificationMessage.setText(titleToDisplay);

            // 2. Définir la date (FIX pour la date de notification)
            // Nous utilisons la nouvelle colonne 'applicationDate' qui sera mise à jour
            // lors de la postulation, l'acceptation ou le refus.
            String notificationDateTime = candidate.getApplicationDate(); // 👈 MODIFICATION CLÉ

            if (notificationDateTime != null) {
                // Prend seulement la date (et potentiellement l'heure si vous voulez)
                // Le format est probablement 'YYYY-MM-DD HH:MM:SS' de SQLite (DATETIME('now'))
                String dateOnly;
                if (notificationDateTime.contains(" ")) {
                    dateOnly = notificationDateTime.split(" ")[0];
                } else {
                    dateOnly = notificationDateTime; // Si l'heure n'est pas présente
                }
                textNotificationDate.setText(dateOnly);
            } else {
                // Fallback si la date est nulle (ne devrait pas arriver avec DEFAULT('now') dans la DB)
                textNotificationDate.setText("Date inconnue");
            }


            // 3. Définir l'écouteur de clic
            itemView.setOnClickListener(v -> listener.onNotificationClick(candidate));
        }
    }
}