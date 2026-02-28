package com.example.services_project.ui.dashboard;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import com.example.services_project.model.User;
import com.example.services_project.data.DatabaseHelper;

import java.util.Calendar;

public class ServiceDetailActivity extends AppCompatActivity {

    private ImageView imageService, buttonBack;
    private TextView textCategory, textTitle, textDescription, textPrice, textLocation, textPostedBy;
    private Button applyButton;

    private ServicesViewModel viewModel;
    private Service currentService;

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

        currentService = (Service) getIntent().getSerializableExtra("service");

        if (currentService != null) {
            int currentUserId = viewModel.getCurrentUserId();
            if (currentService.getUserId() == currentUserId) {
                applyButton.setVisibility(View.GONE);
            }

            try {
                if (currentService.getImageUri() != null && !currentService.getImageUri().isEmpty()) {
                    imageService.setImageURI(Uri.parse(currentService.getImageUri()));
                } else if (currentService.getImageResId() > 0) {
                    imageService.setImageResource(currentService.getImageResId());
                } else {
                    imageService.setImageResource(R.drawable.ic_haircut);
                }
            } catch (Exception e) {
                imageService.setImageResource(R.drawable.ic_haircut);
            }

            textCategory.setText(currentService.getCategory());
            textTitle.setText(currentService.getTitle());
            textDescription.setText(currentService.getDescription());
            textPrice.setText("Tarif : " + currentService.getPrice());
            textLocation.setText("Localisation : " + currentService.getLocation());

            User user = getUserById(currentService.getUserId());
            textPostedBy.setText(user != null ? "Posté par : " + user.getFullName() : "Posté par : Inconnu");
        }

        buttonBack.setOnClickListener(v -> onBackPressed());

        applyButton.setOnClickListener(v -> {
            if (currentService != null) {
                showApplyDialog(currentService);
            } else {
                Toast.makeText(this, "Service introuvable", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private User getUserById(int userId) {
        return new DatabaseHelper(this).getUserById(userId);
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

        btnClose.setOnClickListener(v -> dialog.dismiss());

        // DatePicker
        editDate.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(
                    this,
                    (view, y, m, d) -> {
                        editDate.setText(String.format("%02d/%02d/%04d", d, m + 1, y));
                        editHeure.setText("");
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.getDatePicker().setMinDate(System.currentTimeMillis());
            dpd.show();
        });

        // TimePicker
        editHeure.setOnClickListener(v -> {
            if (editDate.getText().toString().isEmpty()) {
                Toast.makeText(this, "Veuillez choisir une date d'abord", Toast.LENGTH_SHORT).show();
                return;
            }
            Calendar now = Calendar.getInstance();
            TimePickerDialog tpd = new TimePickerDialog(
                    this,
                    (view, h, m) -> editHeure.setText(String.format("%02d:%02d", h, m)),
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true
            );
            tpd.show();
        });

        btnPostuler.setOnClickListener(v -> {
            if (editNom.getText().toString().isEmpty()
                    || editPrenom.getText().toString().isEmpty()
                    || editEmail.getText().toString().isEmpty()
                    || editPhone.getText().toString().isEmpty()
                    || editDate.getText().toString().isEmpty()
                    || editHeure.getText().toString().isEmpty()
                    || editLocalisation.getText().toString().isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            Candidate candidate = new Candidate(
                    -1,
                    viewModel.getCurrentUserId(),
                    service.getId(),
                    editPrenom.getText().toString(),
                    editNom.getText().toString(),
                    editDate.getText().toString() + " " + editHeure.getText().toString(),
                    null,
                    editLocalisation.getText().toString(),
                    editPhone.getText().toString(),
                    editEmail.getText().toString(),
                    "PENDING"
            );

            viewModel.addApplicant(service.getId(), candidate);

            // Affiche le toast et reste sur la même page
            Toast.makeText(this,
                    "Candidature envoyée avec succès pour : " + service.getTitle(),
                    Toast.LENGTH_LONG).show();

            dialog.dismiss();
        });

        dialog.show();
    }
}
