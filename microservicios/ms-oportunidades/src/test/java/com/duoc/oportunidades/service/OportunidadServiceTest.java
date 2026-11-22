package com.duoc.oportunidades.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.duoc.oportunidades.model.Oportunidad;
import com.duoc.oportunidades.repository.OportunidadRepository;

@ExtendWith(MockitoExtension.class)
class OportunidadServiceTest {

    @Mock
    private OportunidadRepository repository;

    @InjectMocks
    private OportunidadService service;

    private Oportunidad crearOportunidad(Long id, String estado, String responsable) {
        Oportunidad o = new Oportunidad();
        o.setId(id);
        o.setNombre("Oportunidad " + id);
        o.setTipo("Actividad municipal");
        o.setEstado(estado);
        o.setResponsable(responsable);
        o.setActualizacion("2026-01-01");
        return o;
    }

    @Test
    void listarSinFiltrosDevuelveTodas() {
        when(repository.findAll(any(Sort.class))).thenReturn(List.of(
                crearOportunidad(1L, "Evento identificado", "Carla Demo"),
                crearOportunidad(2L, "Contacto pendiente", "Rodrigo Demo")));
        assertEquals(2, service.listar(null, null).size());
    }

    @Test
    void listarFiltraPorEstadoYResponsable() {
        when(repository.findAll(any(Sort.class))).thenReturn(List.of(
                crearOportunidad(1L, "Evento identificado", "Carla Demo"),
                crearOportunidad(2L, "Contacto pendiente", "Rodrigo Demo")));
        assertEquals(1, service.listar("Contacto pendiente", null).size());
        assertEquals(1, service.listar(null, "carla demo").size());
        assertEquals(0, service.listar("Contacto pendiente", "Carla Demo").size());
    }

    @Test
    void buscarInexistenteLanza404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.buscar(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void crearSinActualizacionAsignaFechaDeHoy() {
        Oportunidad o = crearOportunidad(null, "Evento identificado", "Carla Demo");
        o.setActualizacion("");
        when(repository.save(any(Oportunidad.class))).thenAnswer(inv -> inv.getArgument(0));

        Oportunidad guardada = service.crear(o);

        assertEquals(LocalDate.now().toString(), guardada.getActualizacion());
    }

    @Test
    void crearConservaActualizacionSiViene() {
        Oportunidad o = crearOportunidad(null, "Evento identificado", "Carla Demo");
        o.setActualizacion("2026-05-05");
        when(repository.save(any(Oportunidad.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals("2026-05-05", service.crear(o).getActualizacion());
    }

    @Test
    void actualizarCopiaDatosYActualizaFecha() {
        Oportunidad existente = crearOportunidad(1L, "Evento identificado", "Carla Demo");
        Oportunidad datos = crearOportunidad(null, "Contacto pendiente", "Rodrigo Demo");
        datos.setNombre("Nombre nuevo");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Oportunidad.class))).thenAnswer(inv -> inv.getArgument(0));

        Oportunidad resultado = service.actualizar(1L, datos);

        assertEquals(1L, resultado.getId());
        assertEquals("Nombre nuevo", resultado.getNombre());
        assertEquals("Rodrigo Demo", resultado.getResponsable());
        assertEquals(LocalDate.now().toString(), resultado.getActualizacion());
    }

    @Test
    void cambiarEstadoActualizaEstadoYFecha() {
        Oportunidad existente = crearOportunidad(1L, "Evento identificado", "Carla Demo");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Oportunidad.class))).thenAnswer(inv -> inv.getArgument(0));

        Oportunidad resultado = service.cambiarEstado(1L, "Cerrada");

        assertEquals("Cerrada", resultado.getEstado());
        assertNotEquals("2026-01-01", resultado.getActualizacion());
    }

    @Test
    void eliminarBorraOportunidadExistente() {
        Oportunidad existente = crearOportunidad(1L, "Evento identificado", "Carla Demo");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        service.eliminar(1L);
        verify(repository).delete(existente);
    }

    @Test
    void eliminarInexistenteLanza404() {
        when(repository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.eliminar(5L));
    }
}
