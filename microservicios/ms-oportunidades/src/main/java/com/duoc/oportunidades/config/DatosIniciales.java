package com.duoc.oportunidades.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.duoc.oportunidades.model.Oportunidad;
import com.duoc.oportunidades.repository.OportunidadRepository;

@Configuration
public class DatosIniciales {

    @Bean
    public CommandLineRunner cargarOportunidades(OportunidadRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            Oportunidad feria = new Oportunidad();
            feria.setNombre("Feria de beneficios Municipalidad Demo");
            feria.setTipo("Actividad municipal");
            feria.setDescripcion("Feria comunal con stands de servicios y beneficios para vecinos.");
            feria.setFuente("Sitio web municipal");
            feria.setFechaDeteccion("2026-11-02");
            feria.setResponsable("Carla Demo");
            feria.setPrioridad("Alta");
            feria.setPotencial("Alto");
            feria.setEstado("Evento identificado");
            feria.setOrganizacionId("1");
            feria.setFechaInicio("2026-12-10");
            feria.setFechaTermino("2026-12-11");
            feria.setHorario("10:00");
            feria.setComuna("Puente Alto");
            feria.setRegion("Metropolitana");
            feria.setUbicacion("Plaza de Armas Demo");
            feria.setPublico("Vecinos y familias");
            feria.setAsistentes("3000");
            feria.setCondiciones("Stand de 3x3 metros, sujeto a aprobacion municipal.");
            feria.setRequisitos("Solicitud formal y permiso de instalacion.");
            feria.setCostos("150000");
            feria.setEspacios("Stands al aire libre");
            feria.setObservaciones("Datos ficticios para demostracion.");
            feria.setActualizacion("2026-11-02");
            repository.save(feria);

            Oportunidad jornada = new Oportunidad();
            jornada.setNombre("Jornada de bienestar Empresa Demo");
            jornada.setTipo("Jornada de beneficios");
            jornada.setDescripcion("Jornada interna de bienestar para colaboradores de la empresa.");
            jornada.setFuente("Contacto directo");
            jornada.setFechaDeteccion("2026-11-10");
            jornada.setResponsable("Carla Demo");
            jornada.setPrioridad("Media");
            jornada.setPotencial("Medio");
            jornada.setEstado("Propuesta enviada");
            jornada.setOrganizacionId("2");
            jornada.setFechaInicio("2027-01-20");
            jornada.setFechaTermino("2027-01-20");
            jornada.setHorario("09:00");
            jornada.setComuna("Las Condes");
            jornada.setRegion("Metropolitana");
            jornada.setUbicacion("Oficinas Empresa Demo");
            jornada.setPublico("Colaboradores");
            jornada.setAsistentes("250");
            jornada.setCondiciones("Coordinar con recursos humanos.");
            jornada.setRequisitos("Cotizacion previa.");
            jornada.setCostos("0");
            jornada.setEspacios("Hall principal");
            jornada.setObservaciones("Datos ficticios para demostracion.");
            jornada.setActualizacion("2026-11-10");
            repository.save(jornada);
        };
    }
}
