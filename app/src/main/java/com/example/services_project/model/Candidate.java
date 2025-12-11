package com.example.services_project.model;

public class Candidate {

    private int id;
    private int applicantId;
    private int serviceId;
    private String firstName;
    private String lastName;
    private String dateTime;
    private String applicationDate; //  La date et heure où l'application a été créée/postulée
    private String location;
    private String phone;
    private String email;
    private String serviceTitle;
    private String status;

    //  Constructeur
    public Candidate(int id, int applicantId, int serviceId, String firstName, String lastName, String dateTime,
                     String applicationDate, String location, String phone, String email, String serviceTitle, String status) {
        this.id = id;
        this.applicantId = applicantId;
        this.serviceId = serviceId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateTime = dateTime;
        this.applicationDate = applicationDate;
        this.location = location;
        this.phone = phone;
        this.email = email;
        this.serviceTitle = serviceTitle;
        this.status = status;
    }
//Constructeur
    public Candidate(int applicantId, int serviceId, String firstName, String lastName, String dateTime,
                     String applicationDate, String location, String phone, String email) {
        this(0, applicantId, serviceId, firstName, lastName, dateTime, applicationDate, location, phone, email, null, "PENDING");
    }

    public Candidate(int id, int applicantId, int serviceId, String firstName, String lastName, String dateTime,
                     String applicationDate, String location, String phone, String email, String status) {
        this(id, applicantId, serviceId, firstName, lastName, dateTime, applicationDate, location, phone, email, null, status);
    }

    // Constructeur
    public Candidate(int serviceId, String firstName, String lastName, String dateTime,
                     String location, String phone, String email) {
        this(0, 0, serviceId, firstName, lastName, dateTime, null, location, phone, email, null, "PENDING"); // applicationDate ajouté comme null ici
    }

    //  GETTERS
    public int getId() { return id; }
    public int getApplicantId() { return applicantId; }
    public int getServiceId() { return serviceId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDateTime() { return dateTime; }
    public String getApplicationDate() { return applicationDate; } // NOUVEAU
    public String getLocation() { return location; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getServiceTitle() { return serviceTitle; }
    public String getStatus() { return status; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setApplicantId(int applicantId) { this.applicantId = applicantId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public void setApplicationDate(String applicationDate) { this.applicationDate = applicationDate; }
    public void setLocation(String location) { this.location = location; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setServiceTitle(String serviceTitle) { this.serviceTitle = serviceTitle; }
    public void setStatus(String status) { this.status = status; }
}