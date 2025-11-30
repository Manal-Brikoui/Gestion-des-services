package com.example.services_project.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.services_project.R;
import com.example.services_project.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // Durée de l'écran de splash (en ms)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Utiliser Handler pour retarder l'affichage de la page suivante
        new Handler().postDelayed(() -> {
            // Démarrer LoginActivity après le délai
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Ferme l'activité Splash pour ne pas revenir en arrière
        }, SPLASH_DURATION);
    }
}
