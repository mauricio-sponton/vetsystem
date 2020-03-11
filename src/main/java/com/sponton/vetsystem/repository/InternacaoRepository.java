package com.sponton.vetsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Internacao;

public interface InternacaoRepository extends JpaRepository<Internacao, Long> {

	@Query("select i from Internacao i where i.status = 'Ativa'")
	List<Internacao> findInternacaoByAtiva();

	@Query("select i from Internacao i where i.status = 'Encerrada'")
	List<Internacao> findInternacaoByEncerrada();

	
}
