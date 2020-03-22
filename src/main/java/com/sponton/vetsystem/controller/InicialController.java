package com.sponton.vetsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.ModelMap;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.SecretariaService;
import com.sponton.vetsystem.service.VeterinarioService;

@ControllerAdvice(annotations = Controller.class)
public class InicialController {
	
	@Autowired
	private VeterinarioService veterinarioService;
	
	@Autowired
	private SecretariaService secretariaService;
	
	@ModelAttribute("currentRole")
	public void consultarNome(@AuthenticationPrincipal User user, ModelMap model) {
		if(user !=null) {
			if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.VETERINARIO.getDesc()))) {
				Veterinario veterinario = veterinarioService.buscarPorEmail(user.getUsername());
				model.addAttribute("veterinario", veterinario);
			}
			if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.SECRETARIA.getDesc()))) {
				Secretaria secretaria = secretariaService.buscarPorEmail(user.getUsername());
				model.addAttribute("secretaria", secretaria);
			}
		}
		
	}

}
