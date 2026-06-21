package com.spacework.crm.dto.response;

import java.time.LocalDateTime;

public class UsuarioResponseDTO {
    private Integer id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correoElectronico;
    private String telefono;
    private String numeroDocumento;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;
    private boolean estado;
    
    // Objetos anidados planos
    private RolDTO rol;
    private TipoDocumentoDTO tipoDocumento;
    private EmpresaDTO empresa;

    public UsuarioResponseDTO() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public RolDTO getRol() { return rol; }
    public void setRol(RolDTO rol) { this.rol = rol; }

    public TipoDocumentoDTO getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumentoDTO tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public EmpresaDTO getEmpresa() { return empresa; }
    public void setEmpresa(EmpresaDTO empresa) { this.empresa = empresa; }

    // Clases DTO anidadas
    public static class RolDTO {
        private Integer id;
        private String nombreRol;
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getNombreRol() { return nombreRol; }
        public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
    }

    public static class TipoDocumentoDTO {
        private Integer id;
        private String nombreTipo;
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getNombreTipo() { return nombreTipo; }
        public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }
    }

    public static class EmpresaDTO {
        private Integer id;
        private String razonSocial;
        private String ruc;
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getRazonSocial() { return razonSocial; }
        public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
        public String getRuc() { return ruc; }
        public void setRuc(String ruc) { this.ruc = ruc; }
    }
}
