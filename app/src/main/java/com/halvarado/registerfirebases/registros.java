package com.halvarado.registerfirebases;

public class registros {
    private String foro;
    private String nombres;
    private String apellidos;
    private String genero;
    private String fechaNacimiento;

    public registros() {
    }

    @Override
    public String toString() {
        return foro + ": " + nombres + " " + apellidos;
    }

    public registros(String foro, String nombres, String apellidos, String genero, String fechaNacimiento) {
        this.foro = foro;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.genero = genero;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getForo() {
        return foro;
    }

    public void setForo(String foro) {
        this.foro = foro;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }


}
