package com.example.services_project.ui.dashboard;
import com.example.services_project.ui.login.LoginActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.services_project.R;
import com.example.services_project.data.DatabaseHelper;
import com.example.services_project.model.User;
import com.example.services_project.utils.UserSessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    private ImageView ivProfileImage, ivEditPhoto;
    private TextView tvUserFullName, tvUserEmail;
    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword, btnLogout;
    private LinearLayout layoutHelp, layoutContact, layoutAbout;
    private UserSessionManager session;
    private User currentUser;

    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Session utilisateur
        session = new UserSessionManager(requireContext());
        currentUser = session.getLoggedUser();

        // UI
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        ivEditPhoto = view.findViewById(R.id.ivEditPhoto);
        tvUserFullName = view.findViewById(R.id.tvUserFullName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Support  et Informations
        layoutHelp = view.findViewById(R.id.layoutHelp);
        layoutContact = view.findViewById(R.id.layoutContact);
        layoutAbout = view.findViewById(R.id.layoutAbout);

        // Afficher infos utilisateur
        tvUserFullName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        tvUserEmail.setText(currentUser.getEmail());

        // Charger la photo de profil si elle existe
        loadProfileImage();

        // Gallery Launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        ivProfileImage.setImageURI(selectedImageUri);
                        saveProfileImage(selectedImageUri);
                    }
                }
        );

        // Clic sur l'icône caméra pour changer la photo
        ivEditPhoto.setOnClickListener(v -> openGallery());

        // Clic sur l'image elle-même
        ivProfileImage.setOnClickListener(v -> openGallery());

        // Changer mot de passe
        btnChangePassword.setOnClickListener(v -> changePassword());

        // Déconnexion
        btnLogout.setOnClickListener(v -> logout());

        // Support et Informations - Clics
        layoutHelp.setOnClickListener(v -> showHelpDialog());
        layoutContact.setOnClickListener(v -> showContactDialog());
        layoutAbout.setOnClickListener(v -> showAboutDialog());

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void loadProfileImage() {
        String imagePath = getProfileImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ivProfileImage.setImageURI(Uri.fromFile(imageFile));
            }
        }
    }

    private void saveProfileImage(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            File directory = requireContext().getFilesDir();
            File file = new File(directory, "profile_" + currentUser.getId() + ".jpg");

            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            // Sauvegarder le chemin dans SharedPreferences
            session.saveProfileImagePath(file.getAbsolutePath());

            Toast.makeText(requireContext(), "Photo de profil mise à jour !", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }

    private String getProfileImagePath() {
        return requireContext().getSharedPreferences("AUTH_PREFS", requireContext().MODE_PRIVATE)
                .getString("profile_image_path", null);
    }

    private void changePassword() {
        String oldPwd = etOldPassword.getText().toString().trim();
        String newPwd = etNewPassword.getText().toString().trim();
        String confirmPwd = etConfirmPassword.getText().toString().trim();

        if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!oldPwd.equals(currentUser.getPassword())) {
            Toast.makeText(getContext(), " Ancien mot de passe incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            Toast.makeText(getContext(), " Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérification du mot de passe
        if (!isValidPassword(newPwd)) {
            Toast.makeText(getContext(), " Le mot de passe doit contenir au moins une lettre majuscule, un chiffre et un caractère spécial.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPwd.length() < 4) {
            Toast.makeText(getContext(), " Mot de passe trop court (min 4 caractères)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mise à jour dans la BDD
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        boolean success = dbHelper.updatePassword(currentUser.getEmail(), newPwd);

        if (success) {
            // Mise à jour de la session
            currentUser.setPassword(newPwd);
            session.saveLoggedUser(currentUser);

            Toast.makeText(getContext(), " Mot de passe changé avec succès !", Toast.LENGTH_SHORT).show();

            // Réinitialiser les champs
            etOldPassword.setText("");
            etNewPassword.setText("");
            etConfirmPassword.setText("");
        } else {
            Toast.makeText(getContext(), " Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        session.logoutUser(); // Déconnexion de l'utilisateur
        Toast.makeText(requireContext(), "Déconnexion réussie", Toast.LENGTH_SHORT).show();

        // Redirection vers LoginActivity
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }



    private void showHelpDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_help);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnClose = dialog.findViewById(R.id.btnCloseHelp);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showContactDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_contact);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnClose = dialog.findViewById(R.id.btnCloseContact);
        LinearLayout layoutEmail = dialog.findViewById(R.id.layoutEmail);
        LinearLayout layoutPhone = dialog.findViewById(R.id.layoutPhone);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Action Email
        layoutEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@servicesapp.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support - Services App");
            startActivity(Intent.createChooser(emailIntent, "Envoyer un email"));
        });

        // Action Téléphone
        layoutPhone.setOnClickListener(v -> {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.parse("tel:+212600000000"));
            startActivity(phoneIntent);
        });

        dialog.show();
    }

    private void showAboutDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_about);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnClose = dialog.findViewById(R.id.btnCloseAbout);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Validation du mot de passe
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}

