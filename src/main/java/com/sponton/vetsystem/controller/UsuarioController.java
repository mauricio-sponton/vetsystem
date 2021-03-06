package com.sponton.vetsystem.controller;

import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
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

import com.sponton.vetsystem.domain.CargaHoraria;
import com.sponton.vetsystem.domain.Notificacao;
import com.sponton.vetsystem.domain.Perfil;
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.domain.Usuario;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.CargaHorariaService;
import com.sponton.vetsystem.service.NotificacaoService;
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

	@Autowired
	private CargaHorariaService cargaHorariaService;

	@Autowired
	private NotificacaoService notificacaoService;

	// abrir cadastro de usuarios (vet/admin/secretaria)
	@GetMapping("/novo/cadastro/usuario")
	public String cadastroPorAdminParaAdminVetSecretaria(Usuario usuario) {

		return "usuario/cadastro";
	}
	@GetMapping("/ajuda")
	public String abrirPaginaAjuda(Usuario usuario) {

		return "usuario/ajuda";
	}

	@GetMapping("/listar")
	public String listarUsuarios(Usuario usuario, @AuthenticationPrincipal User user) {
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.ADMIN.getDesc())) || (user
				.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.ADMIN.getDesc()))
				&& user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.VETERINARIO.getDesc()))
				|| (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.ADMIN.getDesc())) && user
						.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.SECRETARIA.getDesc()))))) {
			return "usuario/lista";
		} else {
			return "usuario/funcionarios";
		}

	}

	// listar na datatable
	@GetMapping("/datatables/server/usuarios")
	public ResponseEntity<?> listarUsuariosDatatables(HttpServletRequest request) {
		return ResponseEntity.ok(service.buscarTodos(request));
	}

	@GetMapping("/datatables/server/funcionarios")
	public ResponseEntity<?> listarFuncionariosDatatables(HttpServletRequest request) {
		return ResponseEntity.ok(service.buscarTodosFuncionarios(request));
	}

	// salvar cadastro de usuario por administrador
	@PostMapping("/cadastro/salvar")
	public String salvarUsuarios(@Valid Usuario usuario, BindingResult result, RedirectAttributes attr,
			ModelMap model) {
		if (result.hasErrors()) {
			model.addAttribute("erro", "Por favor preencha os campos");
			return "usuario/lista";
		}
		if (usuario.hasId()) {
			Usuario u2 = service.buscarPorId(usuario.getId());
			if (u2.getPerfis().contains(new Perfil(PerfilTipo.SECRETARIA.getCod()))
					&& usuario.getPerfis().contains(new Perfil(PerfilTipo.VETERINARIO.getCod()))) {
				
				Secretaria secretaria = secretariaService.buscarPorUsuarioId(usuario.getId());
				if(secretaria.hasId()) {
					secretariaService.remover(secretaria.getId());
				}
				
			}
			if (u2.getPerfis().contains(new Perfil(PerfilTipo.VETERINARIO.getCod()))
					&& usuario.getPerfis().contains(new Perfil(PerfilTipo.SECRETARIA.getCod()))) {
				
				Veterinario veterinario = veterinarioService.buscarPorUsuarioId(usuario.getId());
				if(veterinario.hasId()) {
					veterinarioService.remover(veterinario.getId());
				}
				
			}
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
			@PathVariable("perfis") Long[] perfisId, ModelMap modelmap) {
		Usuario us = service.buscarPorIdEPerfis(usuarioId, perfisId);

		if (us.getPerfis().contains(new Perfil(PerfilTipo.ADMIN.getCod()))
				&& !us.getPerfis().contains(new Perfil(PerfilTipo.VETERINARIO.getCod()))
				&& !us.getPerfis().contains(new Perfil(PerfilTipo.SECRETARIA.getCod()))) {
			return new ModelAndView("usuario/lista", "usuario", service.buscarPorId(usuarioId));

		} else if (us.getPerfis().contains(new Perfil(PerfilTipo.VETERINARIO.getCod()))) {
			Veterinario veterinario = veterinarioService.buscarPorUsuarioId(usuarioId);
			if (veterinario.hasNotId()) {
				ModelAndView model = new ModelAndView("error");
				model.addObject("status", 404);
				model.addObject("error", "Página não encontrada");
				model.addObject("message", "Os dados do veterinário ainda não foram cadastrados");
				return model;
			} else {
				List<CargaHoraria> cargasVet = cargaHorariaService.buscarHorarioPorVeterinario(veterinario.getId());
				modelmap.addAttribute("cargasVet", cargasVet);
				modelmap.addAttribute("vet", veterinario);
				return new ModelAndView("veterinario/userview");
			}

		} else if (us.getPerfis().contains(new Perfil(PerfilTipo.SECRETARIA.getCod()))) {
			Secretaria secretaria = secretariaService.buscarPorUsuarioId(usuarioId);
			if (secretaria.hasNotId()) {
				ModelAndView model = new ModelAndView("error");
				model.addObject("status", 404);
				model.addObject("error", "Página não encontrada");
				model.addObject("message", "Os dados da secretária ainda não foram cadastrados");
			} else {
				return new ModelAndView("secretaria/userview", "sec", secretaria);
			}
		}
		return new ModelAndView("redirect:/u/lista");
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr, @AuthenticationPrincipal User user) {
		Usuario usuario = service.buscarPorEmail(user.getUsername());
		Usuario u2 = service.buscarPorId(id);
		if (u2.getPerfis().contains(new Perfil(PerfilTipo.VETERINARIO.getCod()))) {
			Veterinario veterinario = veterinarioService.buscarPorUsuarioId(id);
			veterinarioService.remover(veterinario.getId());

		}
		if (u2.getPerfis().contains(new Perfil(PerfilTipo.SECRETARIA.getCod()))) {
			Secretaria secretaria = secretariaService.buscarPorUsuarioId(id);
			secretariaService.remover(secretaria.getId());

			/*
			 * else if (u2.getPerfis().contains(new Perfil(PerfilTipo.SECRETARIA.getCod()))
			 * && u2.getPerfis().contains(new Perfil(PerfilTipo.VETERINARIO.getCod()))) {
			 * Veterinario veterinario = veterinarioService.buscarPorUsuarioId(id);
			 * Secretaria secretaria = secretariaService.buscarPorUsuarioId(id);
			 * veterinarioService.remover(veterinario.getId());
			 * secretariaService.remover(secretaria.getId());
			 */
		}

		if (usuario.getId() == id) {
			attr.addFlashAttribute("falha", "Você não pode deletar esse usuário");
			return "redirect:/u/listar";
		}
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/u/listar";
	}

	// abre pagina de pedido de redefinicao de senha
	@GetMapping("/p/redefinir/senha")
	public String pedidoRedefinirSenha() {
		return "usuario/pedido-recuperar-senha";
	}

	// form de pedido de recuperar senha
	@GetMapping("/p/recuperar/senha")
	public String redefinirSenha(String email, ModelMap model) throws MessagingException {
		service.pedidoRedefinicaoDeSenha(email);
		model.addAttribute("sucesso",
				"Em instantes você receberá um email para prosseguir com a redefinição de sua senha.");
		model.addAttribute("usuario", new Usuario(email));
		return "usuario/recuperar-senha";
	}

	// salvar nova senha via recuperacao de senha
	@PostMapping("/p/nova/senha")
	public String confirmacaoRedefinicaoSenha(Usuario usuario, ModelMap model) {
		Usuario u = service.buscarPorEmail(usuario.getEmail());
		if (!usuario.getCodigoVerificador().equals(u.getCodigoVerificador())) {
			model.addAttribute("falha", "Código verificador não confere.");
			return "usuario/recuperar-senha";
		}
		u.setCodigoVerificador(null);
		service.alterarSenha(u, usuario.getSenha());
		model.addAttribute("alerta", "sucesso");
		model.addAttribute("titulo", "Senha redefinida!");
		model.addAttribute("texto", "Você já pode logar no sistema.");
		return "login";
	}

	@GetMapping("/notificacoes")
	public String visualizarNotificacoes(ModelMap model, @AuthenticationPrincipal User user) {
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.SECRETARIA.getDesc()))) {
			Secretaria secretaria = secretariaService.buscarPorEmail(user.getUsername());
			if(secretaria.hasId()) {
				List<Notificacao> notificacoes = notificacaoService.buscarNotificacaoPorSecretariaId(secretaria.getId());
				model.addAttribute("notificacoes", notificacoes);
				model.addAttribute("secretaria", secretaria);
			}
			
		}
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.VETERINARIO.getDesc()))) {
			Veterinario veterinario = veterinarioService.buscarPorEmail(user.getUsername());
			if(veterinario.hasId()) {
				List<Notificacao> notificacoes = notificacaoService.buscarNotificacaoPorVeterinarioId(veterinario.getId());
				model.addAttribute("notificacoes", notificacoes);
				model.addAttribute("veterinario", veterinario);
			}
		}
		return "usuario/notificacoes";
	}
}
