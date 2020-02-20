package com.sponton.vetsystem.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.service.EspecieService;
import com.sponton.vetsystem.service.RacaService;

@Controller
@RequestMapping("especies")
public class EspecieController {

	@Autowired
	EspecieService service;
	
	@Autowired
	RacaService racaService;
	
	@GetMapping("/cadastrar")
	public String novaEspecie(Especie especie) {
		return "especie/cadastro";
	}
	@PostMapping("/salvar")
	public String salvarEspecie(@Valid Especie especie, BindingResult result, RedirectAttributes attr) {
		if(result.hasErrors()) {
			return "especie/cadastro";
		}
		try {
			service.salvarEspecie(especie);
			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		}catch(DataIntegrityViolationException ex) {
			attr.addFlashAttribute("falha", "Cadastro não realizado pois essa espécie já existe no sistema");
		}
		
		return "redirect:/especies/cadastrar";
	}
	@GetMapping("/listar")
	public String listarEspecies() {
		return "especie/lista";
	}
	@GetMapping("/datatables/server")
	public ResponseEntity<?> listarEspeciesDatatables(HttpServletRequest request) {
		return ResponseEntity.ok(service.buscarTodos(request));
	}
	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("especie", service.buscarPorId(id));
		return "especie/cadastro";
	}
	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/especies/listar";
	}
	@GetMapping("/visualizar/{id}")
	public String visualizar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("especie", service.buscarPorId(id));
		model.addAttribute("racas", racaService.buscarRacaPorEspecie(id));
		return "especie/visualizar";
	}
	@GetMapping("/titulo")
	public ResponseEntity<?> getEspeciesPorTermo(@RequestParam("termo") String termo){
		List<String> especies = service.buscarEspecieByTermo(termo);
		return ResponseEntity.ok(especies);
	}
}
