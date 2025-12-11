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
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.services_project.R;
import com.example.services_project.model.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddFragment extends DialogFragment {

    private EditText edtTitle, edtDescription, edtLocation, edtPrice, edtMoreDetails;
    private Spinner spinnerCategory;
    private Button btnSaveService;
    private ImageView imgService;

    private ServicesViewModel viewModel;
    private Uri selectedImageUri = null;
    private Uri currentImageUri = null;
    private int serviceId = -1;

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(ServicesViewModel.class);

        // Initialisation UI
        edtTitle = root.findViewById(R.id.edtTitle);
        edtDescription = root.findViewById(R.id.edtDescription);
        edtLocation = root.findViewById(R.id.edtLocation);
        edtPrice = root.findViewById(R.id.edtPrice);
        edtMoreDetails = root.findViewById(R.id.edtMoreDetails);
        spinnerCategory = root.findViewById(R.id.spinnerCategory);
        btnSaveService = root.findViewById(R.id.btnAddService);
        imgService = root.findViewById(R.id.imgService);

        // Spinner categories
         String[] categories = {
                 "COIFFURE", "PLOMBERIE", "MASSAGE",
                "ÉLECTRICIEN", "PÉDIATRIE", "INFORMATIQUE",
                "DESIGN", "CUISINE",
                "JARDINIER", "MÉCANIQUE", "NETTOYAGE", "SÉCURITÉ"
        };

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterSpinner);

        // Arguments pour modification
        if (getArguments() != null) {
            serviceId = getArguments().getInt("serviceId", -1);
            edtTitle.setText(getArguments().getString("title", ""));
            edtDescription.setText(getArguments().getString("description", ""));
            edtLocation.setText(getArguments().getString("location", ""));
            edtPrice.setText(getArguments().getString("price", ""));
            edtMoreDetails.setText(getArguments().getString("moreDetails", ""));
            spinnerCategory.setSelection(adapterSpinner.getPosition(getArguments().getString("category", categories[0])));

            String imageUri = getArguments().getString("imageUri");
            if (imageUri != null && !imageUri.isEmpty()) {
                try {
                    currentImageUri = Uri.parse(imageUri);
                    imgService.setImageURI(currentImageUri);
                    selectedImageUri = currentImageUri;
                } catch (Exception e) {
                    e.printStackTrace();
                    selectedImageUri = null;
                    currentImageUri = null;
                }
            }
        }

        // Galerie
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
        btnSaveService.setOnClickListener(v -> saveService());

        return root;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void saveService() {
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

        String localPath;
        int imageResId = 0;

        if (selectedImageUri != null && !selectedImageUri.equals(currentImageUri)) {
            localPath = saveImageToInternalStorage(selectedImageUri);
        } else {
            //  garder l’ancienne si on n'a pas changer image
            localPath = currentImageUri != null ? currentImageUri.toString() : null;
            imageResId = (localPath == null) ? R.drawable.ic_noimage : 0;
        }

        int userId = getCurrentUserId();

        Service service = new Service(serviceId, category, title, description, imageResId, localPath, location, price, moreDetails, userId);

        if (serviceId == -1) {
            viewModel.insertService(service);
            Toast.makeText(requireContext(), "Service ajouté avec succès", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.updateService(service);
            Toast.makeText(requireContext(), "Service modifié avec succès", Toast.LENGTH_SHORT).show();
        }

        dismiss(); // fermer le DialogFragment
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

    private int getCurrentUserId() {
        return 1;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setLayout(width, height);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.white);
        }
    }
}
