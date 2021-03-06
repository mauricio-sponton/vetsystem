package com.sponton.vetsystem.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sponton.vetsystem.domain.Agendamento;
import com.sponton.vetsystem.domain.Notificacao;
import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.repository.AgendamentoRepository;

@Service
public class AgendamentoService {

	@Autowired
	private AgendamentoRepository repository;
	
	@Transactional(readOnly = false)
	public void salvar(@Valid Agendamento agendamento) {
		repository.save(agendamento);
	}

	@Transactional(readOnly = true)
	public List<Agendamento> buscarTodos() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}

	@Transactional(readOnly = false)
	public void remover(Long id) {
		repository.deleteById(id);	
	}

	@Transactional(readOnly = true)
	public List<Agendamento> buscarVeterinarioPorId(Long id) {
		return repository.findVeterinarioById(id);
	}

	@Transactional(readOnly = true)
	public Agendamento buscarPorId(Long id) {
		return repository.findById(id).get();
	}

	
}
