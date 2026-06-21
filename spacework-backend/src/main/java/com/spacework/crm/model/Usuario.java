package com.spacework.crm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Usuarios", schema = "Usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UsuarioID")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TipoDocumentoID", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "NumeroDocumento", length = 20, nullable = false, unique = true)
    private String numeroDocumento;

    @Column(name = "Nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "Ap_Paterno", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column(name = "Ap_Materno", length = 50, nullable = false)
    private String apellidoMaterno;

    @Column(name = "CorreoElectronico", length = 100, nullable = false, unique = true)
    private String correoElectronico;

    @Column(name = "Telefono", length = 20)
    private String telefono;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "Contrasena", length = 255, nullable = false)
    private String contrasena; // almacenará el hash de BCrypt

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RolID", nullable = false)
    private Rol rol;

    @Column(name = "FechaRegistro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "UltimoAcceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

    @Column(name = "IntentosFallidos", nullable = false)
    private int intentosFallidos = 0;

    @Column(name = "Bloqueado", nullable = false)
    private boolean bloqueado = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EmpresaID", nullable = true)
    private Empresa empresa;

    // constructors
    public Usuario() {}

    // getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
}
