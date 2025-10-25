package com.example.services_project.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.services_project.R; // Assurez-vous d'avoir R importé
import com.example.services_project.model.Service;
import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> services = new ArrayList<>();
    private final ServiceClickListener clickListener;

    public interface ServiceClickListener {
        void onServiceClick(Service service);
    }

    public ServiceAdapter(ServiceClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setServices(List<Service> newServices) {
        this.services = newServices;
        notifyDataSetChanged(); // Simple
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Remplacez R.layout.item_service par le nom de votre fichier XML d'item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        holder.bind(service, clickListener);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryTextView;
        private final TextView titleTextView;
        private final ImageView iconImageView;
        private final View detailsArrow; // La flèche à droite

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            // Assurez-vous que ces IDs existent dans item_service.xml
            categoryTextView = itemView.findViewById(R.id.service_category);
            titleTextView = itemView.findViewById(R.id.service_title);
            iconImageView = itemView.findViewById(R.id.service_icon);
            detailsArrow = itemView.findViewById(R.id.details_arrow);
        }

        public void bind(final Service service, final ServiceClickListener listener) {
            categoryTextView.setText(service.getCategory());
            titleTextView.setText(service.getTitle());
            iconImageView.setImageResource(service.getIconResId());

            // Le clic sur la flèche déclenche l'action "détails maintenant"
            detailsArrow.setOnClickListener(v -> listener.onServiceClick(service));
        }
    }
}