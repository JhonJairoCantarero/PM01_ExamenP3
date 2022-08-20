package com.example.pm01_examenp3;

public class usuario {
    private String nombre,email,clave,as;

    public usuario(){

    }

    public String getAs(){
        return as;
    }
    public String getNombre(){
        return nombre;
    }
    public String getEmail(){
        return email;
    }
    public String getClave(){
        return clave;
    }

    public void setAs(String as){
        this.as = as;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setClave(String clave){
        this.clave = clave;
    }
}
