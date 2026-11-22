package com.duoc.oportunidades.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.duoc.oportunidades.model.Oportunidad;
import com.duoc.oportunidades.repository.OportunidadRepository;

@Service
public class OportunidadService {

    private final OportunidadRepository repository;

    public OportunidadService(OportunidadRepository repository) {
        this.repository = repository;
    }

    // Filtros opcionales: si vienen nulos o vacios no se aplican
    public List<Oportunidad> listar(String estado, String responsable) {
        return repository.findAll(Sort.by("id")).stream()
                .filter(o -> estado == null || estado.isBlank() || o.getEstado().equalsIgnoreCase(estado))
                .filter(o -> responsable == null || responsable.isBlank()
                        || o.getResponsable().equalsIgnoreCase(responsable))
                .toList();
    }

    public Oportunidad buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oportunidad no encontrada"));
    }

    public Oportunidad crear(Oportunidad oportunidad) {
        oportunidad.setId(null);
        if (oportunidad.getActualizacion() == null || oportunidad.getActualizacion().isBlank()) {
            oportunidad.setActualizacion(hoy());
        }
        return repository.save(oportunidad);
    }

    public Oportunidad actualizar(Long id, Oportunidad datos) {
        Oportunidad existente = buscar(id);
        existente.copiarDatos(datos);
        existente.setActualizacion(hoy());
        return repository.save(existente);
    }

    public Oportunidad cambiarEstado(Long id, String estado) {
        Oportunidad existente = buscar(id);
        existente.setEstado(estado);
        existente.setActualizacion(hoy());
        return repository.save(existente);
    }

    public void eliminar(Long id) {
        Oportunidad existente = buscar(id);
        repository.delete(existente);
    }

    private String hoy() {
        return LocalDate.now().toString();
    }
}
