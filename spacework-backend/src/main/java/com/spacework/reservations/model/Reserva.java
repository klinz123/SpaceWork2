package com.spacework.reservations.model;

import jakarta.persistence.*;
import com.spacework.crm.model.Usuario;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Reservas", schema = "Reservas")
@Getter
@Setter
@NoArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReservaID")
    private Integer id;

    @Column(name = "CodigoReserva", length = 20, nullable = false, unique = true)
    private String codigoReserva;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UsuarioID", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EspacioID", nullable = false)
    private Espacio espacio;

    @Column(name = "FechaReserva", nullable = false)
    private LocalDateTime fechaReserva = LocalDateTime.now();

    @Column(name = "FechaInicioReserva", nullable = false)
    private LocalDateTime fechaInicioReserva;

    @Column(name = "FechaFinReserva", nullable = false)
    private LocalDateTime fechaFinReserva;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EstadoReservaID", nullable = false)
    private EstadoReserva estadoReserva;

    @Column(name = "MontoTotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "Impuestos", precision = 10, scale = 2, nullable = false)
    private BigDecimal impuestos = BigDecimal.ZERO;

    @Column(name = "DescuentoAplicado", precision = 10, scale = 2, nullable = false)
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;

    @Column(name = "Observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "TipoComprobante", length = 20)
    private String tipoComprobante;

    @Column(name = "DocumentoIdentidad", length = 20)
    private String documentoIdentidad;

    @Column(name = "RazonSocial", length = 150)
    private String razonSocial;

    @Column(name = "Direccion", length = 200)
    private String direccion;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

    @OneToMany(mappedBy = "reserva", fetch = FetchType.EAGER)
    private java.util.List<ReservaServicio> reservaServicios;

}


