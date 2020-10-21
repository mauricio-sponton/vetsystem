package com.sponton.vetsystem.controller;

import java.util.Arrays;
import java.util.List;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Perfil;
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.domain.Usuario;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.SecretariaService;
import com.sponton.vetsystem.service.UsuarioService;
import com.sponton.vetsystem.service.VeterinarioService;

@Controller
@RequestMapping("u")
public class UsuarioController {

	@Autowired
	private UsuarioService service;

	@Autowired
	private VeterinarioService veterinarioService;

	@Autowired
	private SecretariaService secretariaService;

	// abrir cadastro de usuarios (vet/admin/secretaria)
	@GetMapping("/novo/cadastro/usuario")
	public String cadastroPorAdminParaAdminVetSecretaria(Usuario usuario) {

		return "usuario/cadastro";
	}

	@GetMapping("/listar")
	public String listarUsuarios(Usuario usuario) {
		return "usuario/lista";
	}

	// listar na datatable
	@GetMapping("/datatables/server/usuarios")
	public ResponseEntity<?> listarUsuariosDatatables(HttpServletRequest request) {
		return ResponseEntity.ok(service.buscarTodos(request));
	}

	// salvar cadastro de usuario por administrador
	@PostMapping("/cadastro/salvar")
	public String salvarUsuarios(@Valid Usuario usuario, BindingResult result, RedirectAttributes attr, ModelMap model) {
		if (result.hasErrors()) {
			model.addAttribute("erro", "Por favor preencha os campos");
			return "usuario/lista";
		}
		List<Perfil> perfis = usuario.getPerfis();
		if (perfis.size() > 2 || perfis.containsAll(Arrays.asList(new Perfil(2L), new Perfil(3L)))) {
			attr.addFlashAttribute("falha",
					"Não é possível cadastrar um usuário que seja veterinário(a) e secretária(o).");
			attr.addFlashAttribute("usuario", usuario);
		} else {
			try {
				service.salvarUsuario(usuario);
				attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
			} catch (DataIntegrityViolationException ex) {
				attr.addFlashAttribute("falha", "Cadastro não realizado pois o email já existe");
			}
		}

		return "redirect:/u/listar";
	}

	@GetMapping("/editar/credenciais/usuario/{id}")
	public ModelAndView preEditarCredenciais(@PathVariable("id") Long id) {
		return new ModelAndView("usuario/lista", "usuario", service.buscarPorId(id));
	}

	@GetMapping("/editar/senha")
	public String abrirEditarSenha() {
		return "usuario/editar-senha";
	}

	@PostMapping("/confirmar/senha")
	public String editarSenha(@RequestParam("senha1") String s1, @RequestParam("senha2") String s2,
			@RequestParam("senha3") String s3, @AuthenticationPrincipal User user, RedirectAttributes attr) {
		if (!s1.equals(s2)) {
			attr.addFlashAttribute("falha", "Senhas não conferem, tente novamente");
			return "redirect:/u/editar/senha";
		}
		Usuario u = service.buscarPorEmail(user.getUsername());
		if (!UsuarioService.isSenhaCorreta(s3, u.getSenha())) {
			attr.addFlashAttribute("falha", "Senha atual não confere, tente novamente");
			return "redirect:/u/editar/senha";
		}
		service.alterarSenha(u, s1);
		attr.addFlashAttribute("sucesso", "Senha alterada com sucesso");
		return "redirect:/u/editar/senha";
	}

	@GetMapping("/visualizar/dados/usuario/{id}/perfis/{perfis}")
	public ModelAndView visualizarDadosPessoais(@PathVariable("id") Long usuarioId,
			@PathVariable("perfis") Long[] perfisId) {
		Usuario us = service.buscarPorIdEPerfis(usuarioId, perfisId);

		if (us.getPerfis().contains(new Perfil(PerfilTipo.ADMIN.getCod()))
				&& !us.getPerfis().contains(new Perfil(PerfilTipo.VETERINARIO.getCod()))
				&& !us.getPerfis().contains(new Perfil(PerfilTipo.SECRETARIA.getCod()))) {

			return new ModelAndView("usuario/visualizar", "usuario", us);
		} else if (us.getPerfis().contains(new Perfil(PerfilTipo.VETERINARIO.getCod()))) {
			Veterinario veterinario = veterinarioService.buscarPorUsuarioId(usuarioId);
			if (veterinario.hasNotId()) {
				ModelAndView model = new ModelAndView("error");
				model.addObject("status", 403);
				model.addObject("error", "Página não encontrada");
				model.addObject("message", "Os dados do veterinário ainda não foram cadastrados");
				return model;
			} else {
				return new ModelAndView("veterinario/visualizar", "veterinario", veterinario);
			}

		} else if (us.getPerfis().contains(new Perfil(PerfilTipo.SECRETARIA.getCod()))) {
			Secretaria secretaria = secretariaService.buscarPorUsuarioId(usuarioId);
			if (secretaria.hasNotId()) {
				ModelAndView model = new ModelAndView("error");
				model.addObject("status", 404);
				model.addObject("error", "Página não encontrada");
				model.addObject("message", "Os dados da secretária ainda não foram cadastrados");
			} else {
				return new ModelAndView("secretaria/visualizar", "secretaria", secretaria);
			}
		}

		return new ModelAndView("redirect:/u/lista");
	}
	
}
