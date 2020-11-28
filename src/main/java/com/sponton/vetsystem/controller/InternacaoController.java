package com.sponton.vetsystem.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Aplicacao;
import com.sponton.vetsystem.domain.Consulta;
import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.domain.Foto;
import com.sponton.vetsystem.domain.FotoInternacao;
import com.sponton.vetsystem.domain.HistoricoAnimal;
import com.sponton.vetsystem.domain.Internacao;
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.AnimalService;
import com.sponton.vetsystem.service.AplicacaoService;
import com.sponton.vetsystem.service.ConsultaService;
import com.sponton.vetsystem.service.EspecieService;
import com.sponton.vetsystem.service.FotoInternacaoService;
import com.sponton.vetsystem.service.FotoService;
import com.sponton.vetsystem.service.HistoricoAnimalService;
import com.sponton.vetsystem.service.InternacaoService;
import com.sponton.vetsystem.service.VacinaService;
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

	@Autowired
	private ConsultaService consultaService;

	@Autowired
	private AplicacaoService aplicacaoService;

	@Autowired
	private EspecieService especieService;

	@Autowired
	private VacinaService vacinaService;
	
	@Autowired 
	private FotoInternacaoService fotoInternacaoService;

	@GetMapping("/cadastrar")
	public String novaInternacao(Internacao internacao) {
		return "internacao/cadastro";
	}

	@PostMapping("/salvar")
	public String salvarInternacao(@Valid Internacao internacao, BindingResult result, RedirectAttributes attr,
			@AuthenticationPrincipal User user, @RequestParam("status") String status,
			@RequestParam("files") MultipartFile[] files, ModelMap model) {
		if (result.hasErrors() || internacao.getAnimal().getNome().isEmpty()) {
			model.addAttribute("erro", "Por favor preencha os dados");
			model.addAttribute("internacaoAtiva", service.buscarInternacaoAtiva());
			model.addAttribute("internacaoEncerrada", service.buscarInternacaoEncerrada());
			return "internacao/lista";
		}
		if (internacao.hasId()) {
			Internacao int2 = service.buscarPorId(internacao.getId());
			if (int2.getStatus().equals("Encerrada") && status.equals("Ativa")) {
				attr.addFlashAttribute("falha", "Essa internação já foi encerrada, por favor cadastre uma nova");
				return "redirect:/internacoes/listar";
			}

		}
		String titulo = internacao.getAnimal().getNome();
		Animal animal = animalService.buscarPorTitulos(new String[] { titulo }).stream().findFirst().get();
		if (internacao.hasNotId() && animal.getStatus().equals("Internado")) {
			attr.addFlashAttribute("falha", "O paciente já possui uma internação em andamento!");
			return "redirect:/internacoes/listar";
		}
		if (internacao.hasNotId() && status.equals("Encerrada")) {
			attr.addFlashAttribute("falha", "O paciente não possui nenhuma internação em andamento!");
			return "redirect:/internacoes/listar";
		}
		if (internacao.hasId() && status.equals("Encerrada")) {
			animal.setStatus("Normal");
		}

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
			if (internacao.hasNotId() && status.equals("Ativa")) {
				historico.setDescricao("O paciente " + internacao.getAnimal().getNome() + " foi internado.");
				historico.setTipo("Nova internação");
				historico.setUsuario(veterinario.getNome() + " (veterinario)");
				historico.setData(data);
				historico.setHora(hora);
				animal.setStatus("Internado");

			}
			if (internacao.hasId() && status.equals("Ativa")) {
				Internacao statusHistorico = service.buscarPorId(internacao.getId());
				if (!statusHistorico.getDescricao().contains(internacao.getDescricao())) {
					mud.append("As anotações da internação foram alteradas" + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!statusHistorico.getPrescricao().contains(internacao.getPrescricao())) {
					mud.append("A prescrição da internação foi alterada" + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!statusHistorico.getDataEntrada().equals(internacao.getDataEntrada())) {
					mud.append("A data de admissão do paciente foi mudada de "
							+ statusHistorico.getDataEntrada()
									.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
							+ " para "
							+ internacao.getDataEntrada().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
							+ "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!statusHistorico.getHoraEntrada().equals(internacao.getHoraEntrada())) {
					mud.append("A hora de entrada da internação foi mudada de " + statusHistorico.getHoraEntrada()
							+ " para " + internacao.getHoraEntrada() + "." + ";");
					historico.setDescricao(mud.toString());
				}

				if (historico.getDescricao() != null) {

					historico.setTipo("Alteração de internação");
					historico.setUsuario(veterinario.getNome() + " (veterinario)");
					historico.setData(data);
					historico.setHora(hora);

				}

			}
			if (internacao.hasId() && status.equals("Encerrada")) {
				internacao.setDataSaida(data);
				internacao.setHoraSaida(hora);
				historico.setDescricao("O paciente " + internacao.getAnimal().getNome() + " saiu da internação.");
				historico.setTipo("Nova internação");
				historico.setUsuario(veterinario.getNome() + " (veterinario)");
				historico.setData(data);
				historico.setHora(hora);
				animal.setStatus("Normal");
			}

		}

		internacao.setAnimal(animal);

		service.salvarInternacao(internacao);
		if (files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				FotoInternacao foto = new FotoInternacao();
				foto.setFileName(files[i].getOriginalFilename());
				switch(files[i].getContentType()) {
				case "image/jpeg":
					foto.setTipo("jpg");
					break;
				case "image/jpg":
					foto.setTipo("jpg");
					break;
				case "image/png":
					foto.setTipo("png");
					break;
				default:
					model.addAttribute("falha", "Tipos suportados de foto são: Jpg, Jpeg ou PNG");
					break;
				}


				try {
					foto.setInternacao(internacao);
					fotoInternacaoService.salvarFotos(files, foto);
				} catch (Exception e) {
					System.out.println(e);
				}
			}

		}
		if (historico.getDescricao() != null) {
			historico.setAnimal(animal);
			historicoAnimalService.salvar(historico);
		}
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		return "redirect:/internacoes/listar";
	}

	@GetMapping("/listar")
	public String listarInternacoes(ModelMap model, Internacao internacao) {
		// model.addAttribute("internacaoAtiva", service.buscarInternacaoAtiva());
		// model.addAttribute("internacaoEncerrada",
		// service.buscarInternacaoEncerrada());
		return "internacao/lista";
	}

	@GetMapping("/datatables/server")
	public ResponseEntity<?> listarInternacoesDatatables(HttpServletRequest request) {
		return ResponseEntity.ok(service.buscarTodos(request));
	}

	@GetMapping("/datatables/fotos/server/{idInternacao}")
	public ResponseEntity<?> listarFotosDatatables(HttpServletRequest request, @PathVariable("idInternacao") Long id) {
		return ResponseEntity.ok(fotoInternacaoService.buscarTodos(request, id));
	}
	@GetMapping("/datatables/server/{idAnimal}")
	public ResponseEntity<?> listarInternacoesByAnimalDatatables(HttpServletRequest request,
			@PathVariable("idAnimal") Long idAnimal) {
		return ResponseEntity.ok(service.buscarInternacaoPorAnimal(request, idAnimal));
	}

	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("internacao", service.buscarPorId(id));
		// model.addAttribute("internacaoAtiva", service.buscarInternacaoAtiva());
		// model.addAttribute("internacaoEncerrada",
		// service.buscarInternacaoEncerrada());
		return "internacao/lista";
	}

	@GetMapping("/editar/{id}/paciente/{idAnimal}")
	public String preEditarInternacaoPorAnimal(@PathVariable("id") Long id, ModelMap model,
			@PathVariable("idAnimal") Long idAnimal, Aplicacao aplicacao, Consulta consulta) {
		Animal animal = animalService.buscarPorId(idAnimal);
		Especie especie = especieService.buscarEspeciePorAnimal(animal.getEspecie().getNome());
		model.addAttribute("internacao", service.buscarPorId(id));
		model.addAttribute("aplicacao", new Aplicacao());
		model.addAttribute("consulta", new Consulta());
		model.addAttribute("animal", animalService.buscarPorId(idAnimal));
		model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorAnimal(idAnimal));
		// model.addAttribute("consulta",
		// consultaService.buscarConsultaPorAnimal(idAnimal));
		model.addAttribute("vacinas", vacinaService.buscarTodasVacinasPorEspecie(especie.getNome()));
		return "animal/visualizar";
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		Internacao internacao = service.buscarPorId(id);
		Animal animal = animalService.buscarPorId(internacao.getAnimal().getId());

		if (animal.getStatus().contains("Internado")) {
			animal.setStatus("Normal");
		}
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/internacoes/listar";
	}

	@GetMapping("/excluir/{id}/paciente/{idAnimal}")
	public String excluirPorAnimal(@PathVariable("id") Long id, @PathVariable("idAnimal") Long idAnimal,
			RedirectAttributes attr) {
		Internacao internacao = service.buscarPorId(id);
		Animal animal = animalService.buscarPorId(internacao.getAnimal().getId());
		if (animal.getStatus().contains("Internado")) {
			animal.setStatus("Normal");
		}
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/pacientes/visualizar/{idAnimal}";
	}

	@GetMapping("/visualizar/{id}")
	public String visualizar(@PathVariable("id") Long id, ModelMap model, FotoInternacao fotoInternacao) {
		model.addAttribute("internacao", service.buscarPorId(id));
		model.addAttribute("fotoInternacao", new FotoInternacao());
		return "internacao/visualizar";
	}

	@PostMapping("/salvar/paciente/{idAnimal}")
	public String salvarInternacaoPorPaciente(@Valid Internacao internacao, BindingResult result,
			RedirectAttributes attr, @AuthenticationPrincipal User user, @RequestParam("status") String status,
			@RequestParam("files") MultipartFile[] files, ModelMap model, @PathVariable("idAnimal") Long idAnimal,
			Aplicacao aplicacao, Consulta consulta) {

		if (result.hasErrors() || internacao.getAnimal().getNome().isEmpty()) {
			Animal animal = animalService.buscarPorId(idAnimal);
			Especie especie = especieService.buscarEspeciePorAnimal(animal.getEspecie().getNome());
			model.addAttribute("erro", "Por favor preencha os dados");
			model.addAttribute("animal", animalService.buscarPorId(idAnimal));
			model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorAnimal(idAnimal));
			model.addAttribute("vacinas", vacinaService.buscarTodasVacinasPorEspecie(especie.getNome()));
			// model.addAttribute("consulta",
			// consultaService.buscarConsultaPorAnimal(idAnimal));

			return "animal/visualizar";
		}
		if (internacao.hasId()) {
			Internacao int2 = service.buscarPorId(internacao.getId());
			if (int2.getStatus().equals("Encerrada") && status.equals("Ativa")) {
				attr.addFlashAttribute("falha", "Essa internação já foi encerrada, por favor cadastre uma nova");
				return "redirect:/pacientes/visualizar/{idAnimal}";
			}

		}
		String titulo = internacao.getAnimal().getNome();
		Animal animal = animalService.buscarPorTitulos(new String[] { titulo }).stream().findFirst().get();
		if (internacao.hasNotId() && animal.getStatus().equals("Internado")) {
			attr.addFlashAttribute("falha", "O paciente já possui uma internação em andamento!");
			return "redirect:/pacientes/visualizar/{idAnimal}";
		}
		if (internacao.hasNotId() && status.equals("Encerrada")) {
			attr.addFlashAttribute("falha", "O paciente não possui nenhuma internação em andamento!");
			return "redirect:/pacientes/visualizar/{idAnimal}";
		}
		if (internacao.hasId() && status.equals("Encerrada")) {
			animal.setStatus("Normal");
		}

		if (files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				FotoInternacao foto = new FotoInternacao();
				foto.setFileName(files[i].getOriginalFilename());
				switch(files[i].getContentType()) {
				case "image/jpeg":
					foto.setTipo("jpg");
					break;
				case "image/jpg":
					foto.setTipo("jpg");
					break;
				case "image/png":
					foto.setTipo("png");
					break;
				default:
					model.addAttribute("falha", "Tipos suportados de foto são: Jpg, Jpeg ou PNG");
					break;
				}


				try {
					foto.setInternacao(internacao);
					fotoInternacaoService.salvarFotos(files, foto);
				} catch (Exception e) {
					System.out.println(e);
				}
			}

		}
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
			if (internacao.hasNotId() && status.equals("Ativa")) {
				historico.setDescricao("O paciente " + internacao.getAnimal().getNome() + " foi internado.");
				historico.setTipo("Nova internação");
				historico.setUsuario(veterinario.getNome() + " (veterinario)");
				historico.setData(data);
				historico.setHora(hora);
				animal.setStatus("Internado");

			}
			if (internacao.hasId() && status.equals("Ativa")) {
				Internacao statusHistorico = service.buscarPorId(internacao.getId());
				if (!statusHistorico.getDescricao().contains(internacao.getDescricao())) {
					mud.append("As anotações da internação foram alteradas" + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!statusHistorico.getPrescricao().contains(internacao.getPrescricao())) {
					mud.append("A prescrição da internação foi alterada" + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!statusHistorico.getDataEntrada().equals(internacao.getDataEntrada())) {
					mud.append("A data de admissão do paciente foi mudada de "
							+ statusHistorico.getDataEntrada()
									.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
							+ " para "
							+ internacao.getDataEntrada().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
							+ "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!statusHistorico.getHoraEntrada().equals(internacao.getHoraEntrada())) {
					mud.append("A hora de entrada da internação foi mudada de " + statusHistorico.getHoraEntrada()
							+ " para " + internacao.getHoraEntrada() + "." + ";");
					historico.setDescricao(mud.toString());
				}

				if (historico.getDescricao() != null) {

					historico.setTipo("Alteração de internação");
					historico.setUsuario(veterinario.getNome() + " (veterinario)");
					historico.setData(data);
					historico.setHora(hora);

				}

			}
			if (internacao.hasId() && status.equals("Encerrada")) {
				internacao.setDataSaida(data);
				internacao.setHoraSaida(hora);
				historico.setDescricao("O paciente " + internacao.getAnimal().getNome() + " saiu da internação.");
				historico.setTipo("Nova internação");
				historico.setUsuario(veterinario.getNome() + " (veterinario)");
				historico.setData(data);
				historico.setHora(hora);
				animal.setStatus("Normal");
			}

		}

		internacao.setAnimal(animal);

		service.salvarInternacao(internacao);
		if (historico.getDescricao() != null) {
			historico.setAnimal(animal);
			historicoAnimalService.salvar(historico);
		}
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		return "redirect:/pacientes/visualizar/{idAnimal}";
	}
	@GetMapping("/editar/foto/{id}/internacao/{idInternacao}")
	public String preEditarFotos(@PathVariable("id") Long id,  @PathVariable("idInternacao") Long idInternacao, ModelMap model) {
		model.addAttribute("internacao", service.buscarPorId(idInternacao));
		model.addAttribute("fotoInternacao", fotoInternacaoService.buscarPorId(id));
		return "internacao/visualizar";
	}

	@GetMapping("/excluir/foto/{id}/internacao/{idInternacao}")
	public String excluirFotos(@PathVariable("id") Long id, @PathVariable("idInternacao") Long idInternacao, RedirectAttributes attr) {
		fotoInternacaoService.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/internacoes/visualizar/{idInternacao}";
	}

	@GetMapping("/visualizar/foto/{id}")
	public String visualizarFotos(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("foto", fotoInternacaoService.buscarPorId(id));
		return "/foto/visualizar";
	}
	@PostMapping("/salvar/foto/internacao/{id}")
	public String salvarFoto(@Valid FotoInternacao foto, @PathVariable("id") Long id, BindingResult result, RedirectAttributes attr,
			@AuthenticationPrincipal User user,
			@RequestParam("file") MultipartFile file, ModelMap model) {
		Internacao internacao = service.buscarPorId(id);
		if (result.hasErrors() || (file.isEmpty() && foto.hasNotId())) {
			model.addAttribute("erro", "Por favor preencha os dados");
			model.addAttribute("internacao", internacao);
			return "internacao/visualizar";
		}
		if (!file.isEmpty()) {
			switch(file.getContentType()) {
			case "image/jpeg":
				foto.setTipo("jpg");
				break;
			case "image/jpg":
				foto.setTipo("jpg");
				break;
			case "image/png":
				foto.setTipo("png");
				break;
			default:
				model.addAttribute("falha", "Tipos suportados de foto são: Jpg, Jpeg ou PNG");
				break;
			}
			try {
				
				foto.setInternacao(internacao);
				fotoInternacaoService.salvarFoto(file, foto);
				if(foto.hasId()) {
					attr.addFlashAttribute("sucesso", "Foto cadastrada com sucesso");
				}else {
					attr.addFlashAttribute("sucesso", "Foto alterada com sucesso");
				}

			} catch (Exception e) {
				attr.addFlashAttribute("falha", "Erro ao cadastrar foto!");
			}
		}
		if(file.isEmpty() && foto.hasId()) {
			FotoInternacao f2 = fotoInternacaoService.buscarPorId(foto.getId());
			foto.setFileName(f2.getFileName());
			foto.setThumb(f2.getThumb());
			foto.setPath(f2.getPath());
			foto.setTipo(f2.getTipo());
			foto.setInternacao(internacao);
			fotoInternacaoService.salvar(foto);
		}
		
		return"redirect:/internacoes/visualizar/{id}";
	}
}