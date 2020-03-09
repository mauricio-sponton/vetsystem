package com.sponton.vetsystem.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Consulta;
import com.sponton.vetsystem.domain.HistoricoAnimal;
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.AnimalService;
import com.sponton.vetsystem.service.ConsultaService;
import com.sponton.vetsystem.service.HistoricoAnimalService;
import com.sponton.vetsystem.service.VeterinarioService;

@Controller
@RequestMapping("consultas")
public class ConsultaController {

	@Autowired
	private ConsultaService service;
	
	@Autowired
	private AnimalService animalService;
	
	@Autowired
	private VeterinarioService veterinarioService;
	
	@Autowired
	private HistoricoAnimalService historicoAnimalService;
	
	@GetMapping("/cadastrar")
	public String novaConsulta(Consulta consulta) {
		return "consulta/cadastro";
	}
	@PostMapping("/salvar")
	public String salvarConsulta(@Valid Consulta consulta, BindingResult result, RedirectAttributes attr, @AuthenticationPrincipal User user) {
		if(result.hasErrors()) {
			return "consulta/cadastro";
		}
		String titulo = consulta.getAnimal().getNome();
		Animal animal = animalService.buscarPorTitulos(new String[] { titulo}).stream().findFirst().get();
		
		HistoricoAnimal historico = new HistoricoAnimal();
		LocalDate data = LocalDate.now();
		LocalTime hora = LocalTime.now();
		
		if(user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.VETERINARIO.getDesc()))) {
			Veterinario veterinario = veterinarioService.buscarPorEmail(user.getUsername());
			if(veterinario.hasNotId()) {
				attr.addFlashAttribute("falha", "Por favor preencha seus dados pessoais para continuar usando o sistema");
				return "redirect:/veterinarios/dados";
			}
			if(consulta.hasNotId()) {
				historico.setDescricao("Uma nova consulta foi registrada");
				historico.setTipo("Nova consulta");
				historico.setUsuario(veterinario.getNome() + " (veterinario)");
				historico.setData(data);
				historico.setHora(hora);

			}
			if(consulta.hasId()) {
				historico.setDescricao("As informações da consulta foram alteradas");
				historico.setTipo("Alteração de consulta");
				historico.setUsuario(veterinario.getNome() + " (veterinario)");
				historico.setData(data);
				historico.setHora(hora);
			}
			consulta.setAnimal(animal);
			consulta.setVeterinario(veterinario);
			service.salvarConsulta(consulta);

			historico.setAnimal(animal);
			historicoAnimalService.salvar(historico);
			
			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		}
		return "redirect:/consultas/cadastrar";
	}
	@GetMapping("/listar")
	public String listarConsultas() {
		return "consulta/lista";
	}
	@GetMapping("/datatables/server")
	public ResponseEntity<?> listarAnimaisDatatables(HttpServletRequest request, @AuthenticationPrincipal User user) {
		Veterinario veterinario = veterinarioService.buscarPorEmail(user.getUsername());
		return ResponseEntity.ok(service.buscarTodos(request, veterinario.getId()));
	}
	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("consulta", service.buscarPorId(id));
		return "consulta/cadastro";
	}
	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/consultas/listar";
	}
	@GetMapping("/visualizar/{id}")
	public String visualizar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("consulta", service.buscarPorId(id));
		return "consulta/visualizar";
	}

}
