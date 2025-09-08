package com.devcv.vitalink.models;

public class Medico {
    private String fullName;
    private String specialty;
    // Podríamos añadir más campos en el futuro, como la dirección del consultorio, etc.

    public Medico() {} // Constructor vacío para Firestore

    public Medico(String fullName, String specialty) {
        this.fullName = fullName;
        this.specialty = specialty;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}