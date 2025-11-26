package com.example.services_project.ui.register;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.example.services_project.R;
import com.example.services_project.model.User;
import com.example.services_project.ui.login.LoginActivity;
import com.example.services_project.utils.UserSessionManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFirstName, edtLastName, edtEmail, edtPassword;
    private Button btnRegister, btnBackToLogin;
    private RegisterViewModel viewModel = new RegisterViewModel();
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialisation des champs
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        viewModel.init(this);
        sessionManager = new UserSessionManager(this);

        // Gestion du bouton "Créer un compte"
        btnRegister.setOnClickListener(v -> {
            String firstName = edtFirstName.getText().toString().trim();
            String lastName = edtLastName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Création du User
            User user = new User(firstName, lastName, email, password);

            // Appel au ViewModel pour l'inscription
            if (viewModel.register(user)) {
                // Sauvegarder l'utilisateur dans la session
                sessionManager.saveLoggedUser(user);

                Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_SHORT).show();

                // Redirection vers LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Erreur : email déjà utilisé", Toast.LENGTH_SHORT).show();
            }
        });

        // Gestion du bouton "Déjà un compte ? Se connecter"
        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
