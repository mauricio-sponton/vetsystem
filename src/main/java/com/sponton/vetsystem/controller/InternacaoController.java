package com.sponton.vetsystem.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Foto;
import com.sponton.vetsystem.domain.HistoricoAnimal;
import com.sponton.vetsystem.domain.Internacao;
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.AnimalService;
import com.sponton.vetsystem.service.FotoService;
import com.sponton.vetsystem.service.HistoricoAnimalService;
import com.sponton.vetsystem.service.InternacaoService;
import com.sponton.vetsystem.service.VeterinarioService;

@Controller
@RequestMapping("internacoes")
public class InternacaoController {

	@Autowired
	private InternacaoService service;

	@Autowired
	private VeterinarioService veterinarioService;

	@Autowired
	private HistoricoAnimalService historicoAnimalService;

	@Autowired
	private AnimalService animalService;

	@Autowired
	private FotoService fotoService;

	@GetMapping("/cadastrar")
	public String novaInternacao(Internacao internacao) {
		return "internacao/cadastro";
	}

	@PostMapping("/salvar")
	public String salvarInternacao(@Valid Internacao internacao, BindingResult result, RedirectAttributes attr,
			@AuthenticationPrincipal User user, @RequestParam("status") String status,
			@RequestParam("files") MultipartFile[] files) {
		if (result.hasErrors()) {
			return "internacao/cadastro";
		}
		String titulo = internacao.getAnimal().getNome();
		Animal animal = animalService.buscarPorTitulos(new String[] { titulo }).stream().findFirst().get();
		if (internacao.hasNotId() && animal.getStatus().equals("Internado")) {
			attr.addFlashAttribute("falha", "O paciente já possui uma internação em andamento!");
			return "redirect:/internacoes/cadastrar";
		}
		if (internacao.hasNotId() && status.equals("Encerrada")) {
			attr.addFlashAttribute("falha", "O paciente não possui nenhuma internação em andamento!");
			return "redirect:/internacoes/cadastrar";
		}
		if (internacao.hasId() && status.equals("Encerrada")) {
			animal.setStatus("Normal");
		}

		
		if (files.length > 0) {	
				for (int i = 0; i < files.length; i++) {
					Foto foto = new Foto();
					foto.setFileName(files[i].getOriginalFilename());
					foto.setPath("/uploads/");
					foto.setInternacao(internacao);
					try {
						fotoService.salvarFotos(files, foto);
					} catch (Exception e) {
					
					}
				}		
		}	
		HistoricoAnimal historico = new HistoricoAnimal();
		LocalDate data = LocalDate.now();
		LocalTime hora = LocalTime.now();

		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.VETERINARIO.getDesc()))) {
			Veterinario veterinario = veterinarioService.buscarPorEmail(user.getUsername());
			if (veterinario.hasNotId()) {
				attr.addFlashAttribute("falha",
						"Por favor preencha seus dados pessoais para continuar usando o sistema");
				return "redirect:/veterinarios/dados";
			}
			if (internacao.hasNotId()) {
				historico.setDescricao("O paciente " + internacao.getAnimal().getNome() + "foi internado.");
				historico.setTipo("Nova internação");
				historico.setUsuario(veterinario.getNome() + " (veterinario)");
				historico.setData(data);
				historico.setHora(hora);
				animal.setStatus("Internado");

			}
			if (internacao.hasId()) {
				historico.setDescricao("As informações da internação foram alteradas");
				historico.setTipo("Alteração de internação");
				historico.setUsuario(veterinario.getNome() + " (veterinario)");
				historico.setData(data);
				historico.setHora(hora);
			}

		}
		
		internacao.setAnimal(animal);
		service.salvarInternacao(internacao);
		historico.setAnimal(animal);
		historicoAnimalService.salvar(historico);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		return "redirect:/internacoes/cadastrar";
	}

	@GetMapping("/listar")
	public String listarInternacoes(ModelMap model) {
		model.addAttribute("internacaoAtiva", service.buscarInternacaoAtiva());
		model.addAttribute("internacaoEncerrada", service.buscarInternacaoEncerrada());
		return "internacao/lista";
	}

	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("internacao", service.buscarPorId(id));
		return "internacao/cadastro";
	}

	@GetMapping("/visualizar/{id}")
	public String visualizar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("internacao", service.buscarPorId(id));
		model.addAttribute("fotos", fotoService.buscarFotosPorId(id));
		return "internacao/visualizar";
	}
}
