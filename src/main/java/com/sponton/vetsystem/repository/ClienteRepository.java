package com.sponton.vetsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sponton.vetsystem.domain.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	@Query("select c from Cliente c where c.nome like :search% or c.email like :search%")
	Page<Cliente> findByName(String search, Pageable pageable);

}
