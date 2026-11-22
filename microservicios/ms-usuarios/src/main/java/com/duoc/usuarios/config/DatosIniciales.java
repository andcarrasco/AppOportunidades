package com.duoc.usuarios.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.duoc.usuarios.model.Usuario;
import com.duoc.usuarios.repository.UsuarioRepository;

@Configuration
public class DatosIniciales {

    @Bean
    public CommandLineRunner cargarUsuarios(UsuarioRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.save(crear("Carla Demo", "ejecutivo@example.com", "Ejecutivo comercial", "Comercial"));
            repository.save(crear("Rodrigo Demo", "supervisor@example.com", "Supervisor comercial", "Comercial"));
            repository.save(crear("Valentina Demo", "activaciones@example.com", "Gestión de activaciones", "Activaciones"));
        };
    }

    private Usuario crear(String nombre, String correo, String perfil, String area) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setClave("Demo2026");
        u.setPerfil(perfil);
        u.setArea(area);
        u.setZona("Santiago");
        u.setActivo(true);
        u.setFechaCreacion("2026-01-15");
        return u;
    }
}
