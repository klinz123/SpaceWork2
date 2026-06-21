package com.spacework.crm.dto.request;

public class UsuarioLoginRequestDTO {
    private String correoElectronico;
    private String contrasena;

    public UsuarioLoginRequestDTO() {}

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
