package com.example.pm01_examenp3.clases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Blob;

public class medicina {

    String  medicamento, descripcion, tiempo, paciente;
    Integer cantidad, periocidad, id;
    Timestamp inicio_tratamiento;
    Blob foto;


    public medicina() {
    }

    public medicina(Integer id,String medicamento,String paciente, String descripcion, Integer cantidad, String tiempo,Timestamp inicio_tratamiento,  Integer periocidad, Blob foto) {
        this.medicamento = medicamento;
        this.paciente = paciente;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.tiempo = tiempo;
        this.periocidad = periocidad;
        this.foto = foto;
        this.id = id;
        this.inicio_tratamiento = inicio_tratamiento;

    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public Timestamp getInicio_tratamiento() {

        return inicio_tratamiento;
    }

    public void setInicio_tratamiento(Timestamp inicio_tratamiento) {
        this.inicio_tratamiento = inicio_tratamiento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public Integer getPeriocidad() {
        return periocidad;
    }

    public void setPeriocidad(Integer periocidad) {
        this.periocidad = periocidad;
    }

    public Bitmap getfoto() {
        byte[] b = foto.toBytes();
        Bitmap bi = BitmapFactory.decodeByteArray( b,0, b.length);
        return bi;
    }

    public void setfoto(Blob foto) {
        this.foto = foto;
    }
}