package com.spacework.crm.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Empresas", schema = "Usuarios")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EmpresaID")
    private Integer id;

    @Column(name = "DocumentoFiscal", length = 20, nullable = false, unique = true)
    private String documentoFiscal; // Ej: RUC

    @Column(name = "RazonSocial", length = 150, nullable = false)
    private String razonSocial;

    @Column(name = "Direccion", length = 250)
    private String direccion;

    @Column(name = "Telefono", length = 20)
    private String telefono;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

    @Column(name = "EmailContacto", length = 100)
    private String emailContacto;

    @Column(name = "NombreContactoPrincipal", length = 100)
    private String nombreContactoPrincipal;

    @Column(name = "SectorIndustria", length = 50)
    private String sectorIndustria;

    @Column(name = "PorcentajeDescuento", precision = 5, scale = 2)
    private java.math.BigDecimal porcentajeDescuento = java.math.BigDecimal.ZERO;

    public Empresa() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getDocumentoFiscal() { return documentoFiscal; }
    public void setDocumentoFiscal(String documentoFiscal) { this.documentoFiscal = documentoFiscal; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }
    public String getNombreContactoPrincipal() { return nombreContactoPrincipal; }
    public void setNombreContactoPrincipal(String nombreContactoPrincipal) { this.nombreContactoPrincipal = nombreContactoPrincipal; }
    public String getSectorIndustria() { return sectorIndustria; }
    public void setSectorIndustria(String sectorIndustria) { this.sectorIndustria = sectorIndustria; }
    public java.math.BigDecimal getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(java.math.BigDecimal porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }
}
