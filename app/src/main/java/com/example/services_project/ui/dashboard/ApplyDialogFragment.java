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
import androidx.lifecycle.ViewModelProvider;

import com.example.services_project.R;
import com.example.services_project.model.Candidate;

import java.util.Calendar;

public class ApplyDialogFragment extends DialogFragment {

    private EditText editNom, editPrenom, editEmail, editPhone, editDate, editHeure, editLocalisation;
    private Button btnPostuler;
    private ImageView btnClose;

    private int serviceId;
    private ServicesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_apply_service, container, false);

        // Récupération des vues
        editNom = view.findViewById(R.id.editNom);
        editPrenom = view.findViewById(R.id.editPrenom);
        editEmail = view.findViewById(R.id.editEmail);
        editPhone = view.findViewById(R.id.editPhone);
        editDate = view.findViewById(R.id.editDate);
        editHeure = view.findViewById(R.id.editHeure);
        editLocalisation = view.findViewById(R.id.editLocalisation);
        btnPostuler = view.findViewById(R.id.btnPostuler);
        btnClose = view.findViewById(R.id.btnClose);

        if (getArguments() != null) {
            serviceId = getArguments().getInt("serviceId");
        }

        viewModel = new ViewModelProvider(requireActivity()).get(ServicesViewModel.class);

        // Fermer le modal
        btnClose.setOnClickListener(v -> dismiss());

        // ---------------------------------------------------
        // DatePicker : interdit les dates passées
        // ---------------------------------------------------
        editDate.setOnClickListener(v -> {
            final Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            int year = today.get(Calendar.YEAR);
            int month = today.get(Calendar.MONTH);
            int day = today.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        editDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                        editHeure.setText(""); // réinitialiser l'heure si date change
                    }, year, month, day);

            // ⚡ Interdire les dates passées
            datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
            datePickerDialog.show();
        });

        // ---------------------------------------------------
        // TimePicker : interdit les heures passées si date = aujourd'hui
        // ---------------------------------------------------
        editHeure.setOnClickListener(v -> {
            String dateText = editDate.getText().toString().trim();
            if (dateText.isEmpty()) {
                Toast.makeText(requireContext(), "Veuillez choisir une date d'abord", Toast.LENGTH_SHORT).show();
                return;
            }

            final Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view12, selectedHour, selectedMinute) -> {
                        String[] parts = dateText.split("/");
                        int selDay = Integer.parseInt(parts[0]);
                        int selMonth = Integer.parseInt(parts[1]) - 1;
                        int selYear = Integer.parseInt(parts[2]);

                        Calendar selectedDateTime = Calendar.getInstance();
                        selectedDateTime.set(selYear, selMonth, selDay, selectedHour, selectedMinute);

                        // ⚡ Vérifier si date = aujourd'hui
                        Calendar today = Calendar.getInstance();
                        if (selYear == today.get(Calendar.YEAR)
                                && selMonth == today.get(Calendar.MONTH)
                                && selDay == today.get(Calendar.DAY_OF_MONTH)) {

                            if (selectedDateTime.before(now)) {
                                Toast.makeText(requireContext(), "Vous ne pouvez pas choisir une heure passée", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        editHeure.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }, hour, minute, true);

            timePickerDialog.show();
        });

        // ---------------------------------------------------
        // Bouton Postuler
        // ---------------------------------------------------
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
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            Candidate candidate = new Candidate(
                    serviceId,
                    nom,
                    prenom,
                    date + " " + heure,
                    localisation,
                    phone,
                    email
            );

            viewModel.addApplicant(serviceId, candidate);

            Toast.makeText(requireContext(),
                    "Candidature envoyée pour : " + nom + " " + prenom,
                    Toast.LENGTH_LONG).show();
            dismiss();
        });

        // Fond transparent
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        return view;
    }

    // Créer méthode statique pour créer un fragment avec serviceId
    public static ApplyDialogFragment newInstance(int serviceId) {
        ApplyDialogFragment fragment = new ApplyDialogFragment();
        Bundle args = new Bundle();
        args.putInt("serviceId", serviceId);
        fragment.setArguments(args);
        return fragment;
    }
}
