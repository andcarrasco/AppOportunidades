package com.duoc.oportunidades.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.oportunidades.model.Oportunidad;

public interface OportunidadRepository extends JpaRepository<Oportunidad, Long> {
}
