package com.sponton.vetsystem.service;

import java.time.LocalDateTime;
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
import com.sponton.vetsystem.repository.AnimalRepository;

@Service
public class AnimalService {

	@Autowired
	AnimalRepository repository;
	
	@Autowired
	private Datatables datatables;

	@Transactional(readOnly = false)
	public void salvarAnimal(@Valid Animal animal) {
		repository.save(animal);
		
	}

	public Map<String, Object> buscarTodos(HttpServletRequest request) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.ANIMAIS);
		Page<Animal> page =datatables.getSearch().isEmpty() ? repository.findAll(datatables.getPageable())
				: repository.findByName(datatables.getSearch(), datatables.getPageable());
		return datatables.getResponse(page);
	}

	@Transactional(readOnly = true)
	public Animal buscarPorId(Long id) {
		return repository.findById(id).get();
	}
	
	@Transactional(readOnly = false)
	public void remover(Long id) {
		repository.deleteById(id);
		
	}
	
	@Transactional(readOnly = true)
	public List<Animal> buscarAnimalPorDono(Long id) {
		return repository.findAnimalPorDono(id);
	}

	public List<Animal> buscarTodosAnimais() {
		return repository.findAll();
	}
}
