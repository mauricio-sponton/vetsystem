package com.sponton.vetsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.FotoInternacao;

public interface FotoInternacaoRepository extends JpaRepository<FotoInternacao, Long>{

	@Query("select f from FotoInternacao f where f.internacao.id = :id")
	Page<FotoInternacao> findByInternacao(Pageable pageable, Long id);

	@Query("select f from FotoInternacao f where f.fileName like :search% and f.internacao.id = :id")
	Page<FotoInternacao> findByName(String search, Pageable pageable, Long id);

}
