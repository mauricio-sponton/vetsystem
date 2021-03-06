package com.sponton.vetsystem.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sponton.vetsystem.datatables.Datatables;
import com.sponton.vetsystem.datatables.DatatablesColunas;
import com.sponton.vetsystem.domain.CargaHoraria;
import com.sponton.vetsystem.domain.Cliente;
import com.sponton.vetsystem.domain.Notificacao;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.repository.CargaHorariaRepository;
import com.sponton.vetsystem.repository.VeterinarioRepository;

@Service
public class VeterinarioService {
	
	@Autowired 
	private VeterinarioRepository repository;
	
	@Autowired
	private CargaHorariaRepository cargaRepository;
	
	@Autowired
	private Datatables datatables;

	@Transactional(readOnly = true)
	public Veterinario buscarPorEmail(String email) {
		return repository.findByUsuarioEmail(email).orElse(new Veterinario());
	}

	@Transactional(readOnly = false)
	public void salvar(@Valid Veterinario veterinario) {
		repository.save(veterinario);
	}

	@Transactional(readOnly = false)
	public void editar(@Valid Veterinario veterinario) {
		Veterinario v2 = repository.findById(veterinario.getId()).get();
		v2.setCrmv(veterinario.getCrmv());
		v2.setNome(veterinario.getNome());
		
	}
	
	@Transactional(readOnly = true)
	public Veterinario buscarPorUsuarioId(Long id) {
		return repository.findByUsuarioId(id).orElse(new Veterinario());
	}

	@Transactional(readOnly = true)
	public Veterinario buscarPorId(Long id) {
		return repository.findById(id).get();
	}
	/*
	@Transactional(readOnly = true)
	public List<String> buscarVeterinariosByTermo(String termo, LocalTime start, LocalTime end, int diaInicial, int diaFinal, LocalDateTime inicio, LocalDateTime fim) {
		return repository.findVeterinariosByTermo(termo, start, end, diaInicial, diaFinal, inicio, fim);
	}
*/
	@Transactional(readOnly = true)
	public Set<Veterinario> buscarPorTitulos(String[] strings) {
		return repository.findByTitulos(strings);
	}

	@Transactional(readOnly = true)
	public List<String> buscarVeterinariosPeloNome(String termo) {
		return repository.findVeterinariosByNome(termo);
	}

	@Transactional(readOnly = true)
	public Optional<Veterinario> buscarVeterinarioPeloNome(String titulo) {
		return repository.findVeterinarioByNome(titulo);
	}

	@Transactional(readOnly = true)
	public Map<String, Object> buscarTodos(HttpServletRequest request) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.CLIENTES);
		Page<Veterinario> page = datatables.getSearch().isEmpty() ? repository.findAll(datatables.getPageable())
				: repository.findByName(datatables.getSearch(), datatables.getPageable());
		return datatables.getResponse(page);
	}

	@Transactional(readOnly = true)
	public List<Veterinario> buscarVeterinariosDisponiveis(LocalTime start, LocalTime end, int diaInicial,
			int diaFinal, LocalDateTime inicio, LocalDateTime fim) {
		return repository.findVeterinariosDisponiveis(start, end, diaInicial, diaFinal, inicio, fim);
	}

	@Transactional(readOnly = false)
	public void remover(Long id) {
		repository.deleteById(id);
		
	}

	@Transactional(readOnly = true)
	public List<Veterinario> buscarTodosVeterinarios() {
		return repository.findAll();
	}

	@Transactional(readOnly = false)
	public void removerNotificacaoPorVeterinarioId(Long id, Long idNotificacao) {
		Veterinario veterinario = repository.findById(id).get();
		veterinario.getNotificacoes().removeIf(e -> e.getId().equals(idNotificacao));
		
	}


}
