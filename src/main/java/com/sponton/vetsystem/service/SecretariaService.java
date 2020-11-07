package com.sponton.vetsystem.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.repository.SecretariaRepository;


@Service
public class SecretariaService {

	@Autowired
	private SecretariaRepository repository;
	
	@Transactional(readOnly = true)
	public Secretaria buscarPorEmail(String email) {
		return repository.findByUsuarioEmail(email).orElse(new Secretaria());
	}

	@Transactional(readOnly = false)
	public void salvar(@Valid Secretaria secretaria) {
		repository.save(secretaria);
		
	}

	@Transactional(readOnly = false)
	public void editar(@Valid Secretaria secretaria) {
		Secretaria s2 = repository.findById(secretaria.getId()).get();
		s2.setNome(secretaria.getNome());
		
	}

	@Transactional(readOnly = true)
	public Secretaria buscarPorUsuarioId(Long id) {
		return repository.findByUsuarioId(id).orElse(new Secretaria());
	}

	@Transactional(readOnly = true)
	public Secretaria buscarPorId(Long id) {
		return repository.findById(id).get();
	}

	@Transactional(readOnly = true)
	public List<Secretaria> buscarTodos() {

		return repository.findAll();
	}

}
