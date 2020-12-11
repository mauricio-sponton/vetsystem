package com.sponton.vetsystem.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Cliente;
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.UF;
import com.sponton.vetsystem.service.AnimalService;
import com.sponton.vetsystem.service.ClienteService;

@Controller
@RequestMapping("clientes")
public class ClienteController {

	@Autowired
	private ClienteService service;
	
	@Autowired 
	private AnimalService animalService;

	@GetMapping("/cadastrar")
	public String cadastrar(Cliente cliente) {
		return "cliente/cadastro";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid Cliente cliente, BindingResult result, RedirectAttributes attr, ModelMap model) {
		if (result.hasErrors()) {
			model.addAttribute("erro", "por favor preencha os campos");
			return "/cliente/lista";
		}
		try {
			if (cliente.hasNotId()) {
				service.salvar(cliente);
				attr.addFlashAttribute("sucesso", "Cliente cadastrado com sucesso");
			} else {
				service.salvar(cliente);
				attr.addFlashAttribute("sucesso", "Dados alterados com sucesso");
			}
		} catch (DataIntegrityViolationException ex) {
			attr.addFlashAttribute("falha", "Cadastro não realizado pois o email ou cpf já existe");
		}

		return "redirect:/clientes/listar";
	}

	@ModelAttribute("ufs")
	public UF[] getUFs() {
		return UF.values();
	}

	@GetMapping("/listar")
	public String listarClientes(Cliente cliente, @AuthenticationPrincipal User user) {
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.VETERINARIO.getDesc()))) {
			return "cliente/lista-vet";
		}
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.SECRETARIA.getDesc()))) {
			return "cliente/lista";
		}
		return "cliente/lista";
	}

	@GetMapping("/datatables/server")
	public ResponseEntity<?> listarUsuariosDatatables(HttpServletRequest request) {
		return ResponseEntity.ok(service.buscarTodos(request));
	}

	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("cliente", service.buscarPorId(id));
		return "cliente/lista";
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/clientes/listar";
	}

	@GetMapping("/visualizar/{id}")
	public String visualizar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("cliente", service.buscarPorId(id));
		model.addAttribute("animais", animalService.buscarAnimalPorDono(id));
		return "/cliente/visualizar";
	}

}
