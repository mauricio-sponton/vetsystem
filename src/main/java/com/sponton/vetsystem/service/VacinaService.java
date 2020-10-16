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
import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.domain.Vacina;
import com.sponton.vetsystem.repository.VacinaRepository;

@Service
public class VacinaService {
	@Autowired 
	private VacinaRepository repository;
	
	@Autowired
	private Datatables datatables;

	@Transactional(readOnly = false)
	public void salvarVacina(@Valid Vacina vacina) {
		repository.save(vacina);
	}

	public Map<String, Object> buscarTodos(HttpServletRequest request) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.VACINAS);
		Page<Vacina> page =datatables.getSearch().isEmpty() ? repository.findAll(datatables.getPageable())
				: repository.findByName(datatables.getSearch(), datatables.getPageable());
		return datatables.getResponse(page);
	}

	@Transactional(readOnly = true)
	public Vacina buscarPorId(Long id) {
		return repository.findById(id).get();
	}

	@Transactional(readOnly = false)
	public void remover(Long id) {
		repository.deleteById(id);
	}
	
	public List<Vacina> buscarTodasVacinas(){
		return repository.findAll();
	}

	@Transactional(readOnly = true)
	public List<Vacina> buscarVacinasPorNomeEEspecie(String especie, String descricao) {

		return repository.findByNomeAndEspecie(especie, descricao);
	}

	public List<Vacina> buscarTodasVacinasPorEspecie(String especie) {
		return repository.findVacinaByEspecie(especie);
	}

}
