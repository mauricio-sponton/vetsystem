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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Foto;
import com.sponton.vetsystem.domain.Usuario;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.FotoService;
import com.sponton.vetsystem.service.UsuarioService;
import com.sponton.vetsystem.service.VeterinarioService;

@Controller
@RequestMapping("veterinarios")
public class VeterinarioController {

	@Autowired
	private VeterinarioService service;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	FotoService fotoService;
	
	@GetMapping("/dados")
	public String abrirPorVeterinario(Veterinario veterinario, ModelMap model, @AuthenticationPrincipal User user) {
		if(veterinario.hasNotId()) {
			veterinario = service.buscarPorEmail(user.getUsername());
			model.addAttribute("veterinario", veterinario);
		}
		return "veterinario/cadastro";
	}
	@PostMapping("/salvar")
	public String salvar(@Valid Veterinario veterinario, BindingResult result, RedirectAttributes attr, @AuthenticationPrincipal User user,@RequestParam("file") MultipartFile file) {
		if(result.hasErrors()) {
			return "veterinario/cadastro";
		}
		try {
			if(veterinario.hasNotId() && veterinario.getUsuario().hasNotId()) {
				Usuario usuario = usuarioService.buscarPorEmail(user.getUsername());
				veterinario.setUsuario(usuario);
			}
			if (!file.isEmpty()) {
				if(veterinario.getFoto().hasNotId()) {
					Foto foto = new Foto();
					foto.setFileName(file.getOriginalFilename());
					foto.setPath("/uploads/");
					veterinario.setFoto(foto);
					try {
						fotoService.salvarFoto(file, foto);
					} catch (Exception e) {
						attr.addFlashAttribute("falha", "Erro ao cadastrar foto!");
					}
				}
				if(veterinario.getFoto().hasId()) {
					Foto foto = fotoService.buscarFotoId(veterinario.getFoto().getId());
					foto.setFileName(file.getOriginalFilename());
					foto.setPath("/uploads/");
					veterinario.setFoto(foto);
					try {
						fotoService.salvarFoto(file, foto);
					} catch (Exception e) {
						attr.addFlashAttribute("falha", "Erro ao cadastrar foto!");
					}
				}
					
			}
			service.salvar(veterinario);
			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
			attr.addFlashAttribute("veterinario", veterinario);
		}catch(DataIntegrityViolationException ex) {
			attr.addFlashAttribute("falha", "CRMV já cadastrado no sistema");
		}
		
		return "redirect:/veterinarios/dados";
		
	}
	
}
