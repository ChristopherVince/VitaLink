package com.devcv.vitalink.models;

public class User {
    private String fullName;
    private String email;
    private String role;
    // No necesitamos getters/setters para los arrays aquí, solo para la data que mostramos

    public User() {} // Constructor vacío

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}