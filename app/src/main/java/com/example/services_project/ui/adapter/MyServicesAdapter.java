package com.example.services_project.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.services_project.R;
import com.example.services_project.model.Service;

import java.util.List;

public class MyServicesAdapter extends RecyclerView.Adapter<MyServicesAdapter.ViewHolder> {

    private final Context context;
    private List<Service> services;
    private final OnEditClickListener onEditClick;
    private final OnDeleteClickListener onDeleteClick;

    // --- Interfaces ---
    public interface OnEditClickListener {
        void onEdit(Service service);
    }

    public interface OnDeleteClickListener {
        void onDelete(Service service);
    }

    // --- Constructeur ---
    public MyServicesAdapter(Context context, List<Service> services,
                             OnEditClickListener onEditClick,
                             OnDeleteClickListener onDeleteClick) {
        this.context = context;
        this.services = services;
        this.onEditClick = onEditClick;
        this.onDeleteClick = onDeleteClick;
    }

    // --- Mise à jour de la liste ---
    public void updateList(List<Service> newList) {
        this.services = newList;
        notifyDataSetChanged();
    }

    // --- Création ViewHolder ---
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_service, parent, false);
        return new ViewHolder(view);
    }

    // --- Binding ---
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service s = services.get(position);

        holder.textCategory.setText(s.getCategory());
        holder.textTitle.setText(s.getTitle());
        holder.textDescription.setText(s.getDescription());

        // Charger image
        if (s.getImageUri() != null && !s.getImageUri().isEmpty()) {
            holder.imageService.setImageURI(Uri.parse(s.getImageUri()));
        } else {
            holder.imageService.setImageResource(R.drawable.ic_placeholder);
        }

        // Bouton Modifier
        holder.btnEdit.setOnClickListener(v -> onEditClick.onEdit(s));

        // Bouton Supprimer
        holder.btnDelete.setOnClickListener(v -> onDeleteClick.onDelete(s));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    // --- ViewHolder ---
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageService, btnEdit, btnDelete;
        TextView textTitle, textCategory, textDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageService   = itemView.findViewById(R.id.imageService);
            btnEdit        = itemView.findViewById(R.id.btnEditService);
            btnDelete      = itemView.findViewById(R.id.btnDeleteService);
            textTitle      = itemView.findViewById(R.id.textTitle);
            textCategory   = itemView.findViewById(R.id.textCategory);
            textDescription = itemView.findViewById(R.id.textDescription);
        }
    }
}
