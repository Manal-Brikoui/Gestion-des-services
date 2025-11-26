package com.example.services_project.ui.dashboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.services_project.data.ServicesDatabaseHelper;
import com.example.services_project.model.Service;
import com.example.services_project.model.Candidate;

import java.util.ArrayList;
import java.util.List;

public class HomeFragmentRepository {

    private final ServicesDatabaseHelper dbHelper;
    private static final String TAG = "DB_REPO"; // Tag unifié pour le logging

    public HomeFragmentRepository(Context context) {
        dbHelper = new ServicesDatabaseHelper(context);
    }

    // ----------------- Services -----------------
    public List<Service> getAllServices() {
        List<Service> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
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
        } catch (Exception e) {
            Log.e(TAG, "Erreur getAllServices: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return list;
    }

    public void insertService(Service service) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Erreur insertService: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    public void updateService(Service service) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Erreur updateService: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    public void deleteService(int serviceId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete("services", "id = ?", new String[]{String.valueOf(serviceId)});
        } catch (Exception e) {
            Log.e(TAG, "Erreur deleteService: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    // ----------------- Candidatures (Notification) -----------------
    /**
     * Tente d'ajouter un candidat. Loggue une erreur claire si l'insertion échoue.
     */
    public void addCandidate(Candidate candidate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("serviceId", candidate.getServiceId());
            values.put("firstName", candidate.getFirstName());
            values.put("lastName", candidate.getLastName());
            values.put("dateTime", candidate.getDateTime());
            values.put("location", candidate.getLocation());
            values.put("phone", candidate.getPhone());
            values.put("email", candidate.getEmail());

            long result = db.insert("candidates", null, values);

            if (result == -1) {
                // Log d'erreur critique
                Log.e(TAG, "❌ ÉCHEC insertion candidat. Vérifiez les noms de table/colonnes ou les contraintes NOT NULL dans ServicesDatabaseHelper.");
            } else {
                Log.d(TAG, "✅ Succès insertion candidat. ID Ligne: " + result + ", Service ID: " + candidate.getServiceId());
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur inattendue lors de l'insertion du candidat: " + e.getMessage());
        } finally {
            db.close();
        }
    }


    /**
     * Récupère tous les candidats pour un service donné.
     * Utilise try-finally pour garantir la fermeture du Cursor/DB.
     */
    public List<Candidate> getCandidatesForService(int serviceId) {
        List<Candidate> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT serviceId, firstName, lastName, dateTime, location, phone, email FROM candidates WHERE serviceId = ?";
            String[] selectionArgs = new String[]{String.valueOf(serviceId)};

            cursor = db.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                do {
                    list.add(new Candidate(
                            cursor.getInt(cursor.getColumnIndexOrThrow("serviceId")),
                            cursor.getString(cursor.getColumnIndexOrThrow("firstName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("lastName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("dateTime")),
                            cursor.getString(cursor.getColumnIndexOrThrow("location")),
                            cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                            cursor.getString(cursor.getColumnIndexOrThrow("email"))
                    ));
                } while (cursor.moveToNext());
            }

            if (list.isEmpty()) {
                Log.w(TAG, "Aucun candidat trouvé pour le service ID: " + serviceId);
            }

        } catch (Exception e) {
            Log.e(TAG, "Erreur de lecture des candidats pour ID " + serviceId + ": " + e.getMessage());
            // En cas d'erreur de lecture (ex: colonne manquante), on s'assure de retourner vide.
            list.clear();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return list;
    }

    // ----------------- Notifications (Nouveau) -----------------
    /**
     * Récupère tous les candidats pour tous les services postés par un utilisateur donné (userId).
     * @param userId L'ID de l'utilisateur dont on veut voir les notifications.
     */
    public List<Candidate> getAllCandidatesForUserServices(int userId) {
        List<Candidate> notifications = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        // Requête SQL utilisant INNER JOIN pour lier les candidatures aux services de l'utilisateur
        String query = "SELECT c.serviceId, c.firstName, c.lastName, c.dateTime, c.location, c.phone, c.email " +
                "FROM candidates c " +
                "INNER JOIN services s ON c.serviceId = s.id " +
                "WHERE s.userId = ?";
        String[] selectionArgs = new String[]{String.valueOf(userId)};

        try {
            cursor = db.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                do {
                    notifications.add(new Candidate(
                            cursor.getInt(cursor.getColumnIndexOrThrow("serviceId")),
                            cursor.getString(cursor.getColumnIndexOrThrow("firstName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("lastName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("dateTime")),
                            cursor.getString(cursor.getColumnIndexOrThrow("location")),
                            cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                            cursor.getString(cursor.getColumnIndexOrThrow("email"))
                            // Remarque : Le titre du service n'est pas inclus ici car le modèle Candidate
                            // ne semble pas avoir de champ pour cela. Si nécessaire, il faudrait modifier le modèle.
                    ));
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Notifications récupérées pour user ID " + userId + ": " + notifications.size() + " résultats.");

        } catch (Exception e) {
            Log.e(TAG, "Erreur de lecture des notifications pour user ID " + userId + ": " + e.getMessage());
            notifications.clear();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return notifications;
    }
}