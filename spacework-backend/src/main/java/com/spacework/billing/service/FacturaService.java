package com.spacework.billing.service;

import com.spacework.billing.model.DetalleFactura;
import com.spacework.billing.model.Factura;
import com.spacework.billing.repository.DetalleFacturaRepository;
import com.spacework.billing.repository.FacturaRepository;
import com.spacework.core.model.Impuesto;
import com.spacework.core.repository.ImpuestoRepository;
import com.spacework.reservations.model.Reserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final DetalleFacturaRepository detalleFacturaRepository;
    private final ImpuestoRepository impuestoRepository;

    @Autowired
    public FacturaService(FacturaRepository facturaRepository, 
                          DetalleFacturaRepository detalleFacturaRepository,
                          ImpuestoRepository impuestoRepository) {
        this.facturaRepository = facturaRepository;
        this.detalleFacturaRepository = detalleFacturaRepository;
        this.impuestoRepository = impuestoRepository;
    }

    @Transactional
    public Factura generarFacturaDeReserva(Reserva reserva) {
        // Verificar si ya existe factura BASE para evitar duplicados
        if (!facturaRepository.findByReservaIdAndEstadoTrue(reserva.getId()).isEmpty()) {
            // Ya hay al menos un comprobante. No sobreescribir ni crear base duplicada.
            return facturaRepository.findByReservaIdAndEstadoTrue(reserva.getId()).get(0);
        }

        // Determinar si es Factura o Boleta
        boolean esFactura = reserva.getUsuario().getEmpresa() != null;
        String nuevoNumero = generarSiguienteNumero(esFactura);

        // Obtenemos el impuesto (IGV por defecto)
        Impuesto igv = impuestoRepository.findByNombreAndEstadoTrue("IGV")
                .orElseThrow(() -> new IllegalStateException("Impuesto IGV no configurado"));

        BigDecimal porcentajeImpuesto = igv.getPorcentaje().divide(new BigDecimal("100"));
        BigDecimal factorDivision = BigDecimal.ONE.add(porcentajeImpuesto); // 1.18

        BigDecimal totalGeneral = reserva.getMontoTotal();
        BigDecimal subTotal = totalGeneral.divide(factorDivision, 2, RoundingMode.HALF_UP);
        BigDecimal totalImpuestos = totalGeneral.subtract(subTotal);

        Factura factura = new Factura();
        factura.setNumeroFactura(nuevoNumero);
        factura.setReserva(reserva);
        factura.setUsuario(reserva.getUsuario());
        factura.setEmpresa(esFactura ? reserva.getUsuario().getEmpresa() : null);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setSubTotal(subTotal);
        factura.setTotalImpuestos(totalImpuestos);
        factura.setTotalGeneral(totalGeneral);
        factura.setEstadoFactura("EMITIDA");

        Factura guardada = facturaRepository.save(factura);

        DetalleFactura detalle = new DetalleFactura();
        detalle.setFactura(guardada);
        detalle.setDescripcionItem("Alquiler de Espacio: " + reserva.getEspacio().getNombreEspacio());
        detalle.setCantidad(1);
        detalle.setPrecioUnitario(subTotal);
        detalle.setSubTotalItem(subTotal);

        detalleFacturaRepository.save(detalle);

        return guardada;
    }

    @Transactional
    public Factura generarFacturaAdicional(Reserva reserva, BigDecimal monto, String descripcionItem) {
        Impuesto igv = impuestoRepository.findByNombreAndEstadoTrue("IGV")
                .orElseThrow(() -> new IllegalStateException("Impuesto IGV no configurado"));

        BigDecimal porcentajeImpuesto = igv.getPorcentaje().divide(new BigDecimal("100"));
        BigDecimal factorDivision = BigDecimal.ONE.add(porcentajeImpuesto);

        BigDecimal totalGeneral = monto;
        BigDecimal subTotal = totalGeneral.divide(factorDivision, 2, RoundingMode.HALF_UP);
        BigDecimal totalImpuestos = totalGeneral.subtract(subTotal);

        boolean esFactura = reserva.getUsuario().getEmpresa() != null;
        String nuevoNumero = generarSiguienteNumero(esFactura);

        Factura factura = new Factura();
        factura.setNumeroFactura(nuevoNumero);
        factura.setReserva(reserva);
        factura.setUsuario(reserva.getUsuario());
        factura.setEmpresa(esFactura ? reserva.getUsuario().getEmpresa() : null);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setSubTotal(subTotal);
        factura.setTotalImpuestos(totalImpuestos);
        factura.setTotalGeneral(totalGeneral);
        factura.setEstadoFactura("EMITIDA");

        Factura guardada = facturaRepository.save(factura);

        DetalleFactura detalle = new DetalleFactura();
        detalle.setFactura(guardada);
        detalle.setDescripcionItem(descripcionItem);
        detalle.setCantidad(1);
        detalle.setPrecioUnitario(subTotal);
        detalle.setSubTotalItem(subTotal);

        detalleFacturaRepository.save(detalle);

        return guardada;
    }

    private String generarSiguienteNumero(boolean esFactura) {
        String serie = esFactura ? "F001" : "B001";
        Factura ultima = facturaRepository.findTopByNumeroFacturaStartingWithOrderByNumeroFacturaDesc(serie + "-");
        
        int siguienteCorrelativo = 1;
        if (ultima != null && ultima.getNumeroFactura() != null) {
            String[] partes = ultima.getNumeroFactura().split("-");
            if (partes.length == 2) {
                try {
                    siguienteCorrelativo = Integer.parseInt(partes[1]) + 1;
                } catch (NumberFormatException e) {
                    siguienteCorrelativo = 1;
                }
            }
        }
        return serie + "-" + String.format("%08d", siguienteCorrelativo);
    }

    public List<Factura> obtenerTodasLasFacturas() {
        return facturaRepository.findByEstadoTrue();
    }
}
