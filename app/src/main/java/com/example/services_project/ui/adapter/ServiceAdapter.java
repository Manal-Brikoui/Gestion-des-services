package com.example.services_project.ui.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.services_project.ui.dashboard.ServiceDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> serviceList;
    private List<Service> fullList;
    private Context context;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = new ArrayList<>(serviceList);
        this.fullList = new ArrayList<>(serviceList);
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.category.setText(service.getCategory());
        holder.title.setText(service.getTitle());
        holder.description.setText(service.getDescription());

        // Afficher l'image selon resId ou URI
        if (service.getImageUri() != null) {
            holder.image.setImageURI(Uri.parse(service.getImageUri()));
        } else {
            holder.image.setImageResource(service.getImageResId() > 0 ? service.getImageResId() : R.drawable.ic_haircut);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ServiceDetailActivity.class);
            intent.putExtra("service", service);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public void updateList(List<Service> newList) {
        serviceList.clear();
        serviceList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView category, title, description;
        ImageView image;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.textCategory);
            title = itemView.findViewById(R.id.textTitle);
            description = itemView.findViewById(R.id.textDescription);
            image = itemView.findViewById(R.id.imageService);
        }
    }
}
