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
	public List<Notificacao> buscarNotificacaoPorVeterinarioId(Long id) {
		return repository.findNotificacaoPorVeterinarioId(id);
	}

	@Transactional(readOnly = true)
	public List<Notificacao> buscarTodas() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}

	@Transactional(readOnly = false)
	public void salvar(Notificacao notificacao) {
		repository.save(notificacao);
		
	}
	/*
	@Transactional(readOnly = false)
	public void salvarTodos(List<Notificacao> lista) {
		repository.saveAll(lista);
		
	}

	@Transactional(readOnly = true)
	public Notificacao buscar(Long idAgendamento, Long idVet) {
		
		return repository.buscar(idAgendamento, idVet);
	}

	@Transactional(readOnly = true)
	public List<Notificacao> buscarNotificacaoPorVeterinarioIdEData(Long id, LocalDate hoje) {
		return repository.findNotificacaoByVeterinarioIdAndData(id, hoje);
	}
    */


}
