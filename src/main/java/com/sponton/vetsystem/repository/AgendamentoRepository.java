package com.sponton.vetsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Agendamento;

public interface AgendamentoRepository  extends JpaRepository<Agendamento, Long>{

	@Query("select a from Agendamento a where a.veterinario.id = :id")
	List<Agendamento> findVeterinarioById(Long id);

}
