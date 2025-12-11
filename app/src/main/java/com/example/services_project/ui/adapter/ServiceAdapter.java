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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.services_project.R;
import com.example.services_project.model.Service;
import com.example.services_project.ui.dashboard.ServiceDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private final Context context;
    private List<Service> serviceList = new ArrayList<>();

    public ServiceAdapter(Context context, List<Service> initialList) {
        this.context = context;
        if (initialList != null)
            this.serviceList = new ArrayList<>(initialList);
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {

        Service service = serviceList.get(position);

        holder.category.setText(service.getCategory());
        holder.title.setText(service.getTitle());
        holder.description.setText(service.getDescription());

        if (service.getImageUri() != null && !service.getImageUri().isEmpty()) {
            try {
                holder.image.setImageURI(Uri.parse(service.getImageUri()));
            } catch (Exception e) {
                holder.image.setImageResource(R.drawable.ic_haircut);
            }
        } else if (service.getImageResId() > 0) {
            holder.image.setImageResource(service.getImageResId());
        } else {
            holder.image.setImageResource(R.drawable.ic_haircut);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ServiceDetailActivity.class);
            intent.putExtra("service", service);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public void updateList(List<Service> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new ServiceDiffCallback(serviceList, newList)
        );
        serviceList.clear();
        serviceList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }
    static class ServiceDiffCallback extends DiffUtil.Callback {

        List<Service> oldList;
        List<Service> newList;

        public ServiceDiffCallback(List<Service> oldList, List<Service> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            return oldList.get(oldPos).getId() == newList.get(newPos).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            Service oldItem = oldList.get(oldPos);
            Service newItem = newList.get(newPos);

            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getCategory().equals(newItem.getCategory()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getImageResId() == newItem.getImageResId() &&
                    ((oldItem.getImageUri() == null && newItem.getImageUri() == null) ||
                            (oldItem.getImageUri() != null &&
                                    oldItem.getImageUri().equals(newItem.getImageUri())));
        }
    }


    static class ServiceViewHolder extends RecyclerView.ViewHolder {

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
