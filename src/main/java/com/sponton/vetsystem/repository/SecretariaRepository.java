package com.sponton.vetsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Secretaria;



public interface SecretariaRepository extends JpaRepository<Secretaria, Long> {

	@Query("select s from Secretaria s where s.usuario.email like :email")
	Optional<Secretaria> findByUsuarioEmail(String email);

	@Query("select s from Secretaria s where s.usuario.id = :id")
	Optional<Secretaria> findByUsuarioId(Long id);

}
