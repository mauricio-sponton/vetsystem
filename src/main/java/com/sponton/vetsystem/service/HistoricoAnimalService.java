package com.sponton.vetsystem.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sponton.vetsystem.domain.HistoricoAnimal;
import com.sponton.vetsystem.repository.HistoricoAnimalRepository;

@Service
public class HistoricoAnimalService {

	@Autowired 
	private HistoricoAnimalRepository repository;
	
	@Transactional(readOnly = false)
	public void salvar(HistoricoAnimal historico) {
		repository.save(historico);
		
	}

	@Transactional(readOnly = true)	
	public List<HistoricoAnimal> buscarHistoricoPorAnimal(Long id) {
		return repository.findHistoricoByAnimal(id);
	}

	@Transactional(readOnly = true)
	public List<HistoricoAnimal> buscarHistoricoPorTipo(String tipo, Long id) {
		return repository.findHistoricoByTipo(tipo, id);
	}

	@Transactional(readOnly = true)
	public List<HistoricoAnimal> buscarTodos() {
		return repository.findAll();
	}

	public List<HistoricoAnimal> buscarHistoricoPorData(LocalDate data, Long id) {
		return repository.findHistoricoPorData(data, id);
	}

	@Transactional(readOnly = true)
	public List<HistoricoAnimal> buscarHistoricoPorAnimalEStatus(Long id) {
		return repository.findHistoricoByAnimalAndStatus(id);
	}

	@Transactional(readOnly = false)
	public void remover(Long id) {
		repository.deleteById(id);
		
	}


}
