package com.sponton.vetsystem.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Usuario;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.UsuarioService;
import com.sponton.vetsystem.service.VeterinarioService;

@Controller
@RequestMapping("veterinarios")
public class VeterinarioController {

	@Autowired
	private VeterinarioService service;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping("/dados")
	public String abrirPorVeterinario(Veterinario veterinario, ModelMap model, @AuthenticationPrincipal User user) {
		if(veterinario.hasNotId()) {
			veterinario = service.buscarPorEmail(user.getUsername());
			model.addAttribute("veterinario", veterinario);
		}
		return "veterinario/cadastro";
	}
	@PostMapping("/salvar")
	public String salvar(@Valid Veterinario veterinario, BindingResult result, RedirectAttributes attr, @AuthenticationPrincipal User user) {
		if(result.hasErrors()) {
			return "veterinario/cadastro";
		}
		try {
			if(veterinario.hasNotId() && veterinario.getUsuario().hasNotId()) {
				Usuario usuario = usuarioService.buscarPorEmail(user.getUsername());
				veterinario.setUsuario(usuario);
			}
			service.salvar(veterinario);
			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
			attr.addFlashAttribute("veterinario", veterinario);
		}catch(DataIntegrityViolationException ex) {
			attr.addFlashAttribute("falha", "CRMV já cadastrado no sistema");
		}
		
		return "redirect:/veterinarios/dados";
		
	}
	@PostMapping("/editar")
	public String editar(@Valid Veterinario veterinario, BindingResult result, RedirectAttributes attr) {
		if(result.hasErrors()) {
			return "veterinario/cadastro";
		}
		try {
			service.editar( veterinario);
			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
			attr.addFlashAttribute("veterinario",  veterinario);
		}catch(DataIntegrityViolationException ex) {
			attr.addFlashAttribute("falha", "CRMV já cadastrado no sistema");
		}
		
		return "redirect:/veterinarios/dados";
	}
}
