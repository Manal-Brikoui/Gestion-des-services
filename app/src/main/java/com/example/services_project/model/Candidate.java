package com.example.services_project.model;

public class Candidate {
    private int serviceId;       // ID du service auquel le candidat a postulé
    private String firstName;
    private String lastName;
    private String dateTime;
    private String location;
    private String phone;
    private String email;

    // Constructeur
    public Candidate(int serviceId, String firstName, String lastName, String dateTime,
                     String location, String phone, String email) {
        this.serviceId = serviceId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateTime = dateTime;
        this.location = location;
        this.phone = phone;
        this.email = email;
    }

    // Getters
    public int getServiceId() { return serviceId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDateTime() { return dateTime; }
    public String getLocation() { return location; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    // Setters


    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public void setLocation(String location) { this.location = location; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
}
