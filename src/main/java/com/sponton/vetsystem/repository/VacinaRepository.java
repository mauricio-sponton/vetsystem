package com.sponton.vetsystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.domain.Vacina;

public interface VacinaRepository extends JpaRepository<Vacina, Long>{

	@Query("select v from Vacina v where v.descricao like :search% or v.especie.nome like :search%")
	Page<Vacina> findByName(String search, Pageable pageable);

	@Query("select v from Vacina v where v.descricao like :descricao and v.especie.nome like :especie")
	List<Vacina> findByNomeAndEspecie(String especie, String descricao);

	@Query("select v from Vacina v where v.especie.nome = :especie")
	List<Vacina> findVacinaByEspecie(String especie);


}
