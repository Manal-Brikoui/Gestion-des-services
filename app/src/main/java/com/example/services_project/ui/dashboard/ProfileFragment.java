package com.example.services_project.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.services_project.R;
import com.example.services_project.data.DatabaseHelper;
import com.example.services_project.model.User;
import com.example.services_project.utils.UserSessionManager;

public class ProfileFragment extends Fragment {

    private TextView tvUserFullName, tvUserEmail;
    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword, btnLogout;
    private UserSessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Session utilisateur
        session = new UserSessionManager(requireContext());
        User user = session.getLoggedUser();

        // ⚠️ Champs UI
        tvUserFullName = view.findViewById(R.id.tvUserFullName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);

        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Afficher infos utilisateur
        tvUserFullName.setText(user.getFirstName() + " " + user.getLastName());
        tvUserEmail.setText(user.getEmail());

        // Changer mot de passe
        btnChangePassword.setOnClickListener(v -> {
            String oldPwd = etOldPassword.getText().toString().trim();
            String newPwd = etNewPassword.getText().toString().trim();
            String confirmPwd = etConfirmPassword.getText().toString().trim();

            if (!oldPwd.equals(user.getPassword())) {
                Toast.makeText(getContext(), "Ancien mot de passe incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPwd.equals(confirmPwd)) {
                Toast.makeText(getContext(), "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPwd.length() < 4) {
                Toast.makeText(getContext(), "Mot de passe trop court", Toast.LENGTH_SHORT).show();
                return;
            }

            // ⚠️ Mise à jour dans la BDD
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            boolean success = dbHelper.updatePassword(user.getEmail(), newPwd);

            if (success) {
                // ⚠️ Mise à jour de la session
                user.setPassword(newPwd);
                session.saveLoggedUser(user);

                Toast.makeText(getContext(), "Mot de passe changé avec succès", Toast.LENGTH_SHORT).show();

                // Réinitialiser les champs
                etOldPassword.setText("");
                etNewPassword.setText("");
                etConfirmPassword.setText("");
            } else {
                Toast.makeText(getContext(), "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
            }
        });

        // Déconnexion
        btnLogout.setOnClickListener(v -> {
            session.logoutUser();
            requireActivity().finish();
        });

        return view;
    }
}
