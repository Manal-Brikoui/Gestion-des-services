package com.example.services_project.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.services_project.R;
import com.example.services_project.model.User;

import java.util.List;


  //Adapter pour afficher la liste des objets User dans un RecyclerView.

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private List<User> usersList;
    private final OnUserClickListener listener;


      /*Interface pour gérer les clics sur les éléments de la liste et
      pour demander le nombre de messages non lus */
    public interface OnUserClickListener {
        void onUserClick(User user);


        int getUnreadCount(int targetUserId);
    }

    public UserAdapter(Context context, List<User> usersList, OnUserClickListener listener) {
        this.context = context;
        this.usersList = usersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = usersList.get(position);

        holder.textUserName.setText(user.getFullName());

        holder.textInitial.setText(user.getInitial());

        if (listener != null) {
            int unreadCount = listener.getUnreadCount(user.getId());

            if (unreadCount > 0) {
                holder.textUnreadCount.setText(String.valueOf(unreadCount));
                holder.textUnreadCount.setVisibility(View.VISIBLE);
            } else {
                holder.textUnreadCount.setVisibility(View.GONE);
            }
        } else {
            holder.textUnreadCount.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public void updateList(List<User> newList) {
        usersList = newList;
        notifyDataSetChanged();
    }


    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textInitial;
        TextView textUserName;
        TextView textUnreadCount;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textInitial = itemView.findViewById(R.id.textInitial);
            textUserName = itemView.findViewById(R.id.textUserName);
            textUnreadCount = itemView.findViewById(R.id.textUnreadCount);
        }
    }
}