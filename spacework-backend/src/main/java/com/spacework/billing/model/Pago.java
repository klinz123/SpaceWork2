package com.spacework.billing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import com.spacework.reservations.model.Reserva;
import java.time.LocalDateTime;

@Entity
@Table(name = "Pagos", schema = "Reservas")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PagoID")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ReservaID", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FormaPagoID", nullable = false)
    private FormaPago formaPago;

    @Column(name = "MontoPago", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoPago;

    @Column(name = "FechaPago", nullable = false)
    private LocalDateTime fechaPago = LocalDateTime.now();

    @Column(name = "ReferenciaTransaccion", length = 100, nullable = false)
    private String referenciaTransaccion;

    @Column(name = "EstadoPago", length = 20, nullable = false)
    private String estadoPago = "PENDIENTE";

    @JsonIgnore
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.VARBINARY)
    @Column(name = "DatosTarjeta", columnDefinition = "VARBINARY(MAX)")
    private byte[] datosTarjeta;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

    // creadores del objeto
    public Pago() {}

    // MÃ©todos para obtener y modificar los datos
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public BigDecimal getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(BigDecimal montoPago) {
        this.montoPago = montoPago;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getReferenciaTransaccion() {
        return referenciaTransaccion;
    }

    public void setReferenciaTransaccion(String referenciaTransaccion) {
        this.referenciaTransaccion = referenciaTransaccion;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public byte[] getDatosTarjeta() {
        return datosTarjeta;
    }

    public void setDatosTarjeta(byte[] datosTarjeta) {
        this.datosTarjeta = datosTarjeta;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}


