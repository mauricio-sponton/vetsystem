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
import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.repository.EspecieRepository;

@Service
public class EspecieService {

	@Autowired 
	EspecieRepository repository;
	
	@Autowired
	private Datatables datatables;
	
	@Transactional(readOnly = false)
	public void salvarEspecie(@Valid Especie especie) {
		repository.save(especie);
	}

	public Map<String, Object> buscarTodos(HttpServletRequest request) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.ESPECIES);
		Page<Especie> page =datatables.getSearch().isEmpty() ? repository.findAll(datatables.getPageable())
				: repository.findByName(datatables.getSearch(), datatables.getPageable());
		return datatables.getResponse(page);
	}

	@Transactional(readOnly = true)
	public Especie buscarPorId(Long id) {
		return repository.findById(id).get();
	}

	@Transactional(readOnly = false)
	public void remover(Long id) {
		repository.deleteById(id);
	}

	public List<Especie> buscarTodasEspecies() {
		return repository.findAll();
	}

	@Transactional(readOnly = true)
	public List<String> buscarEspecieByTermo(String termo) {
		return repository.findEspecieByTermo(termo);
	}
	@Transactional(readOnly = true)
	public Set<Especie> buscarPorTitulos(String[] titulos) {
		return repository.findByTitulos(titulos);
	}

}
