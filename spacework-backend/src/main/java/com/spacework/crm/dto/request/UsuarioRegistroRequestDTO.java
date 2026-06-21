package com.spacework.crm.dto.request;

public class UsuarioRegistroRequestDTO {
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correoElectronico;
    private String numeroDocumento;
    private String telefono;
    private String contrasena;
    // Opcionales para empresa
    private EmpresaRegistroDTO empresa;

    public UsuarioRegistroRequestDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public EmpresaRegistroDTO getEmpresa() { return empresa; }
    public void setEmpresa(EmpresaRegistroDTO empresa) { this.empresa = empresa; }

    public static class EmpresaRegistroDTO {
        private String documentoFiscal;
        private String razonSocial;
        private String direccion;

        public String getDocumentoFiscal() { return documentoFiscal; }
        public void setDocumentoFiscal(String documentoFiscal) { this.documentoFiscal = documentoFiscal; }
        public String getRazonSocial() { return razonSocial; }
        public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
        public String getDireccion() { return direccion; }
        public void setDireccion(String direccion) { this.direccion = direccion; }
    }
}
