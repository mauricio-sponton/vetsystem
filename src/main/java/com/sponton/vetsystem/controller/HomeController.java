package com.sponton.vetsystem.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.service.AnimalService;
import com.sponton.vetsystem.service.EspecieService;

@Controller
public class HomeController {
	
	@Autowired
	AnimalService animalService;
	
	@Autowired
	EspecieService especieService;
	
	@GetMapping({"/home" })
	public String home(ModelMap model) {
		model.addAttribute("animal", animalService.buscarTodosAnimais());
		return "home";
	}

	// abrir pagina login
	@GetMapping({ "/", "/login" })
	public String login() {
		
		return "login";
	}
	@GetMapping({"/login-error"})
	public String loginError(ModelMap model) {
		model.addAttribute("alerta", "erro");
		model.addAttribute("titulo", "Credenciais inválidas!");
		model.addAttribute("texto", "Login ou senha inválidos, tente novamente");
		model.addAttribute("subtexto", "Acesso permitido apenas para cadastros já ativados");
		return "login";
	}
	// acesso negado
	@GetMapping({"/acesso-negado"})
	public String acessoNegado(ModelMap model, HttpServletResponse resp) {
		model.addAttribute("status", resp.getStatus());
		model.addAttribute("error", "Acesso negado");
		model.addAttribute("message", "Você não tem permissão para acesso a esta área ou ação");					
		return "error";
	}	
	/*
	@RequestMapping("piechart")
	public ResponseEntity<?> getPieChart(){
		List<Especie> animais = especieService.buscarTodasEspecies();
		return new ResponseEntity<>(animais, HttpStatus.OK);
 	}
	@RequestMapping("piechart")
	public ResponseEntity<?> getPieChart(){
		List<Animal> animais = animalService.buscarTodosAnimais();
		return new ResponseEntity<>(animais, HttpStatus.OK);
	} */
}
