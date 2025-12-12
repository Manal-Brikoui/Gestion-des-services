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
    private static final String TAG = "DB_REPO";

    public HomeFragmentRepository(Context context) {
        dbHelper = new ServicesDatabaseHelper(context);
    }


    public List<Service> getAllServices() {
        List<Service> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT id, category, title, description, imageResId, imageUri, location, price, moreDetails, userId FROM services",
                null
        )) {
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
            db.close();
        }
        return list;
    }

    /*
     Méthode pour récupérer les services postés par un utilisateur spécifique.
     */
    public List<Service> getServicesByUser(int userId) {
        List<Service> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT id, category, title, description, imageResId, imageUri, location, price, moreDetails, userId FROM services WHERE userId = ?",
                new String[]{String.valueOf(userId)}
        )) {

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
            Log.e(TAG, "Erreur getServicesByUser: " + e.getMessage());
        } finally {
            db.close();
        }
        return list;
    }


    public void insertService(Service service) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = serviceToContentValues(service);
            db.insert("services", null, values);
        } catch (Exception e) {
            Log.e(TAG, "Erreur insertService: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    /*
     Méthode pour mettre à jour un service existant.
     */
    public void updateService(Service service) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = serviceToContentValues(service);

            int rowsAffected = db.update("services", values, "id = ?", new String[]{String.valueOf(service.getId())});

            if (rowsAffected > 0) {
                Log.d(TAG, " Service ID " + service.getId() + " mis à jour.");
            } else {
                Log.w(TAG, "Service ID " + service.getId() + " non trouvé pour mise à jour.");
            }

        } catch (Exception e) {
            Log.e(TAG, "Erreur updateService: " + e.getMessage());
        } finally {
            db.close();
        }
    }


    /*
     Méthode utilitaire pour créer ContentValues à partir d'un objet Service.
     */
    private ContentValues serviceToContentValues(Service service) {
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
        return values;
    }

    /*
      Méthode pour supprimer un service.
     */
    public void deleteService(int serviceId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int rowsAffected = db.delete("services", "id = ?", new String[]{String.valueOf(serviceId)});

            if (rowsAffected > 0) {
                Log.d(TAG, " Service ID " + serviceId + " supprimé. " + rowsAffected + " lignes affectées.");
            } else {
                Log.w(TAG, "Service ID " + serviceId + " non trouvé pour suppression.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur deleteService: " + e.getMessage());
        } finally {
            db.close();
        }
    }


    //  Candidatures

    public void addCandidate(Candidate candidate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("serviceId", candidate.getServiceId());
            values.put("applicantId", candidate.getApplicantId());
            values.put("firstName", candidate.getFirstName());
            values.put("lastName", candidate.getLastName());
            values.put("dateTime", candidate.getDateTime());
            values.put("location", candidate.getLocation());
            values.put("phone", candidate.getPhone());
            values.put("email", candidate.getEmail());
            values.put("status", "PENDING");

            db.insert("candidates", null, values);
        } catch (Exception e) {
            Log.e(TAG, "Erreur inattendue lors de l'insertion du candidat: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    @Deprecated
    public void updateCandidateStatus(int candidateId, String newStatus) {

        updateCandidateStatusWithDate(candidateId, newStatus, null);
    }


    public void updateCandidateStatusWithDate(int candidateId, String newStatus, String currentDateTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("status", newStatus);

            if (currentDateTime != null) {
                values.put("applicationDate", currentDateTime);
            }

            int rowsAffected = db.update("candidates", values, "id = ?", new String[]{String.valueOf(candidateId)});

            if (rowsAffected > 0) {
                Log.d(TAG, " Candidature ID " + candidateId + " mise à jour à " + newStatus + " avec date.");
            }

        } catch (Exception e) {
            Log.e(TAG, "Erreur updateCandidateStatusWithDate: " + e.getMessage());
        } finally {
            db.close();
        }
    }


    public List<Candidate> getCandidatesForService(int serviceId) {
        List<Candidate> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT c.id, c.applicantId, c.serviceId, c.firstName, c.lastName, c.dateTime, c.applicationDate, c.location, c.phone, c.email, c.status, s.title AS serviceTitle " +
                "FROM candidates c INNER JOIN services s ON c.serviceId = s.id WHERE c.serviceId = ?";
        String[] selectionArgs = new String[]{String.valueOf(serviceId)};

        try (Cursor cursor = db.rawQuery(query, selectionArgs)) {

            if (cursor.moveToFirst()) {
                do {
                    list.add(new Candidate(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("applicantId")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("serviceId")),
                            cursor.getString(cursor.getColumnIndexOrThrow("firstName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("lastName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("dateTime")),

                            cursor.getString(cursor.getColumnIndexOrThrow("applicationDate")),
                            cursor.getString(cursor.getColumnIndexOrThrow("location")),
                            cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                            cursor.getString(cursor.getColumnIndexOrThrow("email")),
                            cursor.getString(cursor.getColumnIndexOrThrow("serviceTitle")),
                            cursor.getString(cursor.getColumnIndexOrThrow("status"))
                    ));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur de lecture des candidats pour ID " + serviceId + ": " + e.getMessage());
            list.clear();
        } finally {
            db.close();
        }
        return list;
    }

    public List<Candidate> getAllCandidatesForUserServices(int userId) {
        List<Candidate> notifications = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT c.id, c.applicantId, c.serviceId, c.firstName, c.lastName, c.dateTime, c.applicationDate, c.location, c.phone, c.email, c.status, s.title AS serviceTitle " +
                "FROM candidates c " +
                "INNER JOIN services s ON c.serviceId = s.id " +
                "WHERE s.userId = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)})) {
            if (cursor.moveToFirst()) {
                do {
                    notifications.add(new Candidate(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("applicantId")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("serviceId")),
                            cursor.getString(cursor.getColumnIndexOrThrow("firstName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("lastName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("dateTime")),
                            cursor.getString(cursor.getColumnIndexOrThrow("applicationDate")),
                            cursor.getString(cursor.getColumnIndexOrThrow("location")),
                            cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                            cursor.getString(cursor.getColumnIndexOrThrow("email")),
                            cursor.getString(cursor.getColumnIndexOrThrow("serviceTitle")),
                            cursor.getString(cursor.getColumnIndexOrThrow("status"))
                    ));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur de lecture des notifications reçues: " + e.getMessage());
        } finally {
            db.close();
        }
        return notifications;
    }

    public List<Candidate> getCandidatesPostedByUser(int applicantId) {
        List<Candidate> notifications = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT c.id, c.applicantId, c.serviceId, c.firstName, c.lastName, c.dateTime, c.applicationDate, c.location, c.phone, c.email, c.status, s.title AS serviceTitle " +
                "FROM candidates c " +
                "INNER JOIN services s ON c.serviceId = s.id " +
                "WHERE c.applicantId = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(applicantId)})) {
            if (cursor.moveToFirst()) {
                do {
                    notifications.add(new Candidate(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("applicantId")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("serviceId")),
                            cursor.getString(cursor.getColumnIndexOrThrow("firstName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("lastName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("dateTime")),
                            cursor.getString(cursor.getColumnIndexOrThrow("applicationDate")),
                            cursor.getString(cursor.getColumnIndexOrThrow("location")),
                            cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                            cursor.getString(cursor.getColumnIndexOrThrow("email")),
                            cursor.getString(cursor.getColumnIndexOrThrow("serviceTitle")),
                            cursor.getString(cursor.getColumnIndexOrThrow("status"))
                    ));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur de lecture des candidatures soumises: " + e.getMessage());
        } finally {
            db.close();
        }
        return notifications;
    }
}