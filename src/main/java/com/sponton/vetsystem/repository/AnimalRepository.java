package com.sponton.vetsystem.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Foto;

public interface AnimalRepository extends JpaRepository<Animal, Long>{

	@Query("select a from Animal a where a.nome like :search% or a.especie.nome like :search% or a.raca.nome like :search% or a.cliente.nome like :search%")
	Page<Animal> findByName(String search, Pageable pageable);

	@Query("select a from Animal a where a.cliente.id = :id")
	List<Animal> findAnimalPorDono(Long id);

	@Query("select a from Animal a where a.nome IN :titulos")
	Set<Animal> findByTitulos(String[] titulos);

	@Query("select a from Animal a where a.nome like :termo%")
	List<Animal> findAnimaisByTermo(String termo);

	@Query("select distinct a.alergias from Animal a where a.nome like :termo")
	List<String> findAnimaisByAlergias(String termo);

	@Query("select a from Animal a where a.raca.id = :id")
	List<Animal> findAnimalByRaca(Long id);

	@Query("select a from Animal a where a.especie.id = :id")
	List<Animal> findAnimalByEspecie(Long id);

	
	
}
