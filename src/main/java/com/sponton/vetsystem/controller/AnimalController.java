package com.sponton.vetsystem.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Cliente;
import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.domain.Foto;
import com.sponton.vetsystem.domain.HistoricoAnimal;
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Raca;
import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.AnimalService;
import com.sponton.vetsystem.service.ClienteService;
import com.sponton.vetsystem.service.ConsultaService;
import com.sponton.vetsystem.service.EspecieService;
import com.sponton.vetsystem.service.FotoService;
import com.sponton.vetsystem.service.HistoricoAnimalService;
import com.sponton.vetsystem.service.RacaService;
import com.sponton.vetsystem.service.SecretariaService;
import com.sponton.vetsystem.service.VeterinarioService;

@Controller
@RequestMapping("pacientes")
public class AnimalController {

	@Autowired
	private AnimalService service;

	@Autowired
	private EspecieService especieService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private HistoricoAnimalService historicoAnimalService;

	@Autowired
	private RacaService racaService;

	@Autowired
	private VeterinarioService veterinarioService;

	@Autowired
	private SecretariaService secretariaService;

	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private FotoService fotoService;

	@GetMapping("/cadastrar")
	public String novoAnimal(Animal animal) {
		return "animal/cadastro";
	}

	@PostMapping("/salvar")
	public String salvarAnimal(@Valid Animal animal, BindingResult result, RedirectAttributes attr,
			@AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file) {
		if (result.hasErrors()) {
			return "animal/cadastro";
		}

		String titulo = animal.getEspecie().getNome();
		String titulo2 = animal.getRaca().getNome();
		Especie especie = especieService.buscarPorTitulos(new String[] { titulo }).stream().findFirst().get();
		Raca raca = racaService.buscarPorTitulos(new String[] { titulo2 }).stream().findFirst().get();
		if (raca.getEspecie().getId() != especie.getId()) {
			attr.addFlashAttribute("falha", "Espécie e raça não condizem!");
			return "redirect:/pacientes/cadastrar";
		}
		HistoricoAnimal historico = new HistoricoAnimal();
		LocalDate data = LocalDate.now();
		LocalTime hora = LocalTime.now();
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.VETERINARIO.getDesc()))) {
			Veterinario veterinario = veterinarioService.buscarPorEmail(user.getUsername());
			if (animal.hasNotId()) {
				historico.setDescricao("O paciente foi criado com sucesso");
				historico.setTipo("Dados");
				historico.setUsuario(veterinario.getNome() + " (veterinario)");
				historico.setData(data);
				historico.setHora(hora);

			}
			if (animal.hasId()) {
				historico.setDescricao("O paciente foi alterado com sucesso");
				historico.setTipo("Mudar dados");
				historico.setUsuario(veterinario.getNome() + " (veterinario)");
				historico.setData(data);
				historico.setHora(hora);
			}
		}
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.SECRETARIA.getDesc()))) {
			Secretaria secretaria = secretariaService.buscarPorEmail(user.getUsername());
			if (animal.hasNotId()) {
				historico.setDescricao("O paciente foi criado com sucesso");
				historico.setTipo("Dados");
				historico.setUsuario(secretaria.getNome() + " (secretária)");
				historico.setData(data);
				historico.setHora(hora);

			}
			if (animal.hasId()) {
				historico.setDescricao("O paciente foi alterado com sucesso");
				historico.setTipo("Mudar dados");
				historico.setUsuario(secretaria.getNome() + " (secretária)");
				historico.setData(data);
				historico.setHora(hora);
			}
		}
		if (!file.isEmpty()) {
			if(animal.getFoto().hasNotId()) {
				Foto foto = new Foto();
				foto.setFileName(file.getOriginalFilename());
				foto.setPath("/uploads/");
				animal.setFoto(foto);
				try {
					fotoService.salvarFoto(file, foto);
				} catch (Exception e) {
					attr.addFlashAttribute("falha", "Erro ao cadastrar foto!");
				}
			}
			if(animal.getFoto().hasId()) {
				Foto foto = fotoService.buscarFotoId(animal.getFoto().getId());
				foto.setFileName(file.getOriginalFilename());
				foto.setPath("/uploads/");
				animal.setFoto(foto);
				try {
					fotoService.salvarFoto(file, foto);
				} catch (Exception e) {
					attr.addFlashAttribute("falha", "Erro ao cadastrar foto!");
				}
			}
				
		}

		animal.setEspecie(especie);
		animal.setRaca(raca);
		service.salvarAnimal(animal);
		historico.setAnimal(animal);
		historicoAnimalService.salvar(historico);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");

		return "redirect:/pacientes/cadastrar";

	}

	@ModelAttribute("clientes")
	public List<Cliente> listaDeClientes() {
		return clienteService.buscarTodosClientes();
	}

	@GetMapping("/listar")
	public String listarAnimais() {
		return "animal/lista";
	}

	@GetMapping("/datatables/server")
	public ResponseEntity<?> listarAnimaisDatatables(HttpServletRequest request) {
		return ResponseEntity.ok(service.buscarTodos(request));
	}

	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("animal", service.buscarPorId(id));
		return "animal/cadastro";
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/pacientes/listar";
	}

	@GetMapping("/visualizar/{id}")
	public String visualizar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("animal", service.buscarPorId(id));
		model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorAnimal(id));
		model.addAttribute("consulta", consultaService.buscarConsultaPorAnimal(id));
		return "animal/visualizar";
	}

	@PostMapping("/buscar/tipo/paciente/{id}")
	public String getPorTipo(@RequestParam("tipo") String tipo, ModelMap model, @PathVariable("id") Long id) {
		model.addAttribute("animal", service.buscarPorId(id));
		model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorTipo(tipo, id));
		model.addAttribute("consulta", consultaService.buscarConsultaPorAnimal(id));
		return "animal/visualizar";
	}

	@PostMapping("/buscar/data/paciente/{id}")
	public String getPorData(@RequestParam("teste") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
			ModelMap model, @PathVariable("id") Long id) {
		model.addAttribute("animal", service.buscarPorId(id));
		model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorData(data, id));
		model.addAttribute("consulta", consultaService.buscarConsultaPorAnimal(id));
		return "animal/visualizar";
	}

	@GetMapping("/titulo")
	public ResponseEntity<?> getAnimaisPorTermo(@RequestParam("termo") String termo) {
		List<String> animais = service.buscarAnimaisByTermo(termo);
		return ResponseEntity.ok(animais);
	}

}
