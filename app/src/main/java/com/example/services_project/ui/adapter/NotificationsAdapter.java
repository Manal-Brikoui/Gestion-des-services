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

// TextView textServiceTitle; // Si le modèle Candidate est étendu pour inclure le titre



        public NotificationViewHolder(@NonNull View itemView) {

            super(itemView);

            textNotificationMessage = itemView.findViewById(R.id.textNotificationMessage);

            textNotificationDate = itemView.findViewById(R.id.textNotificationDate);

// textServiceTitle = itemView.findViewById(R.id.textServiceTitle);

        }



        public void bind(final Candidate candidate, final OnNotificationClickListener listener) {

// Le message: "Voir la demande de service de [Prénom Nom]"

            String fullName = candidate.getFirstName() + " " + candidate.getLastName();

            textNotificationMessage.setText("Demande de service de " + fullName);



// La date est dans le champ dateTime, on doit extraire seulement la date

            String dateTime = candidate.getDateTime();

            String dateOnly = dateTime.split(" ")[0]; // Prend la partie avant l'heure

            textNotificationDate.setText(dateOnly);



// textServiceTitle.setText("pour votre service : " + candidate.getServiceTitle());

// ^^^ NÉCESSITE DE MODIFIER LE MODÈLE CANDIDATE POUR AVOIR serviceTitle ^^^



            itemView.setOnClickListener(v -> listener.onNotificationClick(candidate));

        }

    }

}