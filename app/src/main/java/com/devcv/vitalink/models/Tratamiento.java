package com.devcv.vitalink.models;

import com.google.firebase.firestore.Exclude;

public class Tratamiento {
    private String nombreMedicamento;
    private String dosis;
    private String horaInicio;
    private int frecuenciaHoras;

    @Exclude
    private String idDocumento;

    // Constructor vacío requerido para Firestore
    public Tratamiento() {}

    public Tratamiento(String nombreMedicamento, String dosis, String horaInicio, int frecuenciaHoras) {
        this.nombreMedicamento = nombreMedicamento;
        this.dosis = dosis;
        this.horaInicio = horaInicio;
        this.frecuenciaHoras = frecuenciaHoras;
    }

    // Getters y Setters
    public String getNombreMedicamento() { return nombreMedicamento; }
    public void setNombreMedicamento(String nombreMedicamento) { this.nombreMedicamento = nombreMedicamento; }

    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public int getFrecuenciaHoras() { return frecuenciaHoras; }
    public void setFrecuenciaHoras(int frecuenciaHoras) { this.frecuenciaHoras = frecuenciaHoras; }

    public String getIdDocumento() { return idDocumento; }
    public void setIdDocumento(String idDocumento) { this.idDocumento = idDocumento; }
}