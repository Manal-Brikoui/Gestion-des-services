package com.example.services_project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.services_project.model.User;
import com.example.services_project.model.Message; // Assurez-vous d'importer votre modèle Message

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "services.db";
    private static final int DATABASE_VERSION = 19; // Version pour inclure les MESSAGES

    // --- Constantes TABLE_USERS ---
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FIRST_NAME = "firstName";
    public static final String COLUMN_LAST_NAME = "lastName";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FIRST_NAME + " TEXT, " +
                    COLUMN_LAST_NAME + " TEXT, " +
                    COLUMN_EMAIL + " TEXT UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT" +
                    ");";

    // -------------------------------------------------------------------------
    // CONSTANTES ET TABLE MESSAGES
    // -------------------------------------------------------------------------
    public static final String TABLE_MESSAGES = "messages";
    public static final String MESSAGE_COLUMN_ID = "id";
    public static final String MESSAGE_COLUMN_SENDER_ID = "sender_id";
    public static final String MESSAGE_COLUMN_RECEIVER_ID = "receiver_id";
    public static final String MESSAGE_COLUMN_CONTENT = "content";
    public static final String MESSAGE_COLUMN_TIMESTAMP = "timestamp";
    public static final String MESSAGE_COLUMN_IS_READ = "is_read";

    private static final String CREATE_TABLE_MESSAGES =
            "CREATE TABLE " + TABLE_MESSAGES + " (" +
                    MESSAGE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MESSAGE_COLUMN_SENDER_ID + " INTEGER NOT NULL, " +
                    MESSAGE_COLUMN_RECEIVER_ID + " INTEGER NOT NULL, " +
                    MESSAGE_COLUMN_CONTENT + " TEXT NOT NULL, " +
                    MESSAGE_COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    MESSAGE_COLUMN_IS_READ + " INTEGER DEFAULT 0, " + // 0=false, 1=true
                    "FOREIGN KEY(" + MESSAGE_COLUMN_SENDER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")," +
                    "FOREIGN KEY(" + MESSAGE_COLUMN_RECEIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")" +
                    ");";

    // -------------------------------------------------------------------------
    // CONSTRUCTEUR
    // -------------------------------------------------------------------------
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // -------------------------------------------------------------------------
    // MÉTHODES HÉRITÉES
    // -------------------------------------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating tables in " + DATABASE_NAME);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_MESSAGES);
        // TODO: Ajoutez ici la création de toutes vos autres tables (Services, Candidate, etc.)
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        // Logique pour ajouter la table MESSAGES si l'ancienne version est < 19
        if (oldVersion < 19) {
            db.execSQL(CREATE_TABLE_MESSAGES);
        }

        // Ancienne logique de suppression (si nécessaire pour les mises à jour majeures)
        if (newVersion > oldVersion && oldVersion < 18) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }

    // -------------------------------------------------------------------------
    // GESTION DES UTILISATEURS (CRUD)
    // -------------------------------------------------------------------------

    public boolean insertUser(String firstName, String lastName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?",
                new String[]{email}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_ID +
                        " FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public User getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?",
                new String[]{email}
        );
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
        }
        cursor.close();
        db.close();
        return user;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
        }
        cursor.close();
        db.close();
        return user;
    }

    /**
     * Récupère tous les utilisateurs sauf l'utilisateur courant (filtrage pour la liste de contacts).
     */
    public List<User> getAllUsers(int currentUserId) {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + " != ?", // Exclusion de l'utilisateur courant
                new String[]{String.valueOf(currentUserId)}
        );

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)));
                user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }

    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        int rows = db.update(
                TABLE_USERS,
                values,
                COLUMN_EMAIL + "=?",
                new String[]{email}
        );
        db.close();
        return rows > 0;
    }

    // -------------------------------------------------------------------------
    // GESTION DES MESSAGES (CRUD)
    // -------------------------------------------------------------------------

    public long insertMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MESSAGE_COLUMN_SENDER_ID, message.getSenderId());
        values.put(MESSAGE_COLUMN_RECEIVER_ID, message.getReceiverId());
        values.put(MESSAGE_COLUMN_CONTENT, message.getContent());

        long result = db.insert(TABLE_MESSAGES, null, values);
        db.close();
        return result;
    }

    /**
     * Récupère l'historique des messages échangés UNIQUEMENT entre user1Id et user2Id.
     */
    public List<Message> getMessages(int user1Id, int user2Id) {
        List<Message> messageList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Requête pour sélectionner les messages entre les deux utilisateurs, triés par TIMESTAMP
        String query = "SELECT * FROM " + TABLE_MESSAGES +
                " WHERE (" + MESSAGE_COLUMN_SENDER_ID + " = ? AND " + MESSAGE_COLUMN_RECEIVER_ID + " = ?)" +
                " OR (" + MESSAGE_COLUMN_SENDER_ID + " = ? AND " + MESSAGE_COLUMN_RECEIVER_ID + " = ?)" +
                " ORDER BY " + MESSAGE_COLUMN_TIMESTAMP + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(user1Id), String.valueOf(user2Id),
                String.valueOf(user2Id), String.valueOf(user1Id)
        });

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getInt(cursor.getColumnIndexOrThrow(MESSAGE_COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MESSAGE_COLUMN_SENDER_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MESSAGE_COLUMN_RECEIVER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_COLUMN_CONTENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_COLUMN_TIMESTAMP)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MESSAGE_COLUMN_IS_READ)) == 1
                );
                messageList.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return messageList;
    }
}