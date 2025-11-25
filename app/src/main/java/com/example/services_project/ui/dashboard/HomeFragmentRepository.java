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

    private final ServicesDatabaseHelper dbHelper;

    public HomeFragmentRepository(Context context) {
        dbHelper = new ServicesDatabaseHelper(context);
    }

    // ----------------- Récupérer tous les services -----------------
    public List<Service> getAllServices() {
        List<Service> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, category, title, description, imageResId, imageUri, location, price, moreDetails, userId FROM services",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(new Service(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("imageResId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                        cursor.getString(cursor.getColumnIndexOrThrow("location")),
                        cursor.getString(cursor.getColumnIndexOrThrow("price")),
                        cursor.getString(cursor.getColumnIndexOrThrow("moreDetails")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("userId"))
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
        values.put("imageUri", service.getImageUri());
        values.put("location", service.getLocation());
        values.put("price", service.getPrice());
        values.put("moreDetails", service.getMoreDetails());
        values.put("userId", service.getUserId());

        db.insert("services", null, values);
        db.close();
    }

    // ----------------- Modifier un service existant -----------------
    public void updateService(Service service) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category", service.getCategory());
        values.put("title", service.getTitle());
        values.put("description", service.getDescription());
        values.put("imageResId", service.getImageResId());
        values.put("imageUri", service.getImageUri());
        values.put("location", service.getLocation());
        values.put("price", service.getPrice());
        values.put("moreDetails", service.getMoreDetails());
        values.put("userId", service.getUserId());

        db.update("services", values, "id = ?", new String[]{String.valueOf(service.getId())});
        db.close();
    }

    // ----------------- Supprimer un service -----------------
    public void deleteService(int serviceId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("services", "id = ?", new String[]{String.valueOf(serviceId)});
        db.close();
    }
}
