package com.sponton.vetsystem.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sponton.vetsystem.domain.CargaHoraria;
import com.sponton.vetsystem.repository.CargaHorariaRepository;

@Service
public class CargaHorariaService {

	@Autowired
	private CargaHorariaRepository repository;
	
	@Transactional(readOnly = false)
	public void salvar(@Valid CargaHoraria cargaHoraria) {
		repository.save(cargaHoraria);
		
	}
	@Transactional(readOnly = false)
	public List<CargaHoraria> salvarTodos(List<CargaHoraria> cargas) {
		return repository.saveAll(cargas);
		
	}
	@Transactional(readOnly = true)
	public List<CargaHoraria> buscarHorarioPorVeterinario(Long id) {
		return repository.findHorarioByVeterinario(id);
	}
	
}
