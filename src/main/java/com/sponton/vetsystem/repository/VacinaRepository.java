package com.sponton.vetsystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Vacina;

public interface VacinaRepository extends JpaRepository<Vacina, Long>{

	@Query("select v from Vacina v where v.descricao like :search%")
	Page<Vacina> findByName(String search, Pageable pageable);


}
