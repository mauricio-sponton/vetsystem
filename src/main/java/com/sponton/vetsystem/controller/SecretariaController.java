package com.sponton.vetsystem.controller;

import java.util.Calendar;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.CargaHoraria;
import com.sponton.vetsystem.domain.CargaHorariaDTO;
import com.sponton.vetsystem.domain.Foto;
import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.domain.Usuario;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.CargaHorariaService;
import com.sponton.vetsystem.service.FotoService;
import com.sponton.vetsystem.service.SecretariaService;
import com.sponton.vetsystem.service.UsuarioService;

@Controller
@RequestMapping("secretarias")
public class SecretariaController {

	@Autowired
	private SecretariaService service;

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private FotoService fotoService;
	
	@Autowired
	private CargaHorariaService cargaHorariaService;

	@GetMapping("/dados")
	public String abrirPorSecretaria(Secretaria secretaria, ModelMap model, @AuthenticationPrincipal User user) {
		secretaria = service.buscarPorEmail(user.getUsername());
		if (secretaria.hasNotId()) {
			model.addAttribute("secretaria", secretaria);
			return "secretaria/visualizar";
		}
		if(secretaria.hasId()) {
			model.addAttribute("secretaria", secretaria);
			return "secretaria/visualizar";
		}
		return "secretaria/visualizar";
	}

	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("secretaria", service.buscarPorId(id));
		return "secretaria/cadastro";
	}
	@PostMapping("/salvar")
	public String salvar(@Valid Secretaria secretaria, BindingResult result, RedirectAttributes attr,
			@AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file, ModelMap model) {
		if (result.hasErrors()) {
			model.addAttribute("erro", "Por favor preencha seus dados");
			return "secretaria/visualizar";
		}

		if (secretaria.hasNotId() && secretaria.getUsuario().hasNotId()) {
			Usuario usuario = usuarioService.buscarPorEmail(user.getUsername());
			secretaria.setUsuario(usuario);
		}
		if (!file.isEmpty()) {
			if(secretaria.getFoto().hasNotId()) {
				Foto foto = new Foto();
				foto.setFileName(file.getOriginalFilename());
				foto.setPath("/uploads/");
				secretaria.setFoto(foto);
				try {
					fotoService.salvarFoto(file, foto);
				} catch (Exception e) {
					attr.addFlashAttribute("falha", "Erro ao cadastrar foto!");
				}
			}
			if(secretaria.getFoto().hasId()) {
				Foto foto = fotoService.buscarFotoId(secretaria.getFoto().getId());
				foto.setFileName(file.getOriginalFilename());
				foto.setPath("/uploads/");
				secretaria.setFoto(foto);
				try {
					fotoService.salvarFoto(file, foto);
				} catch (Exception e) {
					attr.addFlashAttribute("falha", "Erro ao cadastrar foto!");
				}
			}
				
		}
		if(file.isEmpty() && secretaria.hasId()) {
			Foto foto = fotoService.buscarFotoId(secretaria.getFoto().getId());
			secretaria.setFoto(foto);
			try {
				fotoService.salvar(foto);
			} catch (Exception e) {
				attr.addFlashAttribute("falha", "Erro ao cadastrar foto!");
			}
		}
		service.salvar(secretaria);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		attr.addFlashAttribute("secretaria", secretaria);

		return "redirect:/secretarias/dados";

	}

}