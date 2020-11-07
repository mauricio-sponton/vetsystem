package com.sponton.vetsystem.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Veterinario;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

	@Query("select v from Veterinario v where v.usuario.email like :email")
	Optional<Veterinario> findByUsuarioEmail(String email);

	@Query("select v from Veterinario v where v.usuario.id = :id")
	Optional<Veterinario> findByUsuarioId(Long id);

	@Query("select v.nome from Veterinario v where exists(" + "select c from CargaHoraria c where v.nome = c.veterinario.nome and c.inicio <= :start and c.fim >= :end and c.diaDaSemana between :diaInicial and :diaFinal" + ") " 
			+ "and v.nome like :termo%")
	List<String> findVeterinariosByTermo(String termo, LocalTime start, LocalTime end, int diaInicial, int diaFinal);

	@Query("select v from Veterinario v where v.nome IN :strings")
	Set<Veterinario> findByTitulos(String[] strings);

	@Query("select v.nome from Veterinario v where v.nome like :termo%")
	List<String> findVeterinariosByNome(String termo);

	@Query("select v from Veterinario v where v.nome = :titulo")
	Optional<Veterinario> findVeterinarioByNome(String titulo);


}
