package com.example.services_project.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.services_project.R;
import com.example.services_project.model.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddFragment extends Fragment {

    private EditText edtTitle, edtDescription, edtLocation, edtPrice, edtMoreDetails;
    private Spinner spinnerCategory;
    private Button btnAddService;
    private ImageView imgService;

    private HomeFragmentViewModel viewModel;
    private Uri selectedImageUri = null;

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeFragmentViewModel.class);

        // Initialisation UI
        edtTitle = root.findViewById(R.id.edtTitle);
        edtDescription = root.findViewById(R.id.edtDescription);
        edtLocation = root.findViewById(R.id.edtLocation);
        edtPrice = root.findViewById(R.id.edtPrice);
        edtMoreDetails = root.findViewById(R.id.edtMoreDetails);
        spinnerCategory = root.findViewById(R.id.spinnerCategory);
        btnAddService = root.findViewById(R.id.btnAddService);
        imgService = root.findViewById(R.id.imgService);

        // Spinner
        String[] categories = {"COIFFURE", "PLOMBERIE", "MASSAGE", "ÉLECTRICIEN", "PÉDIATRIE", "INFORMATIQUE", "DESIGN", "CUISINE"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterSpinner);

        // Galerie Launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imgService.setImageURI(selectedImageUri);
                    }
                }
        );

        imgService.setOnClickListener(v -> openGallery());
        btnAddService.setOnClickListener(v -> addService());

        return root;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void addService() {
        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();
        String moreDetails = edtMoreDetails.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            Toast.makeText(requireContext(), "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sauvegarde locale de l’image
        String localPath = null;
        if (selectedImageUri != null) {
            localPath = saveImageToInternalStorage(selectedImageUri);
        }

        int imageResId = (localPath == null) ? R.drawable.ic_haircut : 0;

        // Récupération de l'ID de l'utilisateur
        int userId = getCurrentUserId();

        Service newService = new Service(
                0,
                category,
                title,
                description,
                imageResId,
                localPath,
                location,
                price,
                moreDetails,
                userId
        );

        viewModel.insertService(newService);
        Toast.makeText(requireContext(), "Service ajouté avec succès", Toast.LENGTH_SHORT).show();
        resetForm();
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            File directory = requireContext().getFilesDir();
            File file = new File(directory, "service_" + System.currentTimeMillis() + ".jpg");

            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void resetForm() {
        edtTitle.setText("");
        edtDescription.setText("");
        edtLocation.setText("");
        edtPrice.setText("");
        edtMoreDetails.setText("");
        spinnerCategory.setSelection(0);
        imgService.setImageResource(R.drawable.ic_haircut);
        selectedImageUri = null;
    }

    // Méthode temporaire pour récupérer l'ID utilisateur
    private int getCurrentUserId() {
        // TODO : remplacer par la récupération réelle de l'utilisateur connecté
        return 1;
    }
}
