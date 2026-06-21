package com.spacework.reservations.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Caracteristicas", schema = "Espacios")
@Getter
@Setter
@NoArgsConstructor
public class Caracteristica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CaracteristicaID")
    private Integer id;

    @Column(name = "NombreCaracteristica", length = 50, nullable = false, unique = true)
    private String nombreCaracteristica;

    @Column(name = "Descripcion", length = 200)
    private String descripcion;

    @Column(name = "Tipo", length = 30, nullable = false)
    private String tipo;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

}

