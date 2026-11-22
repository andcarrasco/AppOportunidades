package com.duoc.usuarios.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.duoc.usuarios.model.Usuario;
import com.duoc.usuarios.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public Usuario crear(Usuario usuario) {
        if (usuario.getClave() == null || usuario.getClave().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clave es obligatoria");
        }
        if (repository.existsByCorreo(usuario.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        }
        usuario.setId(null);
        if (usuario.getFechaCreacion() == null || usuario.getFechaCreacion().isBlank()) {
            usuario.setFechaCreacion(LocalDate.now().toString());
        }
        return repository.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario datos) {
        Usuario existente = buscar(id);
        if (!existente.getCorreo().equals(datos.getCorreo()) && repository.existsByCorreo(datos.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        }
        existente.setNombre(datos.getNombre());
        existente.setCorreo(datos.getCorreo());
        existente.setPerfil(datos.getPerfil());
        existente.setArea(datos.getArea());
        existente.setZona(datos.getZona());
        existente.setActivo(datos.isActivo());
        // Si no se envia clave se mantiene la anterior
        if (datos.getClave() != null && !datos.getClave().isBlank()) {
            existente.setClave(datos.getClave());
        }
        return repository.save(existente);
    }

    public void eliminar(Long id) {
        Usuario existente = buscar(id);
        repository.delete(existente);
    }

    public Usuario login(String correo, String clave) {
        Usuario usuario = repository.findByCorreo(correo)
                .filter(u -> u.getClave() != null && u.getClave().equals(clave))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o clave incorrectos"));
        if (!usuario.isActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario inactivo");
        }
        return usuario;
    }
}
