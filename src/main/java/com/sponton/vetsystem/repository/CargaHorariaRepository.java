package com.sponton.vetsystem.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.CargaHoraria;

public interface CargaHorariaRepository extends JpaRepository<CargaHoraria, Long> {

	@Query("select c from CargaHoraria c where c.veterinario.id = :id")
	List<CargaHoraria> findHorarioByVeterinario(Long id);

	
}
