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

import java.util.ArrayList;
import java.util.List;

public class ApplicantsAdapter extends RecyclerView.Adapter<ApplicantsAdapter.ViewHolder> {

    private final List<Candidate> applicants;
    private OnApplicantActionListener listener;

    // 🔥 Le SEUL constructeur correct
    public ApplicantsAdapter(List<Candidate> applicants, OnApplicantActionListener listener) {
        this.applicants = applicants != null ? applicants : new ArrayList<>();
        this.listener = listener;
    }

    // Listener pour accepter / refuser
    public interface OnApplicantActionListener {
        void onAccept(Candidate candidate);
        void onReject(Candidate candidate);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_applicant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Candidate c = applicants.get(position);

        holder.textName.setText(c.getFirstName() + " " + c.getLastName());
        holder.textDateTime.setText(c.getDateTime());
        holder.textLocation.setText(c.getLocation());
        holder.textPhone.setText(c.getPhone());
        holder.textEmail.setText(c.getEmail());

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAccept(c);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(c);
        });
    }

    @Override
    public int getItemCount() {
        return applicants.size();
    }

    // 🔥 Mise à jour propre de la liste
    public void updateList(List<Candidate> newList) {
        applicants.clear();
        if (newList != null) {
            applicants.addAll(newList);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textDateTime, textLocation, textPhone, textEmail;
        Button btnAccept, btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textApplicantName);
            textDateTime = itemView.findViewById(R.id.textApplicantDateTime);
            textLocation = itemView.findViewById(R.id.textApplicantLocation);
            textPhone = itemView.findViewById(R.id.textApplicantPhone);
            textEmail = itemView.findViewById(R.id.textApplicantEmail);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
