package com.sponton.vetsystem.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sponton.vetsystem.datatables.Datatables;
import com.sponton.vetsystem.datatables.DatatablesColunas;
import com.sponton.vetsystem.domain.Raca;
import com.sponton.vetsystem.repository.RacaRepository;

@Service
public class RacaService {

	@Autowired
	RacaRepository repository;
	
	@Autowired
	private Datatables datatables;
	
	@Transactional(readOnly = false)
	public void salvarEspecie(@Valid Raca raca) {
		repository.save(raca);
	}

	public Map<String, Object> buscarTodos(HttpServletRequest request) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.RACAS);
		Page<Raca> page =datatables.getSearch().isEmpty() ? repository.findAll(datatables.getPageable())
				: repository.findByName(datatables.getSearch(), datatables.getPageable());
		return datatables.getResponse(page);
	}
	
	@Transactional(readOnly = true)
	public Raca buscarPorId(Long id) {
		return repository.findById(id).get();
	}
	@Transactional(readOnly = false)
	public void remover(Long id) {
		repository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public List<Raca> buscarRacaPorEspecie(Long id) {
		return repository.findRacaByEspecie(id);
		
	}

	public List<Raca> buscarTodasRacas() {
		return repository.findAll();
	}

	@Transactional(readOnly = true)
	public List<String> buscarRacaByTermo(String termo, String especie) {
		return repository.findRacaByTermo(termo, especie);
	}

	@Transactional(readOnly = true)
	public Set<Raca> buscarPorTitulos(String[] titulos) {
		return repository.findByTitulos(titulos);
	}





}
