package com.example.services_project.model;

public class Candidate {
    private int serviceId;       // ID du service auquel le candidat a postulé
    private String firstName;
    private String lastName;
    private String dateTime;
    private String location;
    private String phone;
    private String email;
    // NOUVEAU CHAMP POUR LE TITRE DU SERVICE
    private String serviceTitle;

    // Constructeur (Mis à jour pour inclure serviceTitle, si possible)
    public Candidate(int serviceId, String firstName, String lastName, String dateTime,
                     String location, String phone, String email, String serviceTitle) {
        this.serviceId = serviceId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateTime = dateTime;
        this.location = location;
        this.phone = phone;
        this.email = email;
        this.serviceTitle = serviceTitle; // Initialisation du nouveau champ
    }

    // Si vous devez garder l'ancien constructeur pour la rétrocompatibilité (et serviceTitle sera mis à jour plus tard)
    public Candidate(int serviceId, String firstName, String lastName, String dateTime,
                     String location, String phone, String email) {
        this.serviceId = serviceId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateTime = dateTime;
        this.location = location;
        this.phone = phone;
        this.email = email;
        this.serviceTitle = null; // Initialisé à null par défaut
    }


    // Getters

    public int getServiceId() { return serviceId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDateTime() { return dateTime; }
    public String getLocation() { return location; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    // NOUVEAU GETTER
    public String getServiceTitle() { return serviceTitle; }


    // Setters

    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public void setLocation(String location) { this.location = location; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    // NOUVEAU SETTER
    public void setServiceTitle(String serviceTitle) { this.serviceTitle = serviceTitle; }
}