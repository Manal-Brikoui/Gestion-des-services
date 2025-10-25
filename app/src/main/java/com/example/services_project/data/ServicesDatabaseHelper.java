package com.example.services_project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import com.example.services_project.model.Service; // Importez votre modèle Service
import java.util.ArrayList;
import java.util.List;

public class ServicesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "services.db";
    private static final int DATABASE_VERSION = 1;

    // Constantes pour la table SERVICES
    public static final String TABLE_SERVICES = "services";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SERVICE_CATEGORY = "category";
    public static final String COLUMN_SERVICE_TITLE = "title";
    public static final String COLUMN_SERVICE_DESCRIPTION = "description";
    public static final String COLUMN_SERVICE_ICON_ID = "icon_id";

    private static final String CREATE_TABLE_SERVICES =
            "CREATE TABLE " + TABLE_SERVICES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SERVICE_CATEGORY + " TEXT, " +
                    COLUMN_SERVICE_TITLE + " TEXT, " +
                    COLUMN_SERVICE_DESCRIPTION + " TEXT, " +
                    COLUMN_SERVICE_ICON_ID + " INTEGER);";

    public ServicesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SERVICES);
        insertInitialServices(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
        onCreate(db);
    }

    private void insertService(SQLiteDatabase db, String category, String title, String description, int iconId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVICE_CATEGORY, category);
        values.put(COLUMN_SERVICE_TITLE, title);
        values.put(COLUMN_SERVICE_DESCRIPTION, description);
        values.put(COLUMN_SERVICE_ICON_ID, iconId);
        db.insert(TABLE_SERVICES, null, values);
    }

    private void insertInitialServices(SQLiteDatabase db) {
        // --- DONNÉES DE VOTRE INTERFACE ---
        // ATTENTION: Remplacer '0' par R.drawable.votre_icone_de_service
        insertService(db, "COIFFURE", "Coupe de Cheveux", "Coupe de cheveux avec soins du cuir chevelu", 0);
        insertService(db, "PLOMBERIE", "Plomberie - Réparation Évier", "Réparation et entretien du système de plomberie pour un évier fonctionnel", 0);
        insertService(db, "MÉNAGE", "Massage Relaxant", "massage relaxant pour muscles endoloris", 0);
        insertService(db, "ÉLECTRICIEN", "Électricien - Installation Éclairage", "Installation d'éclairage électrique pour une belle ambiance", 0);
        insertService(db, "PÉDIATRIE", "Pédiatrie - Consulting", "Consultation avec un pédiatre qualifié", 0);
    }

    // Méthode pour récupérer tous les services
    public List<Service> getAllServices() {
        List<Service> serviceList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SERVICES,
                new String[]{COLUMN_SERVICE_CATEGORY, COLUMN_SERVICE_TITLE, COLUMN_SERVICE_DESCRIPTION, COLUMN_SERVICE_ICON_ID},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                // IMPORTANT: Utiliser getColumnIndexOrThrow pour la sécurité
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_CATEGORY));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_DESCRIPTION));
                int iconId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_ICON_ID));

                Service service = new Service(category, title, description, iconId);
                serviceList.add(service);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return serviceList;
    }
}