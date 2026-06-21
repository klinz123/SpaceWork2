package com.spacework.reservations.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Ubicaciones", schema = "Espacios")
@Getter
@Setter
@NoArgsConstructor
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UbicacionID")
    private Integer id;

    @Column(name = "NombreUbicacion", length = 100, nullable = false, unique = true)
    private String nombreUbicacion;

    @Column(name = "Direccion", length = 200, nullable = false)
    private String direccion;

    @Column(name = "Ciudad", length = 50, nullable = false)
    private String ciudad;

    @Column(name = "Pais", length = 50, nullable = false)
    private String pais;

    @Column(name = "Latitud")
    private Double latitud;

    @Column(name = "Longitud")
    private Double longitud;

    @Column(name = "UrlGoogleMaps", length = 500)
    private String urlGoogleMaps;

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

}

