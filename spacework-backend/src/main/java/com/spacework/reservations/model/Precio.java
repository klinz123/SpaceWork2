package com.spacework.reservations.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Precios", schema = "Espacios")
@Getter
@Setter
@NoArgsConstructor
public class Precio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PrecioID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EspacioID", nullable = false)
    private Espacio espacio;

    @Column(name = "TipoTarifa", length = 30, nullable = false)
    private String tipoTarifa;

    @Column(name = "Monto", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "Moneda", length = 3, nullable = false)
    private String moneda = "PEN";

    @Column(name = "Descuento", precision = 5, scale = 2)
    private BigDecimal descuento;

    @Column(name = "FechaInicioVigencia", nullable = false)
    private LocalDateTime fechaInicioVigencia = LocalDateTime.now();

    @Column(name = "FechaFinVigencia")
    private LocalDateTime fechaFinVigencia;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

}

