package com.spacework.reservations.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "FotosEspacio", schema = "Espacios")
@Getter
@Setter
@NoArgsConstructor
public class FotoEspacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FotoID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EspacioID", nullable = false)
    @JsonBackReference
    private Espacio espacio;

    @Column(name = "UrlFoto", length = 500, nullable = false)
    private String urlFoto;

    @Column(name = "EsPrincipal", nullable = false)
    private boolean esPrincipal = false;

    public FotoEspacio(Espacio espacio, String urlFoto, boolean esPrincipal) {
        this.espacio = espacio;
        this.urlFoto = urlFoto;
        this.esPrincipal = esPrincipal;
    }
}
