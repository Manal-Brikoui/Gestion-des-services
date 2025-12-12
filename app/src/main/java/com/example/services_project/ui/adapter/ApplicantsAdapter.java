package com.example.services_project.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.services_project.R;
import com.example.services_project.model.Candidate;
import java.util.List;

public class ApplicantsAdapter extends RecyclerView.Adapter<ApplicantsAdapter.CandidateViewHolder> {

    private List<Candidate> candidateList;
    private final OnApplicantActionListener listener;

    public interface OnApplicantActionListener {
        void onAccept(Candidate candidate);
        void onReject(Candidate candidate);
    }

    public ApplicantsAdapter(List<Candidate> candidateList, OnApplicantActionListener listener) {
        this.candidateList = candidateList;
        this.listener = listener;
    }

    public void updateList(List<Candidate> newList) {
        this.candidateList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applicant, parent, false);
        return new CandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {
        Candidate candidate = candidateList.get(position);

        // Nom complet
        String firstName = candidate.getFirstName() != null ? candidate.getFirstName() : "";
        String lastName = candidate.getLastName() != null ? candidate.getLastName() : "";
        String fullName = String.format("%s %s", firstName, lastName).trim();
        holder.name.setText(fullName);

        // Initiale
        String initial = !fullName.isEmpty() ? fullName.substring(0, 1).toUpperCase() : "?";
        holder.initial.setText(initial);

        // Détails
        holder.dateTime.setText(candidate.getDateTime());
        holder.location.setText(candidate.getLocation());
        holder.phone.setText(candidate.getPhone());
        holder.email.setText(candidate.getEmail());

        // Statut
        String status = candidate.getStatus() != null ? candidate.getStatus() : "PENDING";
        holder.status.setText(getStatusText(status));
        updateStatusBadge(holder.status, status);

        // Gestion de l'expansion
        boolean isExpanded = candidate.isExpanded();
        holder.layoutDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.imgArrow.setRotation(isExpanded ? 180 : 0);

        // Clic pour expand/collapse
        holder.layoutCompact.setOnClickListener(v -> {
            candidate.setExpanded(!candidate.isExpanded());
            notifyItemChanged(position);
        });

        // Boutons selon le statut
        if ("PENDING".equalsIgnoreCase(status)) {
            holder.layoutButtons.setVisibility(View.VISIBLE);
            holder.btnAccept.setOnClickListener(v -> listener.onAccept(candidate));
            holder.btnReject.setOnClickListener(v -> listener.onReject(candidate));
        } else {
            holder.layoutButtons.setVisibility(View.GONE);
        }
    }

    private String getStatusText(String status) {
        if ("ACCEPTED".equalsIgnoreCase(status)) return "Accepté";
        if ("REJECTED".equalsIgnoreCase(status)) return "Refusé";
        return "En attente";
    }

    private void updateStatusBadge(TextView statusView, String status) {
        if ("ACCEPTED".equalsIgnoreCase(status)) {
            statusView.setTextColor(0xFF2E7D32); // Vert foncé
            statusView.setBackgroundResource(R.drawable.button_accept);
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            statusView.setTextColor(0xFFC62828); // Rouge foncé
            statusView.setBackgroundResource(R.drawable.button_reject);
        } else {
            statusView.setTextColor(0xFFF57C00); // Orange
            statusView.setBackgroundResource(R.drawable.status_badge_pending);
        }
    }

    @Override
    public int getItemCount() {
        return candidateList != null ? candidateList.size() : 0;
    }

    public static class CandidateViewHolder extends RecyclerView.ViewHolder {
        CardView cardApplicant;
        LinearLayout layoutCompact, layoutDetails, layoutButtons;
        TextView name, dateTime, location, phone, email, status, initial;
        ImageView imgArrow;
        Button btnAccept, btnReject;

        public CandidateViewHolder(View itemView) {
            super(itemView);

            cardApplicant = itemView.findViewById(R.id.cardApplicant);
            layoutCompact = itemView.findViewById(R.id.layoutCompact);
            layoutDetails = itemView.findViewById(R.id.layoutDetails);
            layoutButtons = itemView.findViewById(R.id.layoutButtons);

            initial = itemView.findViewById(R.id.textApplicantInitial);
            name = itemView.findViewById(R.id.textApplicantName);
            imgArrow = itemView.findViewById(R.id.imgExpandArrow);

            dateTime = itemView.findViewById(R.id.textApplicantDateTime);
            location = itemView.findViewById(R.id.textApplicantLocation);
            phone = itemView.findViewById(R.id.textApplicantPhone);
            email = itemView.findViewById(R.id.textApplicantEmail);
            status = itemView.findViewById(R.id.textApplicantStatus);

            btnAccept = itemView.findViewById(R.id.btnAcceptApplicant);
            btnReject = itemView.findViewById(R.id.btnRejectApplicant);
        }
    }
}