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

        // Initialiser les champs
        edtTitle = root.findViewById(R.id.edtTitle);
        edtDescription = root.findViewById(R.id.edtDescription);
        edtLocation = root.findViewById(R.id.edtLocation);
        edtPrice = root.findViewById(R.id.edtPrice);
        edtMoreDetails = root.findViewById(R.id.edtMoreDetails);
        spinnerCategory = root.findViewById(R.id.spinnerCategory);
        btnAddService = root.findViewById(R.id.btnAddService);
        imgService = root.findViewById(R.id.imgService);

        // Spinner catégories
        String[] categories = {"COIFFURE", "PLOMBERIE", "MASSAGE", "ÉLECTRICIEN", "PÉDIATRIE", "INFORMATIQUE", "DESIGN", "CUISINE"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterSpinner);

        // Launcher pour la galerie
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

        int imageResId = selectedImageUri == null ? R.drawable.ic_haircut : 0;
        String imageUriStr = selectedImageUri != null ? selectedImageUri.toString() : null;

        Service newService = new Service(
                0,
                category,
                title,
                description,
                imageResId,
                imageUriStr,
                location,
                price,
                moreDetails
        );

        viewModel.insertService(newService);

        Toast.makeText(requireContext(), "Service ajouté avec succès", Toast.LENGTH_SHORT).show();

        // Réinitialiser le formulaire
        edtTitle.setText("");
        edtDescription.setText("");
        edtLocation.setText("");
        edtPrice.setText("");
        edtMoreDetails.setText("");
        spinnerCategory.setSelection(0);
        imgService.setImageResource(R.drawable.ic_haircut);
        selectedImageUri = null;
    }
}
