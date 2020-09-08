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
import com.sponton.vetsystem.domain.Aplicacao;
import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.domain.Vacina;
import com.sponton.vetsystem.repository.AplicacaoRepository;

@Service
public class AplicacaoService {

	@Autowired 
	private AplicacaoRepository repository;
	
	@Autowired
	private Datatables datatables;
	
	@Transactional(readOnly = false)
	public void salvarAplicacao(@Valid Aplicacao aplicacao) {
		repository.save(aplicacao);
		
	}

	public Map<String, Object> buscarTodos(HttpServletRequest request, Long idAnimal) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.APLICACOES);
		Page<Aplicacao> page =datatables.getSearch().isEmpty() ? repository.findByAnimalId(datatables.getPageable(),idAnimal)
				: repository.findByName(datatables.getSearch(), datatables.getPageable());
		return datatables.getResponse(page);
	}

	@Transactional(readOnly = true)
	public Aplicacao buscarPorId(Long id) {
		return repository.findById(id).get();
	}

	@Transactional(readOnly = false)
	public void remover(Long id) {
		repository.deleteById(id);
	}

	@Transactional(readOnly = true)	
	public List<Aplicacao> buscarAplicacaoPorAnimal(Long id) {
		return repository.findAplicacaoPorAnimal(id);
	}

	/*
	@Transactional(readOnly = false)
	public void editar(Aplicacao aplicacao) {
		Aplicacao a2 = repository.findById(aplicacao.getId()).get();
		a2.setVacina(aplicacao.getVacina());
		a2.setDataAplicacao(aplicacao.getDataAplicacao());
		repository.save(a2);
		
	}
	*/

}
