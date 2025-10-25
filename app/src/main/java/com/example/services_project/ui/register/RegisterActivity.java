package com.example.services_project.ui.register;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.example.services_project.R;
import com.example.services_project.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword;
    private Button btnRegister, btnBackToLogin;
    private RegisterViewModel viewModel = new RegisterViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        viewModel.init(this);

        // Gestion du bouton "Créer un compte"
        btnRegister.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (viewModel.register(name, email, password)) {
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
