package com.sponton.vetsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Foto;



public interface FotoRepository extends JpaRepository<Foto, Long>{

	@Query("select f from Foto f where f.internacao.id = :id")
	List<Foto> findByFotosId(Long id);


}
