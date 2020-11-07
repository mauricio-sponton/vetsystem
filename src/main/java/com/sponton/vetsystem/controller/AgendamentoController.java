package com.sponton.vetsystem.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sponton.vetsystem.domain.Agendamento;
import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Cliente;
import com.sponton.vetsystem.domain.Notificacao;
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.AgendamentoService;
import com.sponton.vetsystem.service.AnimalService;
import com.sponton.vetsystem.service.NotificacaoService;
import com.sponton.vetsystem.service.SecretariaService;
import com.sponton.vetsystem.service.VeterinarioService;

@Controller
@RequestMapping("agenda")
public class AgendamentoController {

	@Autowired
	private AgendamentoService service;

	@Autowired
	private SecretariaService secretariaService;

	@Autowired
	private AnimalService animalService;

	@Autowired
	private VeterinarioService veterinarioService;
	
	@Autowired
	private NotificacaoService notificacaoService;

	@GetMapping("/abrir")
	public String abrirAgenda(Agendamento agendamento, ModelMap model, @AuthenticationPrincipal User user,
			Veterinario veterinario, Secretaria secretaria) {
		return "agendamento/agenda";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid Agendamento agendamento, BindingResult result, RedirectAttributes attr, ModelMap model,
			@AuthenticationPrincipal User user) {
		if (result.hasErrors()) {
			model.addAttribute("erro", "por favor preencha os campos");
			return "/agendamento/agenda";
		}
		if (agendamento.getInicio().isAfter(agendamento.getFim())
				|| agendamento.getInicio().isEqual(agendamento.getFim())) {
			model.addAttribute("erro", "A data de ínicio não pode ser igual ou ultrapassar a data de término");
			return "/agendamento/agenda";
		}

		try {
			if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.SECRETARIA.getDesc()))) {
				Secretaria secretaria = secretariaService.buscarPorEmail(user.getUsername());
				String vet = agendamento.getVeterinario().getNome();
				Veterinario veterinario = veterinarioService.buscarPorTitulos(new String[] { vet }).stream().findFirst()
						.get();
				if (!agendamento.getAnimal().getNome().isEmpty()) {
					String titulo = agendamento.getAnimal().getNome();
					Animal animal = animalService.buscarPorTitulos(new String[] { titulo }).stream().findFirst().get();
					agendamento.setSem_cadastro("");
					agendamento.setAnimal(animal);
				} else {
					agendamento.setAnimal(null);
				}
				if (agendamento.hasNotId()) {
					agendamento.setVeterinario(veterinario);
					agendamento.setSecretaria(secretaria);
					service.salvar(agendamento);
					attr.addFlashAttribute("sucesso", "Agendamento cadastrado com sucesso");
				} else {
					agendamento.setVeterinario(veterinario);
					agendamento.setSecretaria(secretaria);
					service.salvar(agendamento);
					attr.addFlashAttribute("sucesso", "Dados alterados com sucesso");
				}
			}

		} catch (DataIntegrityViolationException ex) {
			attr.addFlashAttribute("falha", "Cadastro não realizado");
		}

		return "redirect:/agenda/abrir";
	}

	@GetMapping("/todos")
	public @ResponseBody ArrayList<Map<String, Object>> getAgendamentos(Secretaria secretaria, Veterinario veterinario) {
		List<Agendamento> events = null;
		if(veterinario.hasId()) {
			events = service.buscarVeterinarioPorId(veterinario.getId());
		}
		if(secretaria.hasId()) {
			events  = service.buscarTodos(); 
		}
		
		ArrayList<Map<String, Object>> allEvents = new ArrayList<Map<String, Object>>();

		for (Agendamento agendamento : events) {
			Map<String, Object> tudo = new LinkedHashMap<>();
			Map<String, Object> extend = new LinkedHashMap<>();
			tudo.put("id", agendamento.getId());
			tudo.put("title", agendamento.getTipo());
			tudo.put("start", agendamento.getInicio());
			tudo.put("end", agendamento.getFim());
			tudo.put("description", agendamento.getDescricao() != null ? agendamento.getDescricao() : "");
			tudo.put("backgroundColor", agendamento.getColor());
			extend.put("veterinario", agendamento.getVeterinario().getNome());
			extend.put("secretaria",
					agendamento.getSecretaria().getNome() != null ? agendamento.getSecretaria().getNome() : "");
			if (agendamento.getAnimal() != null) {
				extend.put("paciente",
						agendamento.getAnimal().getNome() != null ? agendamento.getAnimal().getNome() : "");
			} else {
				extend.put("pacienteNaoCadastrado",
						agendamento.getSem_cadastro() != null ? agendamento.getSem_cadastro() : "");
			}

			tudo.put("extendedProps", extend != null ? extend : "");
			// tudo.putAll(remapear);

			// System.out.println(tudo);
			allEvents.add(tudo);

		}

		// String json = new Gson().toJson(acumulador);

		return allEvents;
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr, @AuthenticationPrincipal User user) {
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.VETERINARIO.getDesc()))) {
			Agendamento agendamento = service.buscarPorId(id);
			List<Secretaria> secretarias = secretariaService.buscarTodos();
			Notificacao notificacao = new Notificacao();
			//List<Notificacao> notificacoes = new ArrayList<>();
			notificacao.setTitulo("Cancelamento de consulta");
			LocalTime horas = agendamento.getInicio().toLocalTime();
			String paciente = null;
			if(agendamento.getAnimal() != null) {
				paciente = "Paciente: " + agendamento.getAnimal().getNome();
			}else {
				paciente = "Paciente: " + agendamento.getSem_cadastro();
			}
			notificacao.setDescricao("Consulta agendada para o dia " + agendamento.getInicio().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + " às " + horas  + ";" + 
									 paciente + ";" + 
									 "Agendada por: " + agendamento.getSecretaria().getNome() + ";" + 
									 "Cancelada por: " + agendamento.getVeterinario().getNome());
			
			//notificacoes.add(notificacao);
			
			//notificacao.setSecretarias(secretarias);
			for(Secretaria s : secretarias) {
				s.getNotificacoes().add(notificacao);
				//s.setNotificacoes(notificacoes);
			}
			//for(Notificacao n : notificacoes) {
			//	n.setSecretarias(secretarias);
			//}
			
			
			service.remover(id);
			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
			return "redirect:/agenda/abrir";
		}
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/agenda/abrir";
	}
	
	
}
