package com.example.services_project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.services_project.R;

public class ServicesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "services_db";
    private static final int DB_VERSION = 14;

    public ServicesDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Création de la table
        db.execSQL("CREATE TABLE services (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category TEXT, " +
                "title TEXT, " +
                "description TEXT, " +
                "imageResId INTEGER, " +
                "imageUri TEXT, " +   // URI pour images choisies par l'utilisateur
                "location TEXT, " +
                "price TEXT, " +
                "moreDetails TEXT" +
                ")");

        // Services par défaut : INSERT séparés pour éviter les erreurs
        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails) VALUES " +
                "('COIFFURE', 'Coupe de Cheveux', 'Coupe de cheveux avec soins du cuir chevelu', " + R.drawable.ic_haircut + ", 'Salon Paris 12', '50€', 'Inclus shampoing et massage du cuir chevelu')");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails) VALUES " +
                "('PLOMBERIE', 'Réparation Évier', 'Réparation et entretien du système de plomberie pour un évier fonctionnel', " + R.drawable.ic_plumbing + ", 'Rue des Lilas, Lyon', '80€', 'Service rapide et garanti')");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails) VALUES " +
                "('MASSAGE', 'Massage Relaxant', 'Massage relaxant pour muscles endoloris', " + R.drawable.ic_massage + ", 'Maison du client', '60€', 'Durée 1h, huile incluse')");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails) VALUES " +
                "('ÉLECTRICIEN', 'Installation Éclairage', 'Installation d’éclairage électrique pour une belle ambiance', " + R.drawable.ic_electrician + ", 'Appartement Marseille', '100€', 'Matériel fourni')");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails) VALUES " +
                "('PÉDIATRIE', 'Consultation', 'Consultation avec un pédiatre qualifié', " + R.drawable.ic_pediatrics + ", 'Clinique Nice', '70€', 'Première consultation, suivi inclus')");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails) VALUES " +
                "('INFORMATIQUE', 'Développeur Web', 'Création de sites web modernes et performants', " + R.drawable.ic_developer + ", 'Casablanca', '150€', 'Site vitrine, e-commerce ou application web complète')");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails) VALUES " +
                "('DESIGN', 'Designer Graphique', 'Création de logos, affiches et supports visuels professionnels', " + R.drawable.ic_designer + ", 'Rabat', '120€', 'Identité visuelle et branding inclus')");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails) VALUES " +
                "('CUISINE', 'Chef Cuisinier à Domicile', 'Préparation de repas gastronomiques à domicile', " + R.drawable.ic_chef + ", 'Marrakech', '200€', 'Service complet, ingrédients inclus')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Exemple de migration : ajout de la colonne imageUri si ancienne version
        if (oldVersion < 13) {
            db.execSQL("ALTER TABLE services ADD COLUMN imageUri TEXT");
        }
        // Ne pas DROP TABLE pour conserver les services ajoutés par l'utilisateur
    }
}
