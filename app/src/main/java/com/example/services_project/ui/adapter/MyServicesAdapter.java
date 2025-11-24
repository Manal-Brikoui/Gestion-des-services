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

public class MyServicesAdapter extends RecyclerView.Adapter<MyServicesAdapter.MyServiceViewHolder> {

    private List<Service> services;
    private Context context;
    private OnEditClickListener editClickListener;

    // Interface pour gérer le clic sur le crayon
    public interface OnEditClickListener {
        void onEditClick(Service service);
    }

    public MyServicesAdapter(Context context, List<Service> services, OnEditClickListener listener) {
        this.context = context;
        this.services = services;
        this.editClickListener = listener;
    }

    @NonNull
    @Override
    public MyServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_service, parent, false);
        return new MyServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyServiceViewHolder holder, int position) {
        Service service = services.get(position);

        holder.tvCategory.setText(service.getCategory());
        holder.tvTitle.setText(service.getTitle());
        holder.tvDescription.setText(service.getDescription());

        if (service.getImageUri() != null && !service.getImageUri().isEmpty()) {
            holder.imgService.setImageURI(Uri.parse(service.getImageUri()));
        } else {
            holder.imgService.setImageResource(service.getImageResId());
        }

        // Gestion du clic sur l'icône de modification
        holder.btnEditService.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(service);
            }
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public void updateList(List<Service> newList) {
        services = newList;
        notifyDataSetChanged();
    }

    static class MyServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView imgService, btnEditService;
        TextView tvCategory, tvTitle, tvDescription;

        public MyServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            imgService = itemView.findViewById(R.id.imageService);
            btnEditService = itemView.findViewById(R.id.btnEditService);
            tvCategory = itemView.findViewById(R.id.textCategory);
            tvTitle = itemView.findViewById(R.id.textTitle);
            tvDescription = itemView.findViewById(R.id.textDescription);
        }
    }
}
