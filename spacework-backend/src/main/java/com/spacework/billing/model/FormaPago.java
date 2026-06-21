package com.spacework.billing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "FormasPago", schema = "Reservas")
public class FormaPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FormaPagoID")
    private Integer id;

    @Column(name = "NombreForma", length = 50, nullable = false, unique = true)
    private String nombreForma;

    @Column(name = "Descripcion", length = 200)
    private String descripcion;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

    // creadores del objeto
    public FormaPago() {}

    // Métodos para obtener y modificar los datos
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreForma() {
        return nombreForma;
    }

    public void setNombreForma(String nombreForma) {
        this.nombreForma = nombreForma;
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

