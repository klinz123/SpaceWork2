package com.spacework.crm.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Roles", schema = "Usuarios")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RolID")
    private Integer id;

    @Column(name = "NombreRol", length = 50, nullable = false, unique = true)
    private String nombreRol;

    @Column(name = "Descripcion", length = 200)
    private String descripcion;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

    // creadores del objeto
    public Rol() {}

    public Rol(String nombreRol, String descripcion) {
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
    }

    // Métodos para obtener y modificar los datos
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}

