package com.example.services_project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.services_project.R;

public class ServicesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "services_db";
    private static final int DB_VERSION = 54;

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
                "userId INTEGER DEFAULT 0" + // ID de l'auteur du service
                ")");

        // Table candidates
        db.execSQL("CREATE TABLE candidates (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "serviceId INTEGER, " +
                "applicantId INTEGER, " +
                "firstName TEXT, " +
                "lastName TEXT, " +
                "dateTime TEXT, " +
                "applicationDate TEXT DEFAULT (DATETIME('now','localtime')), " +
                "location TEXT, " +
                "phone TEXT, " +
                "email TEXT," +
                "status TEXT DEFAULT 'PENDING'" +
                ")");

        insertDefaultServices(db);//SERVICES PAR DÉFAUT
    }

    private void insertDefaultServices(SQLiteDatabase db) {
        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('COIFFURE', 'Coupe de Cheveux', 'Offrez-vous une coupe de cheveux professionnelle, adaptée à votre style et à la forme de votre visage, accompagnée de soins du cuir chevelu pour revitaliser et hydrater vos cheveux en profondeur. Chaque séance inclut un diagnostic personnalisé de la santé capillaire, des conseils de coiffage pour entretenir votre look au quotidien. ', " +
                R.drawable.ic_haircut + ", 'Salon Paris 12', '50€', 'Inclus shampoing et massage du cuir chevelu', 1)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('PLOMBERIE', 'Réparation Évier', 'Intervention complète sur votre système de plomberie pour restaurer le bon fonctionnement de votre évier, incluant le débouchage, le remplacement des joints ou pièces défectueuses, et des conseils pour éviter les problèmes futurs. Service rapide, fiable et garanti pour votre tranquillité', " +
                R.drawable.ic_plumbing + ", 'Rue des Lilas, Lyon', '80€', 'Service rapide et garanti', 0)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('MASSAGE', 'Massage Relaxant', 'Profitez d’un massage relaxant spécialement conçu pour détendre vos muscles endoloris et réduire les tensions accumulées. Cette séance allie différentes techniques manuelles pour stimuler la circulation sanguine, apaiser le stress et favoriser une sensation de bien-être général', " +
                R.drawable.ic_massage + ", 'Maison du client', '60€', 'Durée 1h, huile incluse', 0)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('ÉLECTRICIEN', 'Installation Éclairage', 'Service complet d’installation d’éclairage pour votre intérieur ou extérieur, visant à créer une ambiance chaleureuse et fonctionnelle. Nous prenons en charge la planification, le câblage sécurisé, le choix des luminaires adaptés à votre espace, et le réglage précis de l’intensité lumineuse pour un résultat esthétique et pratique.', " +
                R.drawable.ic_electrician + ", 'Appartement Marseille', '100€', 'Matériel fourni', 0)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('PÉDIATRIE', 'Consultation', 'Bénéficiez d’une consultation complète avec un pédiatre expérimenté, prenant le temps d’évaluer la santé physique et le développement global de votre enfant. La séance inclut un examen médical approfondi, des conseils personnalisés sur la nutrition, le sommeil.', " +
                R.drawable.ic_pediatrics + ", 'Clinique Nice', '70€', 'Première consultation, suivi inclus', 0)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('INFORMATIQUE', 'Développeur Web', 'Nous vous proposons la conception et le développement de sites web modernes, performants et adaptés à vos besoins. Chaque projet inclut une analyse approfondie de vos objectifs, la mise en place d’une architecture optimisée pour la rapidité et le référencement, ainsi que la création d’un design attractif et responsive.', " +
                R.drawable.ic_developer + ", 'Casablanca', '150€', 'Site vitrine ou application web complète', 0)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('DESIGN', 'Designer Graphique', 'Nous proposons des services complets de design graphique pour donner vie à votre identité visuelle. Cela inclut la création de logos uniques, d’affiches percutantes, de brochures et de tous types de supports visuels professionnels adaptés à vos besoins. Chaque projet est réalisé en tenant compte de votre image de marque, du message à transmettre et de votre public cible.', " +
                R.drawable.ic_designer + ", 'Rabat', '120€', 'Identité visuelle et branding inclus', 0)");

        db.execSQL("INSERT INTO services (category, title, description, imageResId, location, price, moreDetails, userId) VALUES " +
                "('CUISINE', 'Chef à Domicile', 'Profitez d’une expérience culinaire unique avec un chef à domicile qui prépare des repas gastronomiques sur mesure selon vos goûts et vos besoins. Chaque plat est réalisé avec des ingrédients frais et de qualité, en respectant les techniques culinaires les plus raffinées. Le service inclut la planification du menu, la préparation et la présentation soignée des plats, ainsi que des conseils pour accompagner vos repas de manière optimale.', " +
                R.drawable.ic_chef + ", 'Marrakech', '200€', 'Ingrédients inclus', 0)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 39) {
            db.execSQL("DROP TABLE IF EXISTS services");
            db.execSQL("DROP TABLE IF EXISTS candidates");
            onCreate(db);
        }
    }
}