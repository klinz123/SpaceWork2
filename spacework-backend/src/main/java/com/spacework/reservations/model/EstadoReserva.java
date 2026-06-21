package com.spacework.reservations.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "EstadosReserva", schema = "Reservas")
@Getter
@Setter
@NoArgsConstructor
public class EstadoReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EstadoReservaID")
    private Integer id;

    @Column(name = "NombreEstado", length = 50, nullable = false, unique = true)
    private String nombreEstado;

    @Column(name = "Descripcion", length = 200)
    private String descripcion;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

}

