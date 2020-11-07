package com.sponton.vetsystem.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.domain.Notificacao;
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Raca;
import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.AnimalService;
import com.sponton.vetsystem.service.ClienteService;
import com.sponton.vetsystem.service.EspecieService;
import com.sponton.vetsystem.service.NotificacaoService;
import com.sponton.vetsystem.service.RacaService;
import com.sponton.vetsystem.service.SecretariaService;
import com.sponton.vetsystem.service.VeterinarioService;

@Controller
public class HomeController {

	@Autowired
	private AnimalService animalService;

	@Autowired
	private EspecieService especieService;

	@Autowired
	private VeterinarioService veterinarioService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private SecretariaService secretariaService;

	@Autowired
	private RacaService racaService;
	
	@Autowired 
	private NotificacaoService notificacaoService;

	@GetMapping({ "/home" })
	public String home(ModelMap model, @AuthenticationPrincipal User user, RedirectAttributes attr) {
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.VETERINARIO.getDesc()))) {
			Veterinario veterinario = veterinarioService.buscarPorEmail(user.getUsername());
			if (veterinario.hasNotId()) {
				attr.addFlashAttribute("aviso",
						"Por favor preencha seus dados pessoais para continuar usando o sistema");
				return "redirect:/veterinarios/dados";
			}
		}
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.SECRETARIA.getDesc()))) {
			Secretaria secretaria = secretariaService.buscarPorEmail(user.getUsername());
			if (secretaria.hasNotId()) {
				attr.addFlashAttribute("aviso",
						"Por favor preencha seus dados pessoais para continuar usando o sistema");
				return "redirect:/secretarias/dados";
			}
			if(secretaria.hasId()) {
				List<Notificacao> notificacoes = notificacaoService.buscarNotificacaoPorSecretariaId(secretaria.getId());
				model.addAttribute("notificacoes", notificacoes);
			}
		}
		List<Raca> racas = racaService.buscarTodasRacas();
		if (racas.size() > 0) {
			int t = 0;
			Map<String, Integer> surveyMap = new LinkedHashMap<>();
			for (Raca r : racas) {

				List<Animal> separado = animalService.buscarAnimalPorRaca(r.getId());
				for (Animal a : separado) {
					t = (int) separado.stream().count();
				}
				surveyMap.put(r.getNome(), t);
			}
			model.addAttribute("surveyMap", surveyMap);
		}

		model.addAttribute("animal", animalService.buscarTodosAnimais());
		model.addAttribute("cliente", clienteService.buscarTodosClientes());

		return "home";
	}

	// abrir pagina login
	@GetMapping({ "/", "/login" })
	public String login() {

		return "login";
	}

	@GetMapping({ "/login-error" })
	public String loginError(ModelMap model) {
		model.addAttribute("alerta", "erro");
		model.addAttribute("titulo", "Credenciais inválidas!");
		model.addAttribute("texto", "Login ou senha inválidos, tente novamente");
		model.addAttribute("subtexto", "Acesso permitido apenas para cadastros já ativados");
		return "login";
	}

	// acesso negado
	@GetMapping({ "/acesso-negado" })
	public String acessoNegado(ModelMap model, HttpServletResponse resp) {
		model.addAttribute("status", resp.getStatus());
		model.addAttribute("error", "Acesso negado");
		model.addAttribute("message", "Você não tem permissão para acesso a esta área ou ação");
		return "error";
	}
	/*
	 * @RequestMapping("piechart") public ResponseEntity<?> getPieChart(){
	 * List<Especie> animais = especieService.buscarTodasEspecies(); List<Raca>
	 * racas = racaService.buscarTodasRacas(); return new ResponseEntity<>(animais,
	 * HttpStatus.OK); }
	 */
	/*
	 * @RequestMapping("piechart") public ResponseEntity<?> getPieChart(){
	 * List<Raca> racas = racaService.buscarTodasRacas(); int t =0; for(Raca r :
	 * racas) { List<Animal> separado =
	 * animalService.buscarAnimalPorRaca(r.getId()); for(Animal a : separado) { t =
	 * (int)separado.stream().count(); } System.out.println(t); }
	 * 
	 * return ResponseEntity.ok(racas); }
	 */

}
