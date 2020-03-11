package com.sponton.vetsystem.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sponton.vetsystem.domain.Internacao;
import com.sponton.vetsystem.repository.InternacaoRepository;

@Service
public class InternacaoService {

	@Autowired
	private InternacaoRepository repository;
	
	@Transactional(readOnly = false)
	public void salvarInternacao(@Valid Internacao internacao) {
		repository.save(internacao);
	}

	@Transactional(readOnly = true)
	public List<Internacao> buscarInternacaoAtiva() {
		return repository.findInternacaoByAtiva();
	}

	@Transactional(readOnly = true)
	public List<Internacao> buscarInternacaoEncerrada() {
		return repository.findInternacaoByEncerrada();
	}

	@Transactional(readOnly = true)
	public Internacao buscarPorId(Long id) {
		return repository.findById(id).get();
	}

	
}
