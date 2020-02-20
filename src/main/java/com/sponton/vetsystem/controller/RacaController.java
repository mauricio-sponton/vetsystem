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
import com.sponton.vetsystem.domain.Raca;
import com.sponton.vetsystem.service.EspecieService;
import com.sponton.vetsystem.service.RacaService;



@Controller
@RequestMapping("racas")
public class RacaController {
	
	@Autowired
	RacaService service;
	
	@Autowired 
	EspecieService especieService;

	@GetMapping("/cadastrar")
	public String novaRaca(Raca raca) {
		return "raca/cadastro";
	}
	@PostMapping("/salvar")
	public String salvarEspecie(@Valid Raca raca, BindingResult result, RedirectAttributes attr) {
		if(result.hasErrors()) {
			return "raca/cadastro";
		}
		try {
			service.salvarEspecie(raca);
			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		}catch(DataIntegrityViolationException ex) {
			attr.addFlashAttribute("falha", "Cadastro não realizado pois essa raça já existe no sistema");
		}
		
		return "redirect:/racas/cadastrar";
	}
	@GetMapping("/listar")
	public String listarRacas() {
		return "raca/lista";
	}
	@GetMapping("/datatables/server")
	public ResponseEntity<?> listarRacasDatatables(HttpServletRequest request) {
		return ResponseEntity.ok(service.buscarTodos(request));
	}
	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("raca", service.buscarPorId(id));
		return "raca/cadastro";
	}
	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/racas/listar";
	}
	@ModelAttribute("especies")
	public List<Especie> listaDeEspecies(){
		return especieService.buscarTodasEspecies();
	}
	@GetMapping("/titulo")
	public ResponseEntity<?> getRacasPorTermo(@RequestParam("termo") String termo, @RequestParam("especie") String especie){
		List<String> racas = service.buscarRacaByTermo(termo, especie);
		return ResponseEntity.ok(racas);
	}
}
