package com.example.services_project.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.services_project.R;
import com.example.services_project.model.Message;

import java.util.List;


 //Adapter pour afficher la liste des objets Message dans un RecyclerView,


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int MSG_TYPE_SENT = 0;
    private static final int MSG_TYPE_RECEIVED = 1;
    private static final String TAG = "MessageAdapter";

    private final Context context;
    private List<Message> messagesList;
    private final int currentUserId;

    public MessageAdapter(Context context, List<Message> messagesList, int currentUserId) {
        this.context = context;
        this.messagesList = messagesList;
        this.currentUserId = currentUserId;
    }

    //Déterminer le type de vue (Envoyé ou Reçu)
    @Override
    public int getItemViewType(int position) {
        if (messagesList.get(position).getSenderId() == currentUserId) {
            return MSG_TYPE_SENT;
        } else {
            return MSG_TYPE_RECEIVED;
        }
    }
    //  Créer le ViewHolder en fonction du type de vue
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == MSG_TYPE_SENT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    //  Lier les données à la vue
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messagesList.get(position);

        // Afficher le contenu du message
        holder.showMessage.setText(message.getContent());

        // Afficher le timestamp
        holder.textDate.setText(message.getTimestamp());

        //  Log de débogage
        int viewType = getItemViewType(position);
        String type = (viewType == MSG_TYPE_SENT ? "SENT" : "RECEIVED");
        Log.d(TAG, "Message ID: " + message.getId() +
                ", Sender ID: " + message.getSenderId() +
                ", Current User ID: " + currentUserId +
                ", Type: " + type);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }


    public void updateList(List<Message> newList) {
        if (newList != null) {
            messagesList = newList;
            notifyDataSetChanged();
        }
    }


      //ViewHolder qui contient les vues des bulles de message.

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        public final TextView showMessage;
        public final TextView textDate;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.show_message);
            textDate = itemView.findViewById(R.id.text_date);
        }
    }
}