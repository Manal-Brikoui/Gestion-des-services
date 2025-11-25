package com.example.services_project.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.services_project.R;
import com.example.services_project.data.DatabaseHelper;
import com.example.services_project.ui.register.RegisterActivity;
import com.example.services_project.ui.dashboard.DashboardActivity;
import com.example.services_project.utils.UserSessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnRegister;
    private UserSessionManager sessionManager;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        sessionManager = new UserSessionManager(this);
        dbHelper = new DatabaseHelper(this);

        // Gestion du bouton Login
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {

                if (dbHelper.checkUser(email, password)) {
                    // Récupérer l'ID utilisateur
                    int userId = getUserId(email);

                    // Sauvegarder l'ID dans la session
                    sessionManager.saveUserId(userId);

                    Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();

                    // Passage au dashboard
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.putExtra("userId", userId); // Passer l'ID utilisateur
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Email et mot de passe requis", Toast.LENGTH_SHORT).show();
            }
        });

        // Gestion du bouton Register
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    /**
     * Récupère l'ID utilisateur depuis la BDD selon l'email
     */
    private int getUserId(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_ID},
                DatabaseHelper.COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        }
        cursor.close();
        db.close();
        return id;
    }
}
