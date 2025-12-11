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
import com.example.services_project.model.Candidate;
import com.example.services_project.model.Service;
import com.example.services_project.model.User;  // Import du modèle User pour obtenir le nom et prénom
import com.example.services_project.data.DatabaseHelper; // Import de DatabaseHelper

import java.util.Calendar;

public class ServiceDetailActivity extends AppCompatActivity {

    private ImageView imageService, buttonBack;
    private TextView textCategory, textTitle, textDescription, textPrice, textLocation, textPostedBy;
    private Button applyButton;

    private ServicesViewModel viewModel;
    private int currentServiceId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        viewModel = new ViewModelProvider(this).get(ServicesViewModel.class);

        buttonBack = findViewById(R.id.buttonBack);
        imageService = findViewById(R.id.imageServiceDetail);
        textCategory = findViewById(R.id.textCategoryDetail);
        textTitle = findViewById(R.id.textTitleDetail);
        textDescription = findViewById(R.id.textDescriptionDetail);
        textPrice = findViewById(R.id.textPriceDetail);
        textLocation = findViewById(R.id.textLocationDetail);
        textPostedBy = findViewById(R.id.textPostedBy);
        applyButton = findViewById(R.id.buttonApply);

        // Récupération du service
        Service service = (Service) getIntent().getSerializableExtra("service");

        if (service != null) {
            currentServiceId = service.getId();

            try {
                if (service.getImageUri() != null && !service.getImageUri().isEmpty()) {
                    Uri uri = Uri.parse(service.getImageUri());
                    imageService.setImageURI(uri);
                } else if (service.getImageResId() > 0) {
                    imageService.setImageResource(service.getImageResId());
                } else {
                    imageService.setImageResource(R.drawable.ic_haircut);
                }
            } catch (Exception e) {
                e.printStackTrace();
                imageService.setImageResource(R.drawable.ic_haircut);
            }

            textCategory.setText(service.getCategory() != null ? service.getCategory() : "");
            textTitle.setText(service.getTitle() != null ? service.getTitle() : "");
            textDescription.setText(service.getDescription() != null ? service.getDescription() : "");
            textPrice.setText("Tarif : " + (service.getPrice() != null ? service.getPrice() : ""));
            textLocation.setText("Localisation : " + (service.getLocation() != null ? service.getLocation() : ""));

            // Affichage du nom de la personne qui a posté le service
            User user = getUserById(service.getUserId()); // Récupérer l'utilisateur par son ID
            if (user != null) {
                textPostedBy.setText("Posté par : " + user.getFullName()); // Afficher le nom complet de l'utilisateur
            } else {
                textPostedBy.setText("Posté par : Inconnu");
            }
        }

        buttonBack.setOnClickListener(v -> onBackPressed());

        applyButton.setOnClickListener(v -> showApplyDialog(service));
    }

    // Méthode pour récupérer un utilisateur par son ID
    private User getUserById(int userId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this); // Créez un objet DatabaseHelper
        return dbHelper.getUserById(userId); // Appelle la méthode getUserById de DatabaseHelper pour récupérer l'utilisateur
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

        btnClose.setOnClickListener(x -> dialog.dismiss());

        // DatePicker : bloquer les dates passées
        editDate.setOnClickListener(x -> {
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH);
            int day = now.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(ServiceDetailActivity.this,
                    (view, y, m, d) -> {
                        editDate.setText(String.format("%02d/%02d/%04d", d, m + 1, y));
                        editHeure.setText(""); // Réinitialiser l'heure si date change
                    },
                    year, month, day);

            dpd.getDatePicker().setMinDate(System.currentTimeMillis());
            dpd.show();
        });

        // TimePicker : bloquer les heures passées pour aujourd'hui
        editHeure.setOnClickListener(x -> {
            String dateText = editDate.getText().toString().trim();
            if (dateText.isEmpty()) {
                Toast.makeText(ServiceDetailActivity.this, "Veuillez choisir une date d'abord", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            TimePickerDialog tpd = new TimePickerDialog(ServiceDetailActivity.this,
                    (view, h, m) -> {
                        String[] parts = dateText.split("/");
                        int selDay = Integer.parseInt(parts[0]);
                        int selMonth = Integer.parseInt(parts[1]) - 1;
                        int selYear = Integer.parseInt(parts[2]);

                        Calendar selected = Calendar.getInstance();
                        selected.set(selYear, selMonth, selDay, h, m);

                        if (selYear == now.get(Calendar.YEAR) &&
                                selMonth == now.get(Calendar.MONTH) &&
                                selDay == now.get(Calendar.DAY_OF_MONTH) &&
                                selected.before(now)) {
                            Toast.makeText(ServiceDetailActivity.this, "Vous ne pouvez pas choisir une heure passée", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        editHeure.setText(String.format("%02d:%02d", h, m));
                    }, hour, minute, true);

            tpd.show();
        });

        // Postuler
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
                Candidate candidate = new Candidate(
                        -1,
                        -1,
                        service.getId(),
                        prenom,
                        nom,
                        date + " " + heure,
                        localisation,
                        phone,
                        email,
                        null,
                        "PENDING"
                );

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
