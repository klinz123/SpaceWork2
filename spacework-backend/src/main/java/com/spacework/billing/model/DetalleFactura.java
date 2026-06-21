package com.spacework.billing.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "DetallesFactura", schema = "Reservas")
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DetalleID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FacturaID", nullable = false)
    private Factura factura;

    @Column(name = "DescripcionItem", length = 250, nullable = false)
    private String descripcionItem;

    @Column(name = "Cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "PrecioUnitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "SubTotalItem", precision = 10, scale = 2, nullable = false)
    private BigDecimal subTotalItem;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

    public DetalleFactura() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Factura getFactura() { return factura; }
    public void setFactura(Factura factura) { this.factura = factura; }
    public String getDescripcionItem() { return descripcionItem; }
    public void setDescripcionItem(String descripcionItem) { this.descripcionItem = descripcionItem; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getSubTotalItem() { return subTotalItem; }
    public void setSubTotalItem(BigDecimal subTotalItem) { this.subTotalItem = subTotalItem; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
}
