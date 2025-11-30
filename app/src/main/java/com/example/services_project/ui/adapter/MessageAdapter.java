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

/**
 * Adapter pour afficher la liste des objets Message dans un RecyclerView,
 * gérant deux types de vues : envoyé (droite) et reçu (gauche).
 */
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

    // ----------------------------------------------------------------------
    // 1. Déterminer le type de vue (Envoyé ou Reçu)
    // ----------------------------------------------------------------------
    @Override
    public int getItemViewType(int position) {
        // Cette logique est CORRECTE pour l'alignement.
        // Si cette condition est TOUJOURS vraie, c'est que le SenderId enregistré
        // dans la BDD pour les messages reçus est ERRONÉ.
        if (messagesList.get(position).getSenderId() == currentUserId) {
            return MSG_TYPE_SENT; // Aligné à droite (votre message)
        } else {
            return MSG_TYPE_RECEIVED; // Aligné à gauche (message de l'interlocuteur)
        }
    }

    // ----------------------------------------------------------------------
    // 2. Créer le ViewHolder en fonction du type de vue
    // ----------------------------------------------------------------------
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

    // ----------------------------------------------------------------------
    // 3. Lier les données à la vue
    // ----------------------------------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messagesList.get(position);

        // Afficher le contenu du message
        holder.showMessage.setText(message.getContent());

        // Afficher le timestamp
        // Assurez-vous que message.getTimestamp() retourne une String (ou est correctement formaté)
        holder.textDate.setText(message.getTimestamp());

        // 💡 Log de débogage ESSENTIEL pour voir les IDs (comme discuté)
        int viewType = getItemViewType(position);
        String type = (viewType == MSG_TYPE_SENT ? "SENT" : "RECEIVED");
        Log.d(TAG, "Message ID: " + message.getId() +
                ", Sender ID: " + message.getSenderId() +
                ", Current User ID: " + currentUserId + // Affichage de l'ID courant pour comparaison
                ", Type: " + type);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    /**
     * Met à jour la liste des messages et notifie le RecyclerView.
     */
    public void updateList(List<Message> newList) {
        if (newList != null) {
            messagesList = newList;
            notifyDataSetChanged();
        }
    }

    /**
     * ViewHolder qui contient les vues des bulles de message.
     */
    static class MessageViewHolder extends RecyclerView.ViewHolder {

        public final TextView showMessage; // Utilisation de 'final' pour la bonne pratique
        public final TextView textDate;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ces IDs doivent exister dans item_message_sent.xml et item_message_received.xml
            showMessage = itemView.findViewById(R.id.show_message);
            textDate = itemView.findViewById(R.id.text_date);
        }
    }
}