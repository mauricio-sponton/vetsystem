package com.sponton.vetsystem.service;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.repository.VeterinarioRepository;

@Service
public class VeterinarioService {
	
	@Autowired 
	private VeterinarioRepository repository;

	@Transactional(readOnly = true)
	public Veterinario buscarPorEmail(String email) {
		return repository.findByUsuarioEmail(email).orElse(new Veterinario());
	}

	@Transactional(readOnly = false)
	public void salvar(@Valid Veterinario veterinario) {
		repository.save(veterinario);
	}

	@Transactional(readOnly = false)
	public void editar(@Valid Veterinario veterinario) {
		Veterinario v2 = repository.findById(veterinario.getId()).get();
		v2.setCrmv(veterinario.getCrmv());
		v2.setNome(veterinario.getNome());
		
	}
	
	@Transactional(readOnly = true)
	public Veterinario buscarPorUsuarioId(Long id) {
		return repository.findByUsuarioId(id).orElse(new Veterinario());
	}

}
