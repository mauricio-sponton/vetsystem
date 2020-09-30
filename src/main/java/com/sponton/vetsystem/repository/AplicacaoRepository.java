package com.sponton.vetsystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Aplicacao;

public interface AplicacaoRepository extends JpaRepository<Aplicacao, Long> {

	@Query("select a from Aplicacao a where a.vacina.descricao like :search%")
	Page<Aplicacao> findByName(String search, Pageable pageable);

	@Query("select a from Aplicacao a where a.id = :id order by a.id DESC")
	List<Aplicacao> findAplicacaoPorAnimal(Long id);

	@Query("select a from Aplicacao a where a.animal.id = :idAnimal")
	Page<Aplicacao> findByAnimalId(Pageable pageable, Long idAnimal);

	@Query("select a from Aplicacao a where a.vacina.descricao like :descricao and a.animal.id = :id")
	List<Aplicacao> findByDesc(String descricao, Long id);

}
