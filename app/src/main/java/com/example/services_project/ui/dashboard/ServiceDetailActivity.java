package com.example.services_project.ui.dashboard;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.services_project.R;
import com.example.services_project.model.Candidate; // Import manquant
import com.example.services_project.model.Service;

import java.io.File;
import java.util.Calendar;

public class ServiceDetailActivity extends AppCompatActivity {

    private ImageView imageService, buttonBack;
    private TextView textCategory, textTitle, textDescription, textPrice, textLocation;
    private Button applyButton;

    // Déclaration du ViewModel
    private ServicesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        // Initialisation du ViewModel
        viewModel = new ViewModelProvider(this).get(ServicesViewModel.class);

        // Initialisation des vues (le reste de l'initialisation est inchangé)
        buttonBack = findViewById(R.id.buttonBack);
        imageService = findViewById(R.id.imageServiceDetail);
        textCategory = findViewById(R.id.textCategoryDetail);
        textTitle = findViewById(R.id.textTitleDetail);
        textDescription = findViewById(R.id.textDescriptionDetail);
        textPrice = findViewById(R.id.textPriceDetail);
        textLocation = findViewById(R.id.textLocationDetail);
        applyButton = findViewById(R.id.buttonApply);

        // Récupération du service
        Service service = (Service) getIntent().getSerializableExtra("service");

        if (service != null) {
            // ⚠️ Chargement image sécurisé (inchangé)
            try {
                if (service.getImageUri() != null && !service.getImageUri().isEmpty()) {
                    Uri uri = Uri.parse(service.getImageUri());
                    imageService.setImageURI(uri);
                } else if (service.getImageResId() > 0) {
                    imageService.setImageResource(service.getImageResId());
                } else {
                    imageService.setImageResource(R.drawable.ic_haircut); // fallback
                }
            } catch (Exception e) {
                e.printStackTrace();
                imageService.setImageResource(R.drawable.ic_haircut);
            }

            // Remplissage des champs (inchangé)
            textCategory.setText(service.getCategory() != null ? service.getCategory() : "");
            textTitle.setText(service.getTitle() != null ? service.getTitle() : "");
            textDescription.setText(service.getDescription() != null ? service.getDescription() : "");
            textPrice.setText("Tarif : " + (service.getPrice() != null ? service.getPrice() : ""));
            textLocation.setText("Localisation : " + (service.getLocation() != null ? service.getLocation() : ""));
        }

        // Flèche retour
        buttonBack.setOnClickListener(v -> onBackPressed());

        // Bouton "Postuler"
        applyButton.setOnClickListener(v -> showApplyDialog(service));
    }

    private void showApplyDialog(Service service) {
        if (service == null) return;

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_apply_service);
        dialog.getWindow().setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );

        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        Button btnPostuler = dialog.findViewById(R.id.btnPostuler);
        EditText editNom = dialog.findViewById(R.id.editNom);
        EditText editPrenom = dialog.findViewById(R.id.editPrenom);
        EditText editEmail = dialog.findViewById(R.id.editEmail);
        EditText editPhone = dialog.findViewById(R.id.editPhone);
        EditText editDate = dialog.findViewById(R.id.editDate);
        EditText editHeure = dialog.findViewById(R.id.editHeure);
        EditText editLocalisation = dialog.findViewById(R.id.editLocalisation);

        // Fermer le modal
        btnClose.setOnClickListener(x -> dialog.dismiss());

        // DatePicker (inchangé)
        editDate.setOnClickListener(x -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(ServiceDetailActivity.this,
                    (view, y, m, d) -> editDate.setText(String.format("%02d/%02d/%04d", d, m + 1, y)),
                    year, month, day);
            dpd.show();
        });

        // TimePicker (inchangé)
        editHeure.setOnClickListener(x -> {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog tpd = new TimePickerDialog(ServiceDetailActivity.this,
                    (view, h, m) -> editHeure.setText(String.format("%02d:%02d", h, m)),
                    hour, minute, true);
            tpd.show();
        });

        // Postuler (LOGIQUE DE SAUVEGARDE AJOUTÉE)
        btnPostuler.setOnClickListener(x -> {
            String nom = editNom.getText().toString().trim();
            String prenom = editPrenom.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String date = editDate.getText().toString().trim();
            String heure = editHeure.getText().toString().trim();
            String localisation = editLocalisation.getText().toString().trim();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || phone.isEmpty()
                    || date.isEmpty() || heure.isEmpty() || localisation.isEmpty()) {
                Toast.makeText(ServiceDetailActivity.this,
                        "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {

                // 1. Créer l'objet Candidat
                Candidate candidate = new Candidate(
                        service.getId(),                // Utilise l'ID du service
                        nom,
                        prenom,
                        date + " " + heure,             // Combine Date et Heure
                        localisation,
                        phone,
                        email
                );

                // 2. ENREGISTRER la candidature via le ViewModel
                viewModel.addApplicant(service.getId(), candidate);

                Toast.makeText(ServiceDetailActivity.this,
                        "Candidature envoyée avec succès pour : " + service.getTitle(),
                        Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}