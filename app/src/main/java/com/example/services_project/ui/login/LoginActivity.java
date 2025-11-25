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
import com.example.services_project.utils.UserSessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnRegister;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        sessionManager = new UserSessionManager(this);

        // Gestion du bouton Login
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {

                // 🔑 Ici, on récupère l'ID du user depuis le repository ou mock
                int userId = getUserIdByEmail(email); // Méthode fictive à implémenter

                if (userId != -1) {
                    // Sauvegarder l'ID du user connecté
                    sessionManager.saveUserId(userId);

                    Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();

                    // Passage au dashboard
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
            }
        });

        // Gestion du bouton Register
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    /**
     * Méthode temporaire pour récupérer un ID utilisateur à partir de l'email
     * Remplacez ceci par votre logique réelle (BDD ou mock)
     */
    private int getUserIdByEmail(String email) {
        switch (email) {
            case "manal@gmail.com":
                return 1;
            case "manal11@gmail.com":
                return 2;
            default:
                return -1;
        }
    }
}
