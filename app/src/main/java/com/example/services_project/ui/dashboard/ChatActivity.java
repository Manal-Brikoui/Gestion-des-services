package com.example.services_project.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.services_project.R;
import com.example.services_project.model.Message;
import com.example.services_project.ui.adapter.MessageAdapter;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private MessageViewModel viewModel;
    private MessageAdapter messageAdapter;

    private int currentUserId;
    private int targetUserId;
    private String targetUserName;

    private EditText inputMessage;
    private RecyclerView recyclerView;

    // Constantes de Session
    private static final String PREF_AUTH_FILE = "AUTH_PREFS";
    private static final String PREF_USER_ID_KEY = "CURRENT_USER_ID";
    private static final int DEFAULT_USER_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //  Récupération des IDs et du nom
        targetUserId = getIntent().getIntExtra("TARGET_USER_ID", -1);
        targetUserName = getIntent().getStringExtra("TARGET_USER_NAME");
        currentUserId = getCurrentUserIdFromSession();

        Log.d(TAG, "Conversation chargée entre A (Moi) ID: " + currentUserId +
                " et B (Cible) ID: " + targetUserId);

        if (targetUserId == -1 || currentUserId == -1) {
            Toast.makeText(this, "Erreur : ID utilisateur ou cible manquant.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialisation des Vues
        inputMessage = findViewById(R.id.inputMessage);
        ImageView buttonSend = findViewById(R.id.buttonSend);
        recyclerView = findViewById(R.id.recyclerViewChat);
        Toolbar toolbar = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);

        //  Configuration de la Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(targetUserName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Configuration du RecyclerView et de l'Adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(this, new ArrayList<>(), currentUserId);
        recyclerView.setAdapter(messageAdapter);

        // Initialisation du ViewModel
        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        viewModel.setCurrentUserId(currentUserId);

        // Observer l'historique des messages
        viewModel.getConversation().observe(this, messages -> {
            messageAdapter.updateList(messages);
            if (!messages.isEmpty()) {
                recyclerView.scrollToPosition(messages.size() - 1);
            }
        });

        // Charger l'historique au démarrage
        viewModel.loadConversation(targetUserId);

        viewModel.markMessagesAsRead(targetUserId);

        //  Gestion du clic sur le bouton d'envoi
        buttonSend.setOnClickListener(v -> sendMessage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     Logique d'envoi du message.
     */
    private void sendMessage() {
        String content = inputMessage.getText().toString().trim();

        if (!content.isEmpty()) {
            viewModel.sendMessage(targetUserId, content);
            inputMessage.setText("");
        }
    }

    private int getCurrentUserIdFromSession() {
        SharedPreferences prefs = getSharedPreferences(PREF_AUTH_FILE, Context.MODE_PRIVATE);
        return prefs.getInt(PREF_USER_ID_KEY, -1);
    }
}