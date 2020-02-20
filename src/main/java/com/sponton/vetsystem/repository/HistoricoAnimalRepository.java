package com.sponton.vetsystem.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.HistoricoAnimal;

public interface HistoricoAnimalRepository extends JpaRepository<HistoricoAnimal, Long>{

	@Query("select h from HistoricoAnimal h where h.animal.id = :id order by h.id DESC")
	List<HistoricoAnimal> findHistoricoByAnimal(Long id);

	@Query("select h from HistoricoAnimal h where h.tipo =:tipo and h.animal.id =:id order by h.id DESC")
	List<HistoricoAnimal> findHistoricoByTipo(String tipo, Long id);

	@Query("select h from HistoricoAnimal h where h.data =:data and h.animal.id =:id order by h.id DESC")
	List<HistoricoAnimal> findHistoricoPorData(LocalDate data, Long id);

}
