package com.example.musicapp.models;

public class User {
    private String fullName;
    private String gender;
    private String birthdate;
    private String email;

    // Required empty constructor for Firebase Firestore serialization
    public User() {
    }

    // Constructor with profile data only
    public User(String fullName, String gender, String birthdate, String email) {
        this.fullName = fullName;
        this.gender = gender;
        this.birthdate = birthdate;
        this.email = email;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}