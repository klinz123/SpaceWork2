package com.spacework.reservations.dto.request;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.math.BigDecimal;
import lombok.Data;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

@Data
public class EspacioRequestDTO {
    private String codigoEspacio;
    
    @NotBlank(message = "El nombreEspacio es obligatorio")
    @Size(max = 100, message = "El nombreEspacio no debe exceder 100 caracteres")
    private String nombreEspacio;
    
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidad;
    
    private Integer capacidadEquipos;
    
    @Size(max = 500, message = "La descripcion no debe exceder 500 caracteres")
    private String descripcion;
    
    @NotBlank(message = "El estadoEspacio es obligatorio")
    private String estadoEspacio;
    
    @NotNull(message = "La horaApertura es obligatoria")
    private LocalTime horaApertura;
    
    @NotNull(message = "La horaCierre es obligatoria")
    private LocalTime horaCierre;
    
    private String fotoUrl;
    
    @Positive(message = "Los metrosCuadrados deben ser positivos")
    private Double metrosCuadrados;
    
    @DecimalMin(value = "0.0", message = "El precioPersonaExtra debe ser al menos 0")
    private Double precioPersonaExtra;
    
    // Referencias
    @Valid
    @NotNull(message = "El tipoEspacio es obligatorio")
    private RelacionDTO tipoEspacio;
    
    @Valid
    @NotNull(message = "La ubicacion es obligatoria")
    private RelacionDTO ubicacion;
    
    private Set<RelacionDTO> caracteristicas;
    
    @Valid
    private List<FotoEspacioRequestDTO> fotos;
    
    // Precios
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precio;
    
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El descuento no puede exceder 100")
    private BigDecimal descuento;

    @Data
    public static class RelacionDTO {
        @NotNull(message = "El ID de la relación es obligatorio")
        private Integer id;
    }

    @Data
    public static class FotoEspacioRequestDTO {
        @NotBlank(message = "La URL de la foto es obligatoria")
        @Size(max = 500, message = "La URL no debe exceder 500 caracteres")
        private String urlFoto;
        private boolean esPrincipal;
    }
}
