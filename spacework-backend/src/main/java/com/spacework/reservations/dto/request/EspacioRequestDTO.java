package com.spacework.reservations.dto.request;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class EspacioRequestDTO {
    private String codigoEspacio;
    private String nombreEspacio;
    private Integer capacidad;
    private Integer capacidadEquipos;
    private String descripcion;
    private String estadoEspacio;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private String fotoUrl;
    private Double metrosCuadrados;
    private Double precioPersonaExtra;
    
    // Referencias
    private RelacionDTO tipoEspacio;
    private RelacionDTO ubicacion;
    private Set<RelacionDTO> caracteristicas;
    private List<FotoEspacioRequestDTO> fotos;
    
    // Precios
    private BigDecimal precio;
    private BigDecimal descuento;

    @Data
    public static class RelacionDTO {
        private Integer id;
    }

    @Data
    public static class FotoEspacioRequestDTO {
        private String urlFoto;
        private boolean esPrincipal;
    }
}
