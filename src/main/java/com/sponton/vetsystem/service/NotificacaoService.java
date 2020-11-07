package com.sponton.vetsystem.service;

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



}
