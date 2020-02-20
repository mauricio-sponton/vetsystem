package com.sponton.vetsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Veterinario;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

	@Query("select v from Veterinario v where v.usuario.email like :email")
	Optional<Veterinario> findByUsuarioEmail(String email);

	@Query("select v from Veterinario v where v.usuario.id = :id")
	Optional<Veterinario> findByUsuarioId(Long id);

}
