package com.sponton.vetsystem.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sponton.vetsystem.datatables.Datatables;
import com.sponton.vetsystem.datatables.DatatablesColunas;
import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Consulta;
import com.sponton.vetsystem.repository.ConsultaRepository;

@Service
public class ConsultaService {

	@Autowired
	private ConsultaRepository repository;
	
	@Autowired
	private Datatables datatables;
	
	public void salvarConsulta(@Valid Consulta consulta) {
		repository.save(consulta);
	}

	public Map<String, Object> buscarTodos(HttpServletRequest request, Long id) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.CONSULTAS);
		Page<Consulta> page =datatables.getSearch().isEmpty() ? repository.findByVeterinario(datatables.getPageable(), id)
				: repository.findByName(datatables.getSearch(), datatables.getPageable(), id);
		return datatables.getResponse(page);
	}

	@Transactional(readOnly = true)
	public Consulta buscarPorId(Long id) {
		return repository.findById(id).get();
	}

	@Transactional(readOnly = false)
	public void remover(Long id) {
		repository.deleteById(id);
	}

	@Transactional(readOnly = true)	
	public List<Consulta> buscarConsultaPorAnimal(Long id) {
		return repository.findConsultaByAnimal(id);
	}

	public Map<String, Object> buscarConsultasPorAnimal(HttpServletRequest request, Long idAnimal) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.CONSULTAS);
		Page<Consulta> page =datatables.getSearch().isEmpty() ? repository.findByAnimalId(datatables.getPageable(), idAnimal)
				: repository.findByName(datatables.getSearch(), datatables.getPageable(), idAnimal);
		return datatables.getResponse(page);
	}

}
