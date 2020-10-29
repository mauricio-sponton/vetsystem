package com.sponton.vetsystem.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
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
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.service.AgendamentoService;
import com.sponton.vetsystem.service.SecretariaService;

@Controller
@RequestMapping("agenda")
public class AgendamentoController {

	@Autowired
	private AgendamentoService service;
	
	@Autowired
	private SecretariaService secretariaService;

	@GetMapping("/abrir")
	public String abrirAgenda(Agendamento agendamento, ModelMap model) {
		// model.addAttribute("agendamentos", service.buscarTodos());
		return "agendamento/agenda";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid Agendamento agendamento, BindingResult result, RedirectAttributes attr, ModelMap model,
			@AuthenticationPrincipal User user) {
		if (result.hasErrors()) {
			model.addAttribute("erro", "por favor preencha os campos");
			return "/agendamento/agenda";
		}
		try {
			if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.SECRETARIA.getDesc()))) {
				Secretaria secretaria = secretariaService.buscarPorEmail(user.getUsername());
				if (agendamento.hasNotId()) {
					agendamento.setSecretaria(secretaria);
					service.salvar(agendamento);
					attr.addFlashAttribute("sucesso", "Agendamento cadastrado com sucesso");
				} else {
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
	public @ResponseBody ArrayList<Map<String, Object>> getAgendamentos() {
		List<Agendamento> events = service.buscarTodos();
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
			extend.put("secretaria", agendamento.getSecretaria().getNome()!= null ? agendamento.getSecretaria().getNome(): "");
			tudo.put("extendedProps", extend != null ? extend : "");
			// tudo.putAll(remapear);

			 //System.out.println(tudo);
			allEvents.add(tudo);
			

		}

		// String json = new Gson().toJson(acumulador);

		return allEvents;
	}
	/*
	 * @RequestMapping(value = "/todos.json", method = RequestMethod.GET,
	 * produces="application/json") public @ResponseBody Map<String,Object>
	 * getTudo() { //String jsonMsg = null; // try { List<Agendamento> events =
	 * service.buscarTodos(); Map<String,Object> tudo = new LinkedHashMap<>();
	 * for(Agendamento agendamento : events) { Map<String, Object> remapear = new
	 * HashMap<String, Object>(); tudo.put("id", agendamento.getId());
	 * tudo.put("title", agendamento.getTipo()); tudo.put("start",
	 * agendamento.getInicio()); tudo.put("end", agendamento.getFim());
	 * //tudo.add(remapear); // }
	 * 
	 * // ObjectMapper mapper = new ObjectMapper(); // jsonMsg =
	 * mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tudo);
	 * 
	 * // }catch(IOException ioex) { // System.out.println(ioex.getMessage()); }
	 * return tudo; //return tudo; }
	 */
	/*
	 * @RequestMapping(value = "/todos", method = RequestMethod.GET)
	 * public @ResponseBody String getTudo(HttpServletResponse resp) {
	 * List<Agendamento> events = service.buscarTodos(); Map<String,Object> tudo =
	 * new HashMap<String, Object>(); for(Agendamento agendamento : events) {
	 * tudo.put("id", agendamento.getId()); tudo.put("title",
	 * agendamento.getTipo()); tudo.put("start", agendamento.getInicio());
	 * tudo.put("end", agendamento.getFim());
	 * 
	 * } String json = new Gson().toJson(tudo);
	 * resp.setContentType("application/json"); resp.setCharacterEncoding("UTF-8");
	 * 
	 * return json; }
	 */

}
