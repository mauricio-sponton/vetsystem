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
import com.sponton.vetsystem.domain.Aplicacao;
import com.sponton.vetsystem.domain.Cliente;
import com.sponton.vetsystem.domain.Internacao;
import com.sponton.vetsystem.repository.InternacaoRepository;

@Service
public class InternacaoService {

	@Autowired
	private InternacaoRepository repository;
	
	@Autowired
	private Datatables datatables;
	
	@Transactional(readOnly = false)
	public void salvarInternacao(@Valid Internacao internacao) {
		repository.save(internacao);
	}

	@Transactional(readOnly = true)
	public List<Internacao> buscarInternacaoAtiva() {
		return repository.findInternacaoByAtiva();
	}

	@Transactional(readOnly = true)
	public List<Internacao> buscarInternacaoEncerrada() {
		return repository.findInternacaoByEncerrada();
	}

	@Transactional(readOnly = true)
	public Internacao buscarPorId(Long id) {
		return repository.findById(id).get();
	}

	@Transactional(readOnly = true)
	public Map<String, Object> buscarTodos(HttpServletRequest request) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.INTERNACOES);
		Page<Internacao> page = datatables.getSearch().isEmpty() ? repository.findAll(datatables.getPageable())
				: repository.findByStatusOrAnimal(datatables.getSearch(), datatables.getPageable());
		return datatables.getResponse(page);
	}

	@Transactional(readOnly = false)
	public void remover(Long id) {
		repository.deleteById(id);

	}

	public Map<String, Object> buscarInternacaoPorAnimal(HttpServletRequest request, Long idAnimal) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.INTERNACOES);
		Page<Internacao> page =datatables.getSearch().isEmpty() ? repository.findInternacaoByAnimal(datatables.getPageable(),idAnimal)
				: repository.findByStatusOrAnimal(datatables.getSearch(), datatables.getPageable());
		return datatables.getResponse(page);
	}

	@Transactional(readOnly = true)
	public List<Internacao> buscarTodasInternacoesAtiva() {
		
		return repository.findInternacaoByAtiva();
	}

	

	
	
}
