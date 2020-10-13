package com.sponton.vetsystem.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Aplicacao;
import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.domain.Internacao;
import com.sponton.vetsystem.domain.Vacina;
import com.sponton.vetsystem.service.AnimalService;
import com.sponton.vetsystem.service.AplicacaoService;
import com.sponton.vetsystem.service.ConsultaService;
import com.sponton.vetsystem.service.HistoricoAnimalService;
import com.sponton.vetsystem.service.InternacaoService;
import com.sponton.vetsystem.service.VacinaService;

@Controller
@RequestMapping("aplicacoes")
public class AplicacaoController {

	@Autowired
	private AplicacaoService service;

	@Autowired
	private AnimalService animalService;

	@Autowired
	private VacinaService vacinaService;
	
	@Autowired
	private HistoricoAnimalService historicoAnimalService;

	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private InternacaoService internacaoService;

	@GetMapping("/cadastrar/{id}")
	public String novoAnimal(Aplicacao aplicacao, @PathVariable("id") Long id, ModelMap model, Internacao internacao) {
		model.addAttribute("animal", animalService.buscarPorId(id));
		model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorAnimal(id));
		model.addAttribute("consulta", consultaService.buscarConsultaPorAnimal(id));
		return "animal/visualizar";
	}

	@PostMapping("/salvar/{id}")
	public String salvarAplicacao(@Valid Aplicacao aplicacao, BindingResult result, RedirectAttributes attr,
			ModelMap model, @PathVariable("id") Long id) {
		Animal animal = animalService.buscarPorId(id);
		
		if (result.hasErrors()) {
			model.addAttribute("animal", animalService.buscarPorId(id));
			model.addAttribute("erro", "Por favor preencha os dados");
			return "aplicacao/cadastro";
		}

		try {
			aplicacao.setDoses(1);	
			if(aplicacao.hasId() && aplicacao.getDoses() >= 1) {
				List<Aplicacao> aplic = service.buscarPorDesc(aplicacao.getVacina().getDescricao(), id);
				//if(aplicacao.getVacina().getDescricao() == aplic.getVacina().getDescricao()) {
				if(aplic.size() < aplicacao.getVacina().getDoses()) {
					aplicacao.setDoses(aplic.size() + 1);	
					aplicacao.setProximaAplicacao(aplicacao.getDataAplicacao().plusDays(aplicacao.getVacina().getIntervalo()));
	
				}
				if(aplic.size() >= aplicacao.getVacina().getDoses()) {
					aplicacao.setDoses(0);	
					aplicacao.setProximaAplicacao(aplicacao.getDataAplicacao().plusDays(365));
				}
				
			}
			
			
			aplicacao.setAnimal(animal);
			service.salvarAplicacao(aplicacao);
			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");

		} catch (DataIntegrityViolationException ex) {
			attr.addFlashAttribute("falha", "Cadastro não realizado pois essa aplicação já está cadastrada no sistema");
		}

		return "redirect:/vacinas/listar";
	}

	@GetMapping("/datatables/server/{idAnimal}")
	public ResponseEntity<?> listarAplicacoesDatatables(HttpServletRequest request,
			@PathVariable("idAnimal") Long idAnimal) {

		return ResponseEntity.ok(service.buscarTodos(request, idAnimal));
	}

	@GetMapping("/editar/{id}/paciente/{idAnimal}")
	public String preEditar(@PathVariable("id") Long id, @PathVariable("idAnimal") Long idAnimal, ModelMap model,
			Aplicacao aplicacao, Internacao internacao) {
		model.addAttribute("animal", animalService.buscarPorId(idAnimal));
		model.addAttribute("aplicacao", service.buscarPorId(id));
		model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorAnimal(idAnimal));
		model.addAttribute("consulta", consultaService.buscarConsultaPorAnimal(idAnimal));
		
		return "animal/visualizar";
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/pacientes/listar";
	}

	@ModelAttribute("vacinas")
	public List<Vacina> listaDeEspecies() {
		return vacinaService.buscarTodasVacinas();
	}
}
