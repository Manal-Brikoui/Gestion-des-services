package com.example.services_project.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.content.Intent;

import com.example.services_project.R;
import com.example.services_project.ui.register.RegisterActivity;
import com.example.services_project.ui.dashboard.DashboardActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation des vues
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Gestion du bouton Login
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Pour test : accepte tout login non vide
            if(!email.isEmpty() && !password.isEmpty()) {
                Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();

                // Passage au dashboard
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish(); // empêche retour sur LoginActivity
            } else {
                Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
            }
        });

        // Gestion du bouton Register
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
