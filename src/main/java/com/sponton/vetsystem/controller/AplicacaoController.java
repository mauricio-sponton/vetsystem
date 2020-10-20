package com.sponton.vetsystem.controller;

import java.util.List;
import java.util.stream.Stream;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Aplicacao;
import com.sponton.vetsystem.domain.Consulta;
import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.domain.Internacao;
import com.sponton.vetsystem.domain.Vacina;
import com.sponton.vetsystem.service.AnimalService;
import com.sponton.vetsystem.service.AplicacaoService;
import com.sponton.vetsystem.service.ConsultaService;
import com.sponton.vetsystem.service.EspecieService;
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
	
	@Autowired
	private EspecieService especieService;

	@GetMapping("/cadastrar/{idAnimal}")
	public String novoAnimal(Aplicacao aplicacao, @PathVariable("idAnimal") Long idAnimal, ModelMap model, Internacao internacao, Consulta consulta) {
		model.addAttribute("animal", animalService.buscarPorId(idAnimal));
		//model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorAnimal(id));
		//model.addAttribute("consulta", consultaService.buscarConsultaPorAnimal(id));
		return "aplicacao/cadastro";
	}

	@PostMapping("/salvar/{idAnimal}")
	public String salvarAplicacao(@Valid Aplicacao aplicacao, BindingResult result, RedirectAttributes attr,
			ModelMap model, @PathVariable("idAnimal") Long idAnimal, Internacao internacao, Consulta consulta) {
		Animal animal = animalService.buscarPorId(idAnimal);
		
		if (result.hasErrors()) {
			Especie especie = especieService.buscarEspeciePorAnimal(animal.getEspecie().getNome());
			model.addAttribute("animal", animalService.buscarPorId(idAnimal));
			model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorAnimal(idAnimal));
			//model.addAttribute("consulta", consultaService.buscarConsultaPorAnimal(idAnimal));
			model.addAttribute("vacinas", vacinaService.buscarTodasVacinasPorEspecie(especie.getNome()));
			model.addAttribute("erro", "Por favor preencha os dados");
			return "animal/visualizar";
		}

		try {
			
				aplicacao.setDoses(1);	
			
		
			if(aplicacao.getDoses() >= 1) {
				
				List<Aplicacao> aplic = service.buscarPorDesc(aplicacao.getVacina().getDescricao(), idAnimal);			
				
				if(aplic.size() < aplicacao.getVacina().getDoses()) {
					if(aplicacao.hasId()) {
						Aplicacao app = service.buscarPorId(aplicacao.getId());
						
						if(aplicacao.getVacina().getDescricao() != app.getVacina().getDescricao()) {
							List<Aplicacao> ultimoValor = service.buscarPorDesc(aplicacao.getVacina().getDescricao(), idAnimal);
							if(ultimoValor.size() >1) {
								int count = ultimoValor.size();
								//Stream<Aplicacao> stream = ultimoValor.stream();
								//Aplicacao valor = stream.skip(count - 1).findFirst().get();
								aplicacao.setDoses(count + 1);
							}else if(ultimoValor.size() ==1){
								aplicacao.setDoses(1);	
							}
							
							
							
						}if(aplicacao.getVacina().getDescricao() == app.getVacina().getDescricao()) {
							aplicacao.setDoses(app.getDoses());
							
						}
						
					}if(aplicacao.hasNotId()){
						aplicacao.setDoses(aplic.size() + 1);	
					}
					
					aplicacao.setProximaAplicacao(aplicacao.getDataAplicacao().plusDays(aplicacao.getVacina().getIntervalo()));
	
				}
				else if(aplicacao.hasNotId() && aplic.size() >= aplicacao.getVacina().getDoses()) {
					aplicacao.setDoses(0);	
					aplicacao.setProximaAplicacao(aplicacao.getDataAplicacao().plusDays(365));
				}
				else if(aplicacao.hasId() && aplic.size() >= aplicacao.getVacina().getDoses()) {
					Aplicacao app = service.buscarPorId(aplicacao.getId());
					aplicacao.setDoses(app.getDoses());
					aplicacao.setProximaAplicacao(aplicacao.getDataAplicacao().plusDays(aplicacao.getVacina().getIntervalo()));
				}
				
				
			}
			
			
			aplicacao.setAnimal(animal);
			service.salvarAplicacao(aplicacao);
			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");

		} catch (DataIntegrityViolationException ex) {
			attr.addFlashAttribute("falha", "Cadastro não realizado pois essa aplicação já está cadastrada no sistema");
		}

		return "redirect:/pacientes/visualizar/{idAnimal}";
	}

	@GetMapping("/datatables/server/{idAnimal}")
	public ResponseEntity<?> listarAplicacoesDatatables(HttpServletRequest request,
			@PathVariable("idAnimal") Long idAnimal) {

		return ResponseEntity.ok(service.buscarTodos(request, idAnimal));
	}

	@GetMapping("/editar/{id}/paciente/{idAnimal}")
	public String preEditar(@PathVariable("id") Long id, @PathVariable("idAnimal") Long idAnimal, ModelMap model,
			Aplicacao aplicacao, Internacao internacao, Consulta consulta) {
		Animal animal = animalService.buscarPorId(idAnimal);
		Especie especie = especieService.buscarEspeciePorAnimal(animal.getEspecie().getNome());
		model.addAttribute("animal", animalService.buscarPorId(idAnimal));
		model.addAttribute("aplicacao", service.buscarPorId(id));
		model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorAnimal(idAnimal));
		//model.addAttribute("consulta", consultaService.buscarConsultaPorAnimal(idAnimal));
		model.addAttribute("vacinas", vacinaService.buscarTodasVacinasPorEspecie(especie.getNome()));
		
		return "animal/visualizar";
	}

	@GetMapping("/excluir/{id}/paciente/{idAnimal}")
	public String excluir(@PathVariable("id") Long id, @PathVariable("idAnimal") Long idAnimal, RedirectAttributes attr) {
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/pacientes/visualizar/{idAnimal}";
	}

	/*
	@ModelAttribute("vacinas")
	public List<Vacina> listaDeEspecies() {
		return vacinaService.buscarTodasVacinas();
	}
	*/
}
