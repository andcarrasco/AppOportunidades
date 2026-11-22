package com.duoc.oportunidades.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.oportunidades.model.CambioEstado;
import com.duoc.oportunidades.model.Oportunidad;
import com.duoc.oportunidades.service.OportunidadService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/oportunidades")
public class OportunidadController {

    private final OportunidadService service;

    public OportunidadController(OportunidadService service) {
        this.service = service;
    }

    @GetMapping
    public List<Oportunidad> listar(@RequestParam(required = false) String estado,
                                    @RequestParam(required = false) String responsable) {
        return service.listar(estado, responsable);
    }

    @GetMapping("/{id}")
    public Oportunidad buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Oportunidad> crear(@Valid @RequestBody Oportunidad oportunidad) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(oportunidad));
    }

    @PutMapping("/{id}")
    public Oportunidad actualizar(@PathVariable Long id, @Valid @RequestBody Oportunidad oportunidad) {
        return service.actualizar(id, oportunidad);
    }

    @PatchMapping("/{id}/estado")
    public Oportunidad cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstado cambio) {
        return service.cambiarEstado(id, cambio.getEstado());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
