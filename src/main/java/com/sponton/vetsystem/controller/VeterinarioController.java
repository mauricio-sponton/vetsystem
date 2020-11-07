package com.sponton.vetsystem.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.CargaHoraria;
import com.sponton.vetsystem.domain.CargaHorariaDTO;
import com.sponton.vetsystem.domain.Foto;
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Usuario;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.CargaHorariaService;
import com.sponton.vetsystem.service.FotoService;
import com.sponton.vetsystem.service.UsuarioService;
import com.sponton.vetsystem.service.VeterinarioService;

@Controller
@RequestMapping("veterinarios")
public class VeterinarioController {

	@Autowired
	private VeterinarioService service;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private FotoService fotoService;

	@Autowired
	private CargaHorariaService cargaHorariaService;

	@GetMapping("/dados")
	public String abrirPorVeterinario(Veterinario veterinario, ModelMap model, @AuthenticationPrincipal User user) {

		veterinario = service.buscarPorEmail(user.getUsername());
		if (veterinario.hasNotId()) {
			model.addAttribute("veterinario", veterinario);
			return "veterinario/visualizar";
		}
		if (veterinario.hasId()) {
			List<CargaHoraria> cargasVet = cargaHorariaService.buscarHorarioPorVeterinario(veterinario.getId());
			if (cargasVet.isEmpty()) {
				CargaHorariaDTO cargasForm = new CargaHorariaDTO();
				for (int i = 1; i <= 7; i++) {
					cargasForm.addCarga(new CargaHoraria());

				}
				model.addAttribute("form", cargasForm);
			}

			if (cargasVet.size() > 0) {
				List<CargaHoraria> listaEdicao = new ArrayList<>();
				cargaHorariaService.buscarHorarioPorVeterinario(veterinario.getId()).iterator()
						.forEachRemaining(listaEdicao::add);
				model.addAttribute("formEdit", new CargaHorariaDTO(listaEdicao));
			}

			model.addAttribute("veterinario", veterinario);
			return "veterinario/visualizar";
		}

		return "veterinario/visualizar";
	}

	@PostMapping("/salvar/horarios")
	public String salvarHorarios(@Valid @ModelAttribute CargaHorariaDTO form, BindingResult result, Model model,
			Veterinario veterinario, RedirectAttributes attr) {
		int t = 1;
		for (CargaHoraria c : form.getCargas()) {

			c.setVeterinario(veterinario);
			if (c.getInicio() == null && c.getFim() != null || c.getInicio() != null && c.getFim() == null) {
				attr.addFlashAttribute("falha", "Os horários dos dias trabalhados devem ser preenchidos.");
				return "redirect:/veterinarios/dados";
			}
			if (c.getInicio() != null && c.getFim() != null) {

				if (c.getInicio().isAfter(c.getFim()) || c.getInicio().equals(c.getFim())) {
					attr.addFlashAttribute("falha", "O horário de entrada não pode ser maior que o horário de saída.");
					return "redirect:/veterinarios/dados";
				}

			}

			switch (t) {
			case 1:
				c.setDiaDaSemana(Calendar.SUNDAY);
				break;
			case 2:
				c.setDiaDaSemana(Calendar.MONDAY);
				break;
			case 3:
				c.setDiaDaSemana(Calendar.TUESDAY);
				break;
			case 4:
				c.setDiaDaSemana(Calendar.WEDNESDAY);
				break;
			case 5:
				c.setDiaDaSemana(Calendar.THURSDAY);
				break;
			case 6:
				c.setDiaDaSemana(Calendar.FRIDAY);
				break;
			case 7:
				c.setDiaDaSemana(Calendar.SATURDAY);
				break;

			}
			t = t + 1;
			if (c.isAtivo() == true) {
				c.setFim(null);
				c.setInicio(null);
			}

		}

		cargaHorariaService.salvarTodos(form.getCargas());
		attr.addFlashAttribute("sucesso", "Dados cadastrados com sucesso");
		return "redirect:/veterinarios/dados";
	}

	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("veterinario", service.buscarPorId(id));
		return "veterinario/visualizar";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid Veterinario veterinario, BindingResult result, RedirectAttributes attr,
			@AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file, ModelMap model) {
		if (result.hasErrors()) {
			model.addAttribute("erro", "Por favor preencha seus dados");
			return "veterinario/visualizar";
		}
		try {
			if (veterinario.hasNotId() && veterinario.getUsuario().hasNotId()) {
				Usuario usuario = usuarioService.buscarPorEmail(user.getUsername());
				veterinario.setUsuario(usuario);
			}
			if (!file.isEmpty()) {
				if (veterinario.getFoto().hasNotId()) {
					Foto foto = new Foto();
					foto.setFileName(file.getOriginalFilename());
					foto.setPath("/uploads/");
					veterinario.setFoto(foto);
					try {
						fotoService.salvarFoto(file, foto);
					} catch (Exception e) {
						attr.addFlashAttribute("falha", "Erro ao cadastrar foto!");
					}
				}
				if (veterinario.getFoto().hasId()) {
					Foto foto = fotoService.buscarFotoId(veterinario.getFoto().getId());
					foto.setFileName(file.getOriginalFilename());
					foto.setPath("/uploads/");
					veterinario.setFoto(foto);
					try {
						fotoService.salvarFoto(file, foto);
					} catch (Exception e) {
						attr.addFlashAttribute("falha", "Erro ao cadastrar foto!");
					}
				}

			}
			if (file.isEmpty() && veterinario.hasId()) {
				Foto foto = fotoService.buscarFotoId(veterinario.getFoto().getId());
				veterinario.setFoto(foto);
				try {
					fotoService.salvar(foto);
				} catch (Exception e) {
					attr.addFlashAttribute("falha", "Erro ao cadastrar foto!");
				}
			}
			service.salvar(veterinario);
			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
			attr.addFlashAttribute("veterinario", veterinario);
		} catch (DataIntegrityViolationException ex) {
			attr.addFlashAttribute("falha", "CRMV já cadastrado no sistema");
		}

		return "redirect:/veterinarios/dados";

	}

	@GetMapping("/titulo")
	public ResponseEntity<?> getVeterinariosPorTermo(@RequestParam("termo") String termo,
			@RequestParam("inicio") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime inicio,
			@RequestParam("fim") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime fim,
			@RequestParam("diaInicial") int diaInicial, @RequestParam("diaFinal") int diaFinal) {
		LocalTime start = inicio.toLocalTime();
		LocalTime end = fim.toLocalTime();
		List<String> vets = service.buscarVeterinariosByTermo(termo, start, end, diaInicial, diaFinal);
		return ResponseEntity.ok(vets);
	}

	@GetMapping("/nome")
	public ResponseEntity<?> getVeterinariosPeloNome(@RequestParam("termo") String termo) {
		List<String> vets = service.buscarVeterinariosPeloNome(termo);
		return ResponseEntity.ok(vets);
	}

	@GetMapping("/nome/{titulo}")
	public ResponseEntity<?> getVeterinarioPeloNome(@PathVariable("titulo") String titulo) {
		Optional<Veterinario> vet = service.buscarVeterinarioPeloNome(titulo);
		return ResponseEntity.ok(vet);
	}
	@GetMapping("/horarios/{id}")
	public ResponseEntity<?> getHorariosPeloVeterinarioId(@PathVariable("id") Long id, ModelMap model){
		List<CargaHoraria> cargasVet = cargaHorariaService.buscarHorarioPorVeterinario(id);
		/*if (cargasVet.size() > 0) {
			List<CargaHoraria> listaEdicao = new ArrayList<>();
			cargaHorariaService.buscarHorarioPorVeterinario(id).iterator()
					.forEachRemaining(listaEdicao::add);
			ModelMap t = model.addAttribute("formEdit", new CargaHorariaDTO(listaEdicao));
			return ResponseEntity.ok(t);
		}*/
		return ResponseEntity.ok(cargasVet);
	}

}
