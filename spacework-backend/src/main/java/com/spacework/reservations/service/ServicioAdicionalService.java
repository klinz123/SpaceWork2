package com.spacework.reservations.service;

import com.spacework.reservations.model.ServicioAdicional;
import com.spacework.reservations.repository.ServicioAdicionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioAdicionalService {

    @Autowired
    private ServicioAdicionalRepository servicioAdicionalRepository;

    public List<ServicioAdicional> getAllServicios() {
        return servicioAdicionalRepository.findAll();
    }

    public List<ServicioAdicional> getActiveServicios() {
        return servicioAdicionalRepository.findByEstado(true);
    }

    public ServicioAdicional getServicioById(Integer id) {
        return servicioAdicionalRepository.findById(id).orElse(null);
    }

    public ServicioAdicional createServicio(ServicioAdicional servicio) {
        return servicioAdicionalRepository.save(servicio);
    }

    public ServicioAdicional updateServicio(Integer id, ServicioAdicional servicioDetalles) {
        ServicioAdicional servicio = getServicioById(id);
        if (servicio != null) {
            servicio.setNombre(servicioDetalles.getNombre());
            servicio.setDescripcion(servicioDetalles.getDescripcion());
            servicio.setPrecioBase(servicioDetalles.getPrecioBase());
            servicio.setProveedor(servicioDetalles.getProveedor());
            servicio.setEstado(servicioDetalles.isEstado());
            servicio.setTipoServicio(servicioDetalles.getTipoServicio());
            servicio.setStockTotal(servicioDetalles.getStockTotal());
            servicio.setImagenUrl(servicioDetalles.getImagenUrl());
            servicio.setCaracteristicasDetalle(servicioDetalles.getCaracteristicasDetalle());
            servicio.setAdvertenciasDevolucion(servicioDetalles.getAdvertenciasDevolucion());
            return servicioAdicionalRepository.save(servicio);
        }
        return null;
    }

    public void deleteServicio(Integer id) {
        ServicioAdicional servicio = getServicioById(id);
        if (servicio != null) {
            servicio.setEstado(false);
            servicioAdicionalRepository.save(servicio);
        }
    }
}
