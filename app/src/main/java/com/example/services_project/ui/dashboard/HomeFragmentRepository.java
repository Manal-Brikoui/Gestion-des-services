package com.example.services_project.ui.dashboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.services_project.data.ServicesDatabaseHelper;
import com.example.services_project.model.Service;

import java.util.ArrayList;
import java.util.List;

public class HomeFragmentRepository {

    private ServicesDatabaseHelper dbHelper;

    public HomeFragmentRepository(Context context) {
        dbHelper = new ServicesDatabaseHelper(context);
    }

    // ----------------- Récupérer tous les services -----------------
    public List<Service> getAllServices() {
        List<Service> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // On récupère maintenant aussi imageUri
        Cursor cursor = db.rawQuery(
                "SELECT id, category, title, description, imageResId, imageUri, location, price, moreDetails FROM services",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("imageResId"));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")); // <- nouveau

                list.add(new Service(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        imageResId,
                        imageUri, // <- nouveau champ
                        cursor.getString(cursor.getColumnIndexOrThrow("location")),
                        cursor.getString(cursor.getColumnIndexOrThrow("price")),
                        cursor.getString(cursor.getColumnIndexOrThrow("moreDetails"))
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    // ----------------- Ajouter un nouveau service -----------------
    public void insertService(Service service) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category", service.getCategory());
        values.put("title", service.getTitle());
        values.put("description", service.getDescription());
        values.put("imageResId", service.getImageResId());
        values.put("imageUri", service.getImageUri()); // <- nouveau
        values.put("location", service.getLocation());
        values.put("price", service.getPrice());
        values.put("moreDetails", service.getMoreDetails());

        db.insert("services", null, values);
        db.close();
    }
}
