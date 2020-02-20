package com.sponton.vetsystem.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Raca;

public interface RacaRepository extends JpaRepository<Raca, Long> {

	@Query("select r from Raca r where r.nome like :search% or r.especie.nome like :search%")
	Page<Raca> findByName(String search, Pageable pageable);

	@Query("select r from Raca r where r.especie.id = :id")
	List<Raca> findRacaByEspecie(Long id);

	@Query("select r.nome from Raca r where r.nome like :termo% and r.especie.nome like :especie")
	List<String> findRacaByTermo(String termo, String especie);

	@Query("select r from Raca r where r.nome IN :titulos")
	Set<Raca> findByTitulos(String[] titulos);

}
