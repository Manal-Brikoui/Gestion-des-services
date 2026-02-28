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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // Gestion du bouton
        btnRegister.setOnClickListener(v -> {
            String firstName = edtFirstName.getText().toString().trim();
            String lastName = edtLastName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vérifier la validité du mot de passe
            if (!isValidPassword(password)) {
                Toast.makeText(this, "Le mot de passe doit contenir au moins une lettre majuscule, un chiffre et un caractère spécial.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Création du User avec le mot de passe
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

        // Gestion du bouton
        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Méthode pour vérifier que le mot de passe contient au moins une lettre majuscule, un chiffre et un caractère spécial
    private boolean isValidPassword(String password) {
        // Expression régulière pour vérifier les exigences du mot de passe
        String passwordPattern = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z0-9@$!%*?&]{6,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
