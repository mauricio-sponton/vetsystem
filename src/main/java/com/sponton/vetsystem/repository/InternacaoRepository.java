package com.sponton.vetsystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Internacao;

public interface InternacaoRepository extends JpaRepository<Internacao, Long> {

	@Query("select i from Internacao i where i.status = 'Ativa'")
	List<Internacao> findInternacaoByAtiva();

	@Query("select i from Internacao i where i.status = 'Encerrada'")
	List<Internacao> findInternacaoByEncerrada();

	@Query("select i from Internacao i where i.status like :search% OR i.animal.nome like :search% order by i.id DESC")
	Page<Internacao> findByStatusOrAnimal(String search, Pageable pageable);

	@Query("select i from Internacao i where i.animal.id = :id order by i.id DESC")
	Page<Internacao> findInternacaoByAnimal(Pageable pageable, Long id);


	
}
