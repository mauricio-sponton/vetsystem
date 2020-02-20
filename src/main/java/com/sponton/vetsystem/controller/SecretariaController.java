package com.sponton.vetsystem.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.domain.Usuario;
import com.sponton.vetsystem.service.SecretariaService;
import com.sponton.vetsystem.service.UsuarioService;

@Controller
@RequestMapping("secretarias")
public class SecretariaController {

	@Autowired
	SecretariaService service;

	@Autowired
	UsuarioService usuarioService;

	@GetMapping("/dados")
	public String abrirPorVeterinario(Secretaria secretaria, ModelMap model, @AuthenticationPrincipal User user) {
		if (secretaria.hasNotId()) {
			secretaria = service.buscarPorEmail(user.getUsername());
			model.addAttribute("secretaria", secretaria);
		}
		return "secretaria/cadastro";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid Secretaria secretaria, BindingResult result, RedirectAttributes attr,
			@AuthenticationPrincipal User user) {
		if (result.hasErrors()) {
			return "secretaria/cadastro";
		}

		if (secretaria.hasNotId() && secretaria.getUsuario().hasNotId()) {
			Usuario usuario = usuarioService.buscarPorEmail(user.getUsername());
			secretaria.setUsuario(usuario);
		}
		service.salvar(secretaria);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		attr.addFlashAttribute("secretaria", secretaria);

		return "redirect:/secretarias/dados";

	}

	@PostMapping("/editar")
	public String editar(@Valid Secretaria secretaria, BindingResult result, RedirectAttributes attr) {
		if (result.hasErrors()) {
			return "secretaria/cadastro";
		}

		service.editar(secretaria);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		attr.addFlashAttribute("secretaria", secretaria);

		return "redirect:/secretarias/dados";
	}
}
