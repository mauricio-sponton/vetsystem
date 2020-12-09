package com.sponton.vetsystem.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Aplicacao;
import com.sponton.vetsystem.domain.Consulta;
import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.domain.HistoricoAnimal;
import com.sponton.vetsystem.domain.Internacao;
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.AnimalService;
import com.sponton.vetsystem.service.ConsultaService;
import com.sponton.vetsystem.service.EspecieService;
import com.sponton.vetsystem.service.HistoricoAnimalService;
import com.sponton.vetsystem.service.VacinaService;
import com.sponton.vetsystem.service.VeterinarioService;

@Controller
@RequestMapping("consultas")
public class ConsultaController {

	@Autowired
	private ConsultaService service;

	@Autowired
	private AnimalService animalService;

	@Autowired
	private VeterinarioService veterinarioService;

	@Autowired
	private HistoricoAnimalService historicoAnimalService;
	
	@Autowired
	private VacinaService vacinaService;
	
	@Autowired
	private EspecieService especieService;


	@PreAuthorize("hasAuthority('VETERINARIO')")
	@PostMapping("/salvar")
	public String salvarConsulta(@Valid Consulta consulta, BindingResult result, RedirectAttributes attr,
			@AuthenticationPrincipal User user, ModelMap model) {
		if (result.hasErrors() || consulta.getAnimal().getNome().isEmpty()) {
			model.addAttribute("erro", "Por favor preencha os dados");
			return "consulta/lista";
		}

		String titulo = consulta.getAnimal().getNome();
		Animal animal = animalService.buscarPorTitulos(new String[] { titulo }).stream().findFirst().get();

		HistoricoAnimal historico = new HistoricoAnimal();
		LocalDate data = LocalDate.now();
		LocalTime hora = LocalTime.now();
		StringBuilder mud = new StringBuilder();

		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.VETERINARIO.getDesc()))) {
			Veterinario veterinario = veterinarioService.buscarPorEmail(user.getUsername());
			if (veterinario.hasNotId()) {
				attr.addFlashAttribute("falha",
						"Por favor preencha seus dados pessoais para continuar usando o sistema");
				return "redirect:/veterinarios/dados";
			}
			if (consulta.hasNotId()) {
				historico.setDescricao("Uma nova consulta foi registrada");
				historico.setTipo("Nova consulta");
				historico.setUsuario(veterinario.getNome() + " (veterinario)");
				historico.setData(data);
				historico.setHora(hora);

			}
			if (consulta.hasId()) {
				Consulta status = service.buscarPorId(consulta.getId());
				if (!status.getData().equals(consulta.getData())) {
					mud.append("A data da consulta foi mudada de "
							+ status.getData().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + " para "
							+ consulta.getData().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + "."
							+ ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getDescricao().contains(consulta.getDescricao())) {
					mud.append("As anotações do paciente foram alteradas" + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getHora().equals(consulta.getHora())) {
					mud.append("A hora de início da consulta foi mudada de " + status.getHora() + " para "
							+ consulta.getHora() + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getPrescricao().contains(consulta.getPrescricao())) {
					mud.append("A prescrição do paciente foi alterada " + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (status.getTermino() != consulta.getTermino()) {
					if(status.getTermino() == null) {
						mud.append("A hora de término da consulta foi mudada de -- para "
								+ consulta.getTermino() + "." + ";");
					}else {
						mud.append("A hora de término da consulta foi mudada de " + status.getTermino() + " para "
								+ consulta.getTermino() + "." + ";");
					}
					
					historico.setDescricao(mud.toString());
				}
				if (status.getTemperatura().doubleValue() != consulta.getTemperatura().doubleValue()) {
					mud.append("A temperatura do paciente foi mudada de " + status.getTemperatura() + " para "
							+ consulta.getTemperatura() + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (status.getPeso().doubleValue() != consulta.getPeso().doubleValue()) {
					mud.append("O peso do paciente foi mudado de " + status.getPeso() + " para " + consulta.getPeso().doubleValue()
							+ "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getAnimal().getNome().contains(consulta.getAnimal().getNome())) {
					mud.append("O nome do paciente foi alterado na consulta de " + status.getAnimal().getNome()
							+ " para " + consulta.getAnimal().getNome() + "." + ";");
					historico.setDescricao(mud.toString());
				}

				if(historico.getDescricao() != null) {
					historico.setTipo("Alteração de consulta");
					historico.setUsuario(veterinario.getNome() + " (veterinario)");
					historico.setData(data);
					historico.setHora(hora);
				}
			
			}
			consulta.setAnimal(animal);
			consulta.setVeterinario(veterinario);
			service.salvarConsulta(consulta);
			if(historico.getDescricao() != null) {
				historico.setAnimal(animal);
				historicoAnimalService.salvar(historico);
			}
			

			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		}
		return "redirect:/consultas/listar";
	}

	@GetMapping("/listar")
	public String listarConsultas(Consulta consulta) {
		return "consulta/lista";
	}

	@GetMapping("/datatables/server")
	public ResponseEntity<?> listarAnimaisDatatables(HttpServletRequest request, @AuthenticationPrincipal User user) {
		Veterinario veterinario = veterinarioService.buscarPorEmail(user.getUsername());
		return ResponseEntity.ok(service.buscarTodos(request, veterinario.getId()));
	}
	@GetMapping("/datatables/server/{idAnimal}")
	public ResponseEntity<?> listarConsultasDatatables(HttpServletRequest request,
			@PathVariable("idAnimal") Long idAnimal) {

		return ResponseEntity.ok(service.buscarConsultasPorAnimal(request, idAnimal));
	}

	@PreAuthorize("hasAuthority('VETERINARIO')")
	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("consulta", service.buscarPorId(id));
		return "consulta/lista";
	}
	@PreAuthorize("hasAuthority('VETERINARIO')")
	@GetMapping("/editar/{id}/paciente/{idAnimal}")
	public String preEditarConsultaPorAnimal(@PathVariable("id") Long id, @PathVariable("idAnimal") Long idAnimal, ModelMap model, Internacao internacao, Aplicacao aplicacao) {
		Animal animal = animalService.buscarPorId(idAnimal);
		Especie especie = especieService.buscarEspeciePorAnimal(animal.getEspecie().getNome());
		model.addAttribute("consulta", service.buscarPorId(id));
		model.addAttribute("aplicacao", new Aplicacao());
		model.addAttribute("internacao", new Internacao());
		model.addAttribute("animal", animalService.buscarPorId(idAnimal));
		model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorAnimal(idAnimal));
		model.addAttribute("vacinas", vacinaService.buscarTodasVacinasPorEspecie(especie.getNome()));
		return "animal/visualizar";
	}

	@PreAuthorize("hasAuthority('VETERINARIO')")
	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/consultas/listar";
	}
	@PreAuthorize("hasAuthority('VETERINARIO')")
	@GetMapping("/excluir/{id}/paciente/{idAnimal}")
	public String excluirPorAnimal(@PathVariable("idAnimal") Long idAnimal, @PathVariable("id") Long id, RedirectAttributes attr) {
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/pacientes/visualizar/{idAnimal}";
	}

	@GetMapping("/visualizar/{id}")
	public String visualizar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("consulta", service.buscarPorId(id));
		return "consulta/visualizar";
	}

	@PreAuthorize("hasAuthority('VETERINARIO')")
	@PostMapping("/salvar/paciente/{id}")
	public String salvarConsultaPorAnimal(@Valid Consulta consulta, BindingResult result, RedirectAttributes attr,
			@AuthenticationPrincipal User user, @PathVariable("id") Long id, Internacao internacao, Aplicacao aplicacao, ModelMap model) {
		if (result.hasErrors()) {
			Animal animal = animalService.buscarPorId(id);
			Especie especie = especieService.buscarEspeciePorAnimal(animal.getEspecie().getNome());
			model.addAttribute("erro", "Por favor preencha os dados");
			model.addAttribute("animal", animalService.buscarPorId(id));
			model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorAnimal(id));
			model.addAttribute("vacinas", vacinaService.buscarTodasVacinasPorEspecie(especie.getNome()));
			return "animal/visualizar";
		}
		Animal animal = animalService.buscarPorId(id);
		HistoricoAnimal historico = new HistoricoAnimal();
		LocalDate data = LocalDate.now();
		LocalTime hora = LocalTime.now();
		StringBuilder mud = new StringBuilder();

		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.VETERINARIO.getDesc()))) {
			Veterinario veterinario = veterinarioService.buscarPorEmail(user.getUsername());
			if (veterinario.hasNotId()) {
				attr.addFlashAttribute("falha",
						"Por favor preencha seus dados pessoais para continuar usando o sistema");
				return "redirect:/veterinarios/dados";
			}
			if (consulta.hasNotId()) {
				historico.setDescricao("Uma nova consulta foi registrada");
				historico.setTipo("Nova consulta");
				historico.setUsuario(veterinario.getNome() + " (veterinario)");
				historico.setData(data);
				historico.setHora(hora);

			}
			if (consulta.hasId()) {
				Consulta status = service.buscarPorId(consulta.getId());
				if (!status.getData().equals(consulta.getData())) {
					mud.append("A data da consulta foi mudada de "
							+ status.getData().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + " para "
							+ consulta.getData().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + "."
							+ ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getDescricao().contains(consulta.getDescricao())) {
					mud.append("As anotações do paciente foram alteradas" + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getHora().equals(consulta.getHora())) {
					mud.append("A hora de início da consulta foi mudada de " + status.getHora() + " para "
							+ consulta.getHora() + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getPrescricao().contains(consulta.getPrescricao())) {
					mud.append("A prescrição do paciente foi alterada " + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (status.getTermino() != consulta.getTermino()) {
					if(status.getTermino() == null) {
						mud.append("A hora de término da consulta foi mudada de -- para "
								+ consulta.getTermino() + "." + ";");
					}else {
						mud.append("A hora de término da consulta foi mudada de " + status.getTermino() + " para "
								+ consulta.getTermino() + "." + ";");
					}
					
					historico.setDescricao(mud.toString());
				}
				if (status.getTemperatura().doubleValue() != consulta.getTemperatura().doubleValue()) {
					mud.append("A temperatura do paciente foi mudada de " + status.getTemperatura() + " para "
							+ consulta.getTemperatura() + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (status.getPeso().doubleValue() != consulta.getPeso().doubleValue()) {
					mud.append("O peso do paciente foi mudado de " + status.getPeso() + " para " + consulta.getPeso().doubleValue()
							+ "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getAnimal().getNome().contains(consulta.getAnimal().getNome())) {
					mud.append("O nome do paciente foi alterado na consulta de " + status.getAnimal().getNome()
							+ " para " + consulta.getAnimal().getNome() + "." + ";");
					historico.setDescricao(mud.toString());
				}

				if(historico.getDescricao() != null) {
					historico.setTipo("Alteração de consulta");
					historico.setUsuario(veterinario.getNome() + " (veterinario)");
					historico.setData(data);
					historico.setHora(hora);
				}
			
			}
			consulta.setAnimal(animal);
			consulta.setVeterinario(veterinario);
			service.salvarConsulta(consulta);
			if(historico.getDescricao() != null) {
				historico.setAnimal(animal);
				historicoAnimalService.salvar(historico);
			}
			

			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		}
		return "redirect:/pacientes/visualizar/{id}";
	}

}
