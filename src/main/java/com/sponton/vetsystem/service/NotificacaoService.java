package com.sponton.vetsystem.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sponton.vetsystem.domain.Notificacao;
import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.repository.NotificacaoRepository;

@Service
public class NotificacaoService {

	@Autowired
	private NotificacaoRepository repository;
	
	@Transactional(readOnly = true)
	public List<Notificacao> buscarNotificacaoPorSecretariaId(Long id) {
		return repository.findNotificacaoBySecretariaId(id);
	}

	@Transactional(readOnly = true)
	public List<Notificacao> buscarTodas() {
		return repository.findAll();
	}

	@Transactional(readOnly = false)
	public void salvar(Notificacao notificacao) {
		repository.save(notificacao);
		
	}
	
	@Transactional(readOnly = true)
	public Notificacao buscarPorAgendamentoId(Long id) {
		return repository.findById(id).get();
	}

	@Transactional(readOnly = true)
	public List<Notificacao> buscarNotificacaoPorVeterinarioId(Long id) {
		return repository.findNotificacaoByVeterinarioId(id);
	}


}
