package com.sponton.vetsystem.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Especie;

public interface EspecieRepository extends JpaRepository<Especie, Long>{

	@Query("select e from Especie e where e.nome like :search%")
	Page<Especie> findByName(String search, Pageable pageable);

	@Query("select e.nome from Especie e where e.nome like :termo%")
	List<String> findEspecieByTermo(String termo);

	@Query("select e from Especie e where e.nome IN :titulos")
	Set<Especie> findByTitulos(String[] titulos);

	@Query("select e from Especie e where e.nome like :string")
	Especie findEspecieByAnimal(String string);

}
