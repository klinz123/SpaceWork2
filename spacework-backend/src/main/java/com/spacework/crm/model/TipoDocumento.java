package com.spacework.crm.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TiposDocumento", schema = "Usuarios")
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TipoDocumentoID")
    private Integer id;

    @Column(name = "NombreTipo", length = 50, nullable = false, unique = true)
    private String nombreTipo;

    @Column(name = "Abreviatura", length = 10, nullable = false, unique = true)
    private String abreviatura;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

    // creadores del objeto
    public TipoDocumento() {}

    // Métodos para obtener y modificar los datos
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}

