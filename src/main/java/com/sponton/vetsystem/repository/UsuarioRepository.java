package com.sponton.vetsystem.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sponton.vetsystem.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	@Query("select u from Usuario u where u.email like :email")
	Usuario findByEmail(@Param("email") String email);
	
	@Query("select u from Usuario u where u.email like :email AND u.ativo = true")
	Optional<Usuario> findByEmailEAtivo(String email);

	@Query("select distinct u from Usuario u " + "join u.perfis p " + "where u.email like :search% OR p.desc like :search%")
	Page<Usuario> findByEmailOrPerfil(String search, Pageable pageable);

	@Query("select u from Usuario u " + "join u.perfis p " + "where u.id = :usuarioId AND p.id IN :perfisId")
	Optional<Usuario> findByIdAndPerfis(Long usuarioId, Long[] perfisId);
}
