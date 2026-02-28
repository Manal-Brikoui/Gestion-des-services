package com.example.services_project.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

import androidx.lifecycle.ViewModelProvider;

import com.example.services_project.R;
import com.example.services_project.utils.UserSessionManager;
import com.example.services_project.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtNewPassword, edtConfirmPassword;
    private Button btnResetPassword;
    private ImageView btnBackToLogin;
    private UserSessionManager session;
    private LoginViewModel loginViewModel;

    private String email; //  Email reçu depuis le Login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // Bouton retour vers Login
        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Session
        session = new UserSessionManager(this);

        // ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.init(this);

        // Récupération EMAIL envoyé depuis LoginActivity
        email = getIntent().getStringExtra("email_from_login");

        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Aucun email fourni !", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // RESET MOT DE PASSE
        btnResetPassword.setOnClickListener(v -> {
            String newPwd = edtNewPassword.getText().toString().trim();
            String confirmPwd = edtConfirmPassword.getText().toString().trim();

            // Vérifications
            if (newPwd.isEmpty() || confirmPwd.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPwd.equals(confirmPwd)) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vérification du mot de passe
            if (!isValidPassword(newPwd)) {
                Toast.makeText(this, "Le mot de passe doit contenir au moins une lettre majuscule, un chiffre et un caractère spécial.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPwd.length() < 4) {
                Toast.makeText(this, "Le mot de passe doit contenir au moins 4 caractères", Toast.LENGTH_SHORT).show();
                return;
            }

            //  Mise à jour dans la base
            boolean success = loginViewModel.changePassword(email, newPwd);

            if (success) {

                //  Mise à jour session
                User u = session.getLoggedUser();
                if (u != null && u.getEmail().equals(email)) {
                    u.setPassword(newPwd);
                    session.saveLoggedUser(u);
                }

                Toast.makeText(this, "Mot de passe mis à jour avec succès !", Toast.LENGTH_LONG).show();

                //  Retour Login + email pré-rempli
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                intent.putExtra("email_reset", email);
                startActivity(intent);

                finish();

            } else {
                Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
            }
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