package com.example.services_project.model;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public User() {}

    // Constructeur utilisé pour l'enregistrement (sans ID)
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Constructeur complet (utile lors de la récupération depuis la DB)
    public User(int id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // --- Getters / Setters (EXISTANTS) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // ----------------------------------------------------------------------
    // --- NOUVELLES MÉTHODES UTILITAIRES POUR L'AFFICHAGE DE LA MESSAGERIE ---
    // ----------------------------------------------------------------------

    /**
     * Retourne le nom complet (Prénom Nom).
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Retourne la première lettre du prénom en majuscule, pour l'icône circulaire.
     * @return L'initiale du prénom ou "?" si le prénom est vide.
     */
    public String getInitial() {
        if (firstName != null && !firstName.isEmpty()) {
            return firstName.substring(0, 1).toUpperCase();
        }
        return "?";
    }
}
