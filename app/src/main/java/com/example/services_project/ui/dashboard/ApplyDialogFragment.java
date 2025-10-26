package com.example.services_project.ui.dashboard;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.services_project.R;

import java.util.Calendar;

public class ApplyDialogFragment extends DialogFragment {

    private EditText editNom, editPrenom, editEmail, editPhone, editDate, editHeure, editLocalisation;
    private Button btnPostuler;
    private ImageView btnClose;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflater le layout correct
        View view = inflater.inflate(R.layout.dialog_apply_service, container, false);

        // --- Récupération des vues ---
        editNom = view.findViewById(R.id.editNom);
        editPrenom = view.findViewById(R.id.editPrenom);
        editEmail = view.findViewById(R.id.editEmail);
        editPhone = view.findViewById(R.id.editPhone);
        editDate = view.findViewById(R.id.editDate);
        editHeure = view.findViewById(R.id.editHeure);
        editLocalisation = view.findViewById(R.id.editLocalisation);
        btnPostuler = view.findViewById(R.id.btnPostuler);
        btnClose = view.findViewById(R.id.btnClose);

        // --- Fermer le modal ---
        btnClose.setOnClickListener(v -> dismiss());

        // --- DatePicker ---
        editDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        editDate.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // --- TimePicker ---
        editHeure.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view12, hourOfDay, minute1) -> {
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                        editHeure.setText(selectedTime);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        // --- Bouton Postuler ---
        btnPostuler.setOnClickListener(v -> {
            String nom = editNom.getText().toString().trim();
            String prenom = editPrenom.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String date = editDate.getText().toString().trim();
            String heure = editHeure.getText().toString().trim();
            String localisation = editLocalisation.getText().toString().trim();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || phone.isEmpty()
                    || date.isEmpty() || heure.isEmpty() || localisation.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Veuillez remplir tous les champs",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(),
                        "Candidature envoyée pour : " + nom + " " + prenom,
                        Toast.LENGTH_LONG).show();
                dismiss();
            }
        });

        // --- Apparence du modal ---
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        return view;
    }
}
