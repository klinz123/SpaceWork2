package com.spacework.crm.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;

public class UsuarioRegistroRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El apellido paterno es obligatorio")
    private String apellidoPaterno;
    
    private String apellidoMaterno;
    
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String correoElectronico;
    
    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 8, max = 20, message = "El documento debe tener entre 8 y 20 caracteres")
    private String numeroDocumento;
    
    @Pattern(regexp = "^[\\d\\s\\+\\-\\(\\)]+$", message = "Formato de teléfono inválido")
    @Size(min = 6, max = 20, message = "El teléfono debe tener entre 6 y 20 caracteres")
    private String telefono;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$", message = "La contraseña debe contener al menos una mayúscula, un número y un carácter especial.")
    private String contrasena;
    
    // Opcionales para empresa
    @Valid
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
        @NotBlank(message = "El documento fiscal es obligatorio")
        @Size(max = 20, message = "El documento fiscal no debe exceder 20 caracteres")
        private String documentoFiscal;
        
        @NotBlank(message = "La razón social es obligatoria")
        @Size(max = 150, message = "La razón social no debe exceder 150 caracteres")
        private String razonSocial;
        
        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 250, message = "La dirección no debe exceder 250 caracteres")
        private String direccion;

        public String getDocumentoFiscal() { return documentoFiscal; }
        public void setDocumentoFiscal(String documentoFiscal) { this.documentoFiscal = documentoFiscal; }
        public String getRazonSocial() { return razonSocial; }
        public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
        public String getDireccion() { return direccion; }
        public void setDireccion(String direccion) { this.direccion = direccion; }
    }
}
