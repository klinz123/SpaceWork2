package com.spacework.reservations.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ReservaServicios", schema = "Reservas")
@Getter
@Setter
@NoArgsConstructor
public class ReservaServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReservaServicioID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReservaID", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ServicioID", nullable = false)
    private ServicioAdicional servicioAdicional;

    @Column(name = "Cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "Subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "FechaUso")
    private java.time.LocalDate fechaUso;

}
