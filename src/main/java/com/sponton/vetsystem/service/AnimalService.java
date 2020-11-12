package com.sponton.vetsystem.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sponton.vetsystem.datatables.Datatables;
import com.sponton.vetsystem.datatables.DatatablesColunas;
import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Foto;
import com.sponton.vetsystem.repository.AnimalRepository;
import com.sponton.vetsystem.repository.FotoRepository;

@Service
public class AnimalService {

	@Autowired
	private AnimalRepository repository;
	
	@Autowired
	private FotoRepository fotoRepository;
	
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

	@Transactional(readOnly = true)
	public Set<Animal> buscarPorTitulos(String[] titulos) {
		return repository.findByTitulos(titulos);
	}

	@Transactional(readOnly = true)
	public List<Animal> buscarAnimaisByTermo(String termo) {
		return repository.findAnimaisByTermo(termo);
		/*
		 * 
		 List<String> sug = new ArrayList<String>();
		List<Animal> animais = repository.findAnimaisByTermo(termo);
		for(Animal a : animais) {
			sug.add(a.getNome() + " (" + a.getCliente().getNome() + ")");
		}
		return sug;
		 * */
	}

	@Transactional(readOnly = true)
	public List<String> buscarAnimaisByAlergias(String termo) {
		return repository.findAnimaisByAlergias(termo);
	}

	@Transactional(readOnly = true)
	public List<Animal> buscarAnimalPorRaca(Long id) {
		return repository.findAnimalByRaca(id);
	}

	@Transactional(readOnly = true)
	public List<Animal> buscarAnimalPorEspecie(Long id) {
		return repository.findAnimalByEspecie(id);
	}

	
	

	
}
