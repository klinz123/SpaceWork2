package com.spacework.billing.model;

import com.spacework.reservations.model.Reserva;
import com.spacework.crm.model.Usuario;
import com.spacework.crm.model.Empresa;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Facturas", schema = "Reservas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FacturaID")
    private Integer id;

    @Column(name = "NumeroFactura", length = 50, unique = true)
    private String numeroFactura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReservaID")
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UsuarioID", nullable = false)
    private Usuario usuario; // A quién se le factura

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EmpresaID")
    private Empresa empresa; // Si factura a nombre de una empresa

    @Column(name = "FechaEmision", nullable = false)
    private LocalDateTime fechaEmision = LocalDateTime.now();

    @Column(name = "SubTotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subTotal;

    @Column(name = "TotalImpuestos", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalImpuestos;

    @Column(name = "TotalGeneral", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalGeneral;

    @Column(name = "EstadoFactura", length = 20, nullable = false)
    private String estadoFactura = "BORRADOR"; // BORRADOR, EMITIDA, PAGADA, ANULADA

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

    public Factura() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }
    public BigDecimal getTotalImpuestos() { return totalImpuestos; }
    public void setTotalImpuestos(BigDecimal totalImpuestos) { this.totalImpuestos = totalImpuestos; }
    public BigDecimal getTotalGeneral() { return totalGeneral; }
    public void setTotalGeneral(BigDecimal totalGeneral) { this.totalGeneral = totalGeneral; }
    public String getEstadoFactura() { return estadoFactura; }
    public void setEstadoFactura(String estadoFactura) { this.estadoFactura = estadoFactura; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
}
