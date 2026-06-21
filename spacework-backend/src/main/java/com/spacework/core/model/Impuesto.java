package com.spacework.core.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Impuestos", schema = "dbo")
public class Impuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ImpuestoID")
    private Integer id;

    @Column(name = "Nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "Porcentaje", precision = 5, scale = 2, nullable = false)
    private BigDecimal porcentaje;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

    public Impuesto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPorcentaje() { return porcentaje; }
    public void setPorcentaje(BigDecimal porcentaje) { this.porcentaje = porcentaje; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
}
