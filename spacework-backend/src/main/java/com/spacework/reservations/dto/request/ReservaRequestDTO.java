package com.spacework.reservations.dto.request;

import java.util.Map;
import lombok.Data;

@Data
public class ReservaRequestDTO {
    private Integer usuarioId;
    private Integer espacioId;
    private String fechaInicio;
    private String fechaFin;
    private Double montoTotal;
    private Double descuentoAplicado;
    private String observaciones;
    private String tipoComprobante;
    private String documentoIdentidad;
    private String razonSocial;
    private String direccion;
    private Map<String, Integer> serviciosAdicionales;

}
