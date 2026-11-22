package com.duoc.oportunidades.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "oportunidades")
public class Oportunidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El campo nombre es obligatorio")
    private String nombre = "";

    @NotBlank(message = "El campo tipo es obligatorio")
    private String tipo = "";

    @Column(length = 1000)
    private String descripcion = "";

    private String fuente = "";

    private String fechaDeteccion = "";

    private String responsable = "";

    private String prioridad = "";

    private String potencial = "";

    @NotBlank(message = "El campo estado es obligatorio")
    private String estado = "";

    private String organizacionId = "";

    private String fechaInicio = "";

    private String fechaTermino = "";

    private String horario = "";

    private String comuna = "";

    private String region = "";

    private String ubicacion = "";

    private String publico = "";

    private String asistentes = "";

    @Column(length = 1000)
    private String condiciones = "";

    @Column(length = 1000)
    private String requisitos = "";

    private String costos = "";

    private String espacios = "";

    @Column(length = 1000)
    private String observaciones = "";

    private String actualizacion = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public String getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(String fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getPotencial() {
        return potencial;
    }

    public void setPotencial(String potencial) {
        this.potencial = potencial;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getOrganizacionId() {
        return organizacionId;
    }

    public void setOrganizacionId(String organizacionId) {
        this.organizacionId = organizacionId;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaTermino() {
        return fechaTermino;
    }

    public void setFechaTermino(String fechaTermino) {
        this.fechaTermino = fechaTermino;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getPublico() {
        return publico;
    }

    public void setPublico(String publico) {
        this.publico = publico;
    }

    public String getAsistentes() {
        return asistentes;
    }

    public void setAsistentes(String asistentes) {
        this.asistentes = asistentes;
    }

    public String getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(String condiciones) {
        this.condiciones = condiciones;
    }

    public String getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }

    public String getCostos() {
        return costos;
    }

    public void setCostos(String costos) {
        this.costos = costos;
    }

    public String getEspacios() {
        return espacios;
    }

    public void setEspacios(String espacios) {
        this.espacios = espacios;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getActualizacion() {
        return actualizacion;
    }

    public void setActualizacion(String actualizacion) {
        this.actualizacion = actualizacion;
    }

    // Copia todos los datos editables (todo menos el id)
    public void copiarDatos(Oportunidad o) {
        this.nombre = o.getNombre() == null ? "" : o.getNombre();
        this.tipo = o.getTipo() == null ? "" : o.getTipo();
        this.descripcion = o.getDescripcion() == null ? "" : o.getDescripcion();
        this.fuente = o.getFuente() == null ? "" : o.getFuente();
        this.fechaDeteccion = o.getFechaDeteccion() == null ? "" : o.getFechaDeteccion();
        this.responsable = o.getResponsable() == null ? "" : o.getResponsable();
        this.prioridad = o.getPrioridad() == null ? "" : o.getPrioridad();
        this.potencial = o.getPotencial() == null ? "" : o.getPotencial();
        this.estado = o.getEstado() == null ? "" : o.getEstado();
        this.organizacionId = o.getOrganizacionId() == null ? "" : o.getOrganizacionId();
        this.fechaInicio = o.getFechaInicio() == null ? "" : o.getFechaInicio();
        this.fechaTermino = o.getFechaTermino() == null ? "" : o.getFechaTermino();
        this.horario = o.getHorario() == null ? "" : o.getHorario();
        this.comuna = o.getComuna() == null ? "" : o.getComuna();
        this.region = o.getRegion() == null ? "" : o.getRegion();
        this.ubicacion = o.getUbicacion() == null ? "" : o.getUbicacion();
        this.publico = o.getPublico() == null ? "" : o.getPublico();
        this.asistentes = o.getAsistentes() == null ? "" : o.getAsistentes();
        this.condiciones = o.getCondiciones() == null ? "" : o.getCondiciones();
        this.requisitos = o.getRequisitos() == null ? "" : o.getRequisitos();
        this.costos = o.getCostos() == null ? "" : o.getCostos();
        this.espacios = o.getEspacios() == null ? "" : o.getEspacios();
        this.observaciones = o.getObservaciones() == null ? "" : o.getObservaciones();
        this.actualizacion = o.getActualizacion() == null ? "" : o.getActualizacion();
    }
}
