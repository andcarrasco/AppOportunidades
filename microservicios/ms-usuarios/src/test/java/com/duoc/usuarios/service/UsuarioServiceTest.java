package com.duoc.usuarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.duoc.usuarios.model.Usuario;
import com.duoc.usuarios.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    private Usuario nuevoUsuario() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNombre("Carla Demo");
        u.setCorreo("ejecutivo@example.com");
        u.setClave("Demo2026");
        u.setPerfil("Ejecutivo comercial");
        u.setActivo(true);
        return u;
    }

    @Test
    void listarDevuelveTodos() {
        when(repository.findAll()).thenReturn(List.of(nuevoUsuario()));
        assertEquals(1, service.listar().size());
    }

    @Test
    void buscarInexistenteLanza404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.buscar(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void crearAsignaFechaYGuarda() {
        Usuario u = nuevoUsuario();
        u.setFechaCreacion("");
        when(repository.existsByCorreo(u.getCorreo())).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario guardado = service.crear(u);

        assertFalse(guardado.getFechaCreacion().isBlank());
        verify(repository).save(u);
    }

    @Test
    void crearConCorreoRepetidoLanza409() {
        Usuario u = nuevoUsuario();
        when(repository.existsByCorreo(u.getCorreo())).thenReturn(true);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.crear(u));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void crearSinClaveLanza400() {
        Usuario u = nuevoUsuario();
        u.setClave("");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.crear(u));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void actualizarMantieneClaveSiNoSeEnvia() {
        Usuario existente = nuevoUsuario();
        Usuario datos = nuevoUsuario();
        datos.setNombre("Carla Editada");
        datos.setClave(null);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = service.actualizar(1L, datos);

        assertEquals("Carla Editada", resultado.getNombre());
        assertEquals("Demo2026", resultado.getClave());
    }

    @Test
    void eliminarBorraUsuarioExistente() {
        Usuario u = nuevoUsuario();
        when(repository.findById(1L)).thenReturn(Optional.of(u));
        service.eliminar(1L);
        verify(repository).delete(u);
    }

    @Test
    void loginCorrectoDevuelveUsuario() {
        Usuario u = nuevoUsuario();
        when(repository.findByCorreo("ejecutivo@example.com")).thenReturn(Optional.of(u));
        assertEquals(u, service.login("ejecutivo@example.com", "Demo2026"));
    }

    @Test
    void loginConClaveIncorrectaLanza401() {
        when(repository.findByCorreo("ejecutivo@example.com")).thenReturn(Optional.of(nuevoUsuario()));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.login("ejecutivo@example.com", "otra"));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void loginUsuarioInactivoLanza403() {
        Usuario u = nuevoUsuario();
        u.setActivo(false);
        when(repository.findByCorreo("ejecutivo@example.com")).thenReturn(Optional.of(u));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.login("ejecutivo@example.com", "Demo2026"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}
