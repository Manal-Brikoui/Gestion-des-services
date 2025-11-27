package com.example.services_project.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.example.services_project.R;
import com.example.services_project.model.User;
import com.example.services_project.ui.register.RegisterActivity;
import com.example.services_project.ui.dashboard.DashboardActivity;
import com.example.services_project.utils.UserSessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnRegister;
    private TextView tvForgotPassword;
    private UserSessionManager session;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ⚡ Initialisation UI
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword); // Nouveau

        // ⚡ Session et ViewModel
        session = new UserSessionManager(this);
        viewModel = new LoginViewModel();
        viewModel.init(this);

        // 🔹 Bouton Connexion
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Email et mot de passe requis", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔹 Récupérer l'utilisateur depuis la BDD
            User user = viewModel.getUser(email);

            if(user == null || !user.getPassword().equals(password)) {
                Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔹 Sauvegarder l'utilisateur dans la session
            session.saveLoggedUser(user);

            Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();

            // 🔹 Redirection vers Dashboard
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // 🔹 Bouton Créer un compte
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // 🔹 Mot de passe oublié
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }
}
