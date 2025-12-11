package com.example.services_project.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.services_project.R;
import com.example.services_project.model.Candidate;
import java.util.List;

public class ApplicantsAdapter extends RecyclerView.Adapter<ApplicantsAdapter.CandidateViewHolder> {

    private List<Candidate> candidateList;
    private final OnApplicantActionListener listener;

    // Interface de communication pour les actions (Accept/Reject)
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

        // Remplissage des champs du candidat
        holder.name.setText(String.format("%s %s", candidate.getFirstName(), candidate.getLastName()));
        holder.dateTime.setText(candidate.getDateTime());
        holder.location.setText(candidate.getLocation());
        holder.phone.setText(candidate.getPhone());
        holder.email.setText(candidate.getEmail());
        holder.status.setText(candidate.getStatus());

        // Logique des boutons ACCEPT/REJECT
        if ("PENDING".equals(candidate.getStatus())) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);

            // Logique de clic : appelle la méthode définie dans ApplicantsDialogFragment
            holder.btnAccept.setOnClickListener(v -> listener.onAccept(candidate));
            holder.btnReject.setOnClickListener(v -> listener.onReject(candidate));

        } else {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }
    // ViewHolder
    public static class CandidateViewHolder extends RecyclerView.ViewHolder {
        public TextView name, dateTime, location, phone, email, status;

        public Button btnAccept, btnReject; //  Les deux boutons sont ici

        public CandidateViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textApplicantName);
            dateTime = itemView.findViewById(R.id.textApplicantDateTime);
            location = itemView.findViewById(R.id.textApplicantLocation);
            phone = itemView.findViewById(R.id.textApplicantPhone);
            email = itemView.findViewById(R.id.textApplicantEmail); // ⬅️ AJOUTEZ CETTE LIGNE ICI
            status = itemView.findViewById(R.id.textApplicantStatus);

            btnAccept = itemView.findViewById(R.id.btnAcceptApplicant);
            btnReject = itemView.findViewById(R.id.btnRejectApplicant);
        }
    }
}