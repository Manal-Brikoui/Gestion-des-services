package com.example.services_project.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.example.services_project.R;
import com.example.services_project.utils.UserSessionManager;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtNewPassword, edtConfirmPassword;
    private Button btnResetPassword;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // ⚡ Initialisation UI
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        // ⚡ Session utilisateur
        session = new UserSessionManager(this);

        // ⚠️ Vérifier si un utilisateur est connecté
        if(session.getLoggedUser() == null){
            Toast.makeText(this, "Vous devez être connecté pour réinitialiser le mot de passe", Toast.LENGTH_SHORT).show();
            finish(); // sortir de l'activité
            return;
        }

        btnResetPassword.setOnClickListener(v -> {
            String newPwd = edtNewPassword.getText().toString().trim();
            String confirmPwd = edtConfirmPassword.getText().toString().trim();

            // ✅ Vérification des champs
            if(newPwd.isEmpty() || confirmPwd.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Vérification correspondance des mots de passe
            if(!newPwd.equals(confirmPwd)) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Vérification longueur minimale
            if(newPwd.length() < 4){
                Toast.makeText(this, "Le mot de passe doit contenir au moins 4 caractères", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔹 Mise à jour du mot de passe via UserSessionManager
            boolean success = session.changePasswordForLoggedUser(newPwd, this);

            if(success){
                Toast.makeText(this, "Mot de passe mis à jour !", Toast.LENGTH_SHORT).show();
                edtNewPassword.setText("");
                edtConfirmPassword.setText("");

                // 🔹 Redirection vers Login
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
