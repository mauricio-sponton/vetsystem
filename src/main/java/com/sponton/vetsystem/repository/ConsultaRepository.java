package com.sponton.vetsystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Consulta;

public interface ConsultaRepository extends JpaRepository<Consulta, Long>{

	@Query("select c from Consulta c where c.animal.nome like :search% and c.veterinario.id = :id")
	Page<Consulta> findByName(String search, Pageable pageable, Long id);

	@Query("select c from Consulta c where c.veterinario.id = :id")
	Page<Consulta> findByVeterinario(Pageable pageable, Long id);

	@Query("select c from Consulta c where c.animal.id = :id order by c.id DESC")
	List<Consulta> findConsultaByAnimal(Long id);

	@Query("select c from Consulta c where c.animal.id = :idAnimal order by c.id DESC")
	Page<Consulta> findByAnimalId(Pageable pageable, Long idAnimal);

	@Query("select c from Consulta c where c.animal.nome like :search% and c.animal.id = :idAnimal order by c.id DESC")
	Page<Consulta> findByAnimal(String search, Pageable pageable, Long idAnimal);

}
