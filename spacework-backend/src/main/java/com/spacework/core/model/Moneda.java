package com.spacework.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Monedas", schema = "dbo")
public class Moneda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MonedaID")
    private Integer id;

    @Column(name = "Codigo", length = 3, nullable = false, unique = true)
    private String codigo;

    @Column(name = "Nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "Simbolo", length = 5, nullable = false)
    private String simbolo;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

    public Moneda() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSimbolo() { return simbolo; }
    public void setSimbolo(String simbolo) { this.simbolo = simbolo; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
}
