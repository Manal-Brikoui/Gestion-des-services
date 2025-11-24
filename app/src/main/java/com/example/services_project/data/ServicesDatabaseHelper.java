package com.example.services_project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.services_project.R;

public class ServicesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "services_db";
    private static final int DB_VERSION = 12; // <- incrémenter la version

    public ServicesDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE services (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category TEXT, " +
                "title TEXT, " +
                "description TEXT, " +
                "imageResId INTEGER, " +
                "imageUri TEXT, " +           // <- ajout colonne pour URI
                "location TEXT, " +
                "price TEXT, " +
                "moreDetails TEXT)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails) VALUES " +
                "('COIFFURE', 'Coupe de Cheveux', 'Coupe de cheveux avec soins du cuir chevelu', " + R.drawable.ic_haircut + ", 'Salon Paris 12', '50€', 'Inclus shampoing et massage du cuir chevelu'), " +
                "('PLOMBERIE', 'Réparation Évier', 'Réparation et entretien du système de plomberie pour un évier fonctionnel', " + R.drawable.ic_plumbing + ", 'Rue des Lilas, Lyon', '80€', 'Service rapide et garanti'), " +
                "('MASSAGE', 'Massage Relaxant', 'Massage relaxant pour muscles endoloris', " + R.drawable.ic_massage + ", 'Maison du client', '60€', 'Durée 1h, huile incluse'), " +
                "('ÉLECTRICIEN', 'Installation Éclairage', 'Installation d’éclairage électrique pour une belle ambiance', " + R.drawable.ic_electrician + ", 'Appartement Marseille', '100€', 'Matériel fourni'), " +
                "('PÉDIATRIE', 'Consultation', 'Consultation avec un pédiatre qualifié', " + R.drawable.ic_pediatrics + ", 'Clinique Nice', '70€', 'Première consultation, suivi inclus'), " +
                "('INFORMATIQUE', 'Développeur Web', 'Création de sites web modernes et performants', " + R.drawable.ic_developer + ", 'Casablanca', '150€', 'Site vitrine, e-commerce ou application web complète'), " +
                "('DESIGN', 'Designer Graphique', 'Création de logos, affiches et supports visuels professionnels', " + R.drawable.ic_designer + ", 'Rabat', '120€', 'Identité visuelle et branding inclus'), " +
                "('CUISINE', 'Chef Cuisinier à Domicile', 'Préparation de repas gastronomiques à domicile', " + R.drawable.ic_chef + ", 'Marrakech', '200€', 'Service complet, ingrédients inclus')"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS services");
        onCreate(db);
    }
}
