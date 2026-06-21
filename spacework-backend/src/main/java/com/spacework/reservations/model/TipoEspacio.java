package com.spacework.reservations.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TiposEspacio", schema = "Espacios")
@Getter
@Setter
@NoArgsConstructor
public class TipoEspacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TipoEspacioID")
    private Integer id;

    @Column(name = "NombreTipo", length = 50, nullable = false, unique = true)
    private String nombreTipo;

    @Column(name = "Descripcion", length = 200)
    private String descripcion;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

}

