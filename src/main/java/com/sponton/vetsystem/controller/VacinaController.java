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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.domain.Vacina;
import com.sponton.vetsystem.service.EspecieService;
import com.sponton.vetsystem.service.VacinaService;

@Controller
@RequestMapping("vacinas")
public class VacinaController {
	
	@Autowired
	private VacinaService service;
	
	@Autowired 
	private EspecieService especieService;
	
	@PostMapping("/salvar")
	public String salvarVacina(@Valid Vacina vacina, BindingResult result, RedirectAttributes attr, ModelMap model) {
		if(result.hasErrors()) {
			model.addAttribute("erro", "Por favor preencha os dados");
			return "vacina/lista";
		}
		try {
			if(vacina.hasNotId()) {
				List<Vacina> vacinas = service.buscarVacinasPorNomeEEspecie(vacina.getEspecie().getNome(),vacina.getDescricao());
				if(vacinas.size() > 0) {
					attr.addFlashAttribute("falha", "Cadastro não realizado pois essa vacina já está cadastrada no sistema");
					return "redirect:/vacinas/listar";
				}
			}
			service.salvarVacina(vacina);
			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		}catch(DataIntegrityViolationException ex) {
			attr.addFlashAttribute("falha", "Cadastro não realizado pois essa vacina já está cadastrada no sistema");
		}
		
		return "redirect:/vacinas/listar";
	}
	@GetMapping("/listar")
	public String listarVacinas(Vacina vacina) {
		return "vacina/lista";
	}
	@GetMapping("/datatables/server")
	public ResponseEntity<?> listarVacinasDatatables(HttpServletRequest request) {
		return ResponseEntity.ok(service.buscarTodos(request));
	}
	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("vacina", service.buscarPorId(id));
		return "vacina/lista";
	}
	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/vacinas/listar";
	}
	@ModelAttribute("especies")
	public List<Especie> listaDeEspecies(){
		return especieService.buscarTodasEspecies();
	}
	
}
