package com.devcv.vitalink.models;

public class Appointment {
    // --- Campos que coinciden con los de Firestore ---
    private String patientName;
    private String time;
    private String reason;
    private String date;
    private String doctorId;
    private String patientId;

    // --- Constructores ---
    // Constructor vacío, es OBLIGATORIO para que Firestore pueda crear objetos
    public Appointment() {}

    // --- Getters y Setters ---
    // Firestore los usa para leer y escribir los datos
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
