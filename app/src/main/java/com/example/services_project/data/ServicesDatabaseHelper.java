package com.example.services_project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.services_project.R;

public class ServicesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "services_db";
    private static final int DB_VERSION = 26; // Nouvelle version pour recréer la DB proprement

    public ServicesDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Table services
        db.execSQL("CREATE TABLE services (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category TEXT, " +
                "title TEXT, " +
                "description TEXT, " +
                "imageResId INTEGER DEFAULT 0, " +
                "imageUri TEXT, " +
                "location TEXT, " +
                "price TEXT, " +
                "moreDetails TEXT, " +
                "userId INTEGER DEFAULT 0" +
                ")");

        // Table candidates
        db.execSQL("CREATE TABLE candidates (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "serviceId INTEGER, " +
                "firstName TEXT, " +
                "lastName TEXT, " +
                "dateTime TEXT, " +
                "location TEXT, " +
                "phone TEXT, " +
                "email TEXT" +
                ")");

        // ------- SERVICES PAR DÉFAUT (images DRAWABLE) -------
        insertDefaultServices(db);
    }

    private void insertDefaultServices(SQLiteDatabase db) {
        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('COIFFURE', 'Coupe de Cheveux', 'Coupe de cheveux avec soins du cuir chevelu', " +
                R.drawable.ic_haircut + ", 'Salon Paris 12', '50€', 'Inclus shampoing et massage du cuir chevelu', 0)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('PLOMBERIE', 'Réparation Évier', 'Réparation du système de plomberie pour un évier fonctionnel', " +
                R.drawable.ic_plumbing + ", 'Rue des Lilas, Lyon', '80€', 'Service rapide et garanti', 0)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('MASSAGE', 'Massage Relaxant', 'Massage relaxant pour muscles endoloris', " +
                R.drawable.ic_massage + ", 'Maison du client', '60€', 'Durée 1h, huile incluse', 0)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('ÉLECTRICIEN', 'Installation Éclairage', 'Installation d’éclairage électrique pour une belle ambiance', " +
                R.drawable.ic_electrician + ", 'Appartement Marseille', '100€', 'Matériel fourni', 0)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('PÉDIATRIE', 'Consultation', 'Consultation avec un pédiatre qualifié', " +
                R.drawable.ic_pediatrics + ", 'Clinique Nice', '70€', 'Première consultation, suivi inclus', 0)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('INFORMATIQUE', 'Développeur Web', 'Création de sites web modernes et performants', " +
                R.drawable.ic_developer + ", 'Casablanca', '150€', 'Site vitrine ou application web complète', 0)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('DESIGN', 'Designer Graphique', 'Création de logos, affiches et supports visuels professionnels', " +
                R.drawable.ic_designer + ", 'Rabat', '120€', 'Identité visuelle et branding inclus', 0)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('CUISINE', 'Chef à Domicile', 'Préparation de repas gastronomiques à domicile', " +
                R.drawable.ic_chef + ", 'Marrakech', '200€', 'Ingrédients inclus', 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Supprime tables et recrée tout
        db.execSQL("DROP TABLE IF EXISTS services");
        db.execSQL("DROP TABLE IF EXISTS candidates");
        onCreate(db);
    }
}
