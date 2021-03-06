package com.sponton.vetsystem.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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

import com.sponton.vetsystem.domain.Agendamento;
import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.domain.Internacao;
import com.sponton.vetsystem.domain.Notificacao;
import com.sponton.vetsystem.domain.Perfil;
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Raca;
import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.domain.Usuario;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.AgendamentoService;
import com.sponton.vetsystem.service.AnimalService;
import com.sponton.vetsystem.service.ClienteService;
import com.sponton.vetsystem.service.EspecieService;
import com.sponton.vetsystem.service.InternacaoService;
import com.sponton.vetsystem.service.NotificacaoService;
import com.sponton.vetsystem.service.RacaService;
import com.sponton.vetsystem.service.SecretariaService;
import com.sponton.vetsystem.service.UsuarioService;
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
	private UsuarioService usuarioService;

	@Autowired
	private AgendamentoService agendamentoService;

	@Autowired
	private InternacaoService internacaoService;

	@GetMapping({ "/home" })
	public String home(ModelMap model, @AuthenticationPrincipal User user, RedirectAttributes attr) {
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.VETERINARIO.getDesc()))) {
			Veterinario veterinario = veterinarioService.buscarPorEmail(user.getUsername());
			if (veterinario.hasNotId()) {
				attr.addFlashAttribute("aviso",
						"Por favor preencha seus dados pessoais para continuar usando o sistema");
				return "redirect:/veterinarios/dados";
			}
			if (veterinario.hasId()) {
				List<Agendamento> agendamentos = agendamentoService.buscarVeterinarioPorId(veterinario.getId());
				LocalDate hoje = LocalDate.now();
				int contador = 0;
				for (Agendamento a : agendamentos) {
					LocalDate l = a.getInicio().toLocalDate();
					if (l.equals(hoje)) {
						contador += 1;
					}

				}
				model.addAttribute("consultasHoje", contador);
				model.addAttribute("veterinario", veterinario);
			}
		}
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.SECRETARIA.getDesc()))) {
			Secretaria secretaria = secretariaService.buscarPorEmail(user.getUsername());
			if (secretaria.hasNotId()) {
				attr.addFlashAttribute("aviso",
						"Por favor preencha seus dados pessoais para continuar usando o sistema");
				return "redirect:/secretarias/dados";
			}
			if (secretaria.hasId()) {
				model.addAttribute("secretaria", secretaria);
				List<Agendamento> agendamentos = agendamentoService.buscarTodos();
				LocalDate hoje = LocalDate.now();
				int semana = hoje.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
				int contadorDia = 0;
				int contadorSemana = 0;
				for (Agendamento a : agendamentos) {
					LocalDate agendamentoHoje = a.getInicio().toLocalDate();
					int week = agendamentoHoje.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
					System.out.println(week);
					if (week == semana) {
						contadorSemana += 1;
					}
					if (agendamentoHoje.equals(hoje)) {
						contadorDia += 1;
					}

				}
				model.addAttribute("consultasHojeSec", contadorDia);
				model.addAttribute("consultasSemana", contadorSemana);
			}

		}

		List<Internacao> internacoes = internacaoService.buscarTodasInternacoesAtiva();
		List<Usuario> usuarios = usuarioService.buscarTodosUsuarios();
		int totalsec =0;
		int totalvet=0;
		int totaladm=0;

		for (Usuario u : usuarios) {
			if (u.getPerfis().contains(new Perfil(PerfilTipo.SECRETARIA.getCod()))) {
				totalsec+=1;
			}
			if (u.getPerfis().contains(new Perfil(PerfilTipo.VETERINARIO.getCod()))) {
				totalvet+=1;
			}
			if (u.getPerfis().contains(new Perfil(PerfilTipo.ADMIN.getCod()))) {
				totaladm+=1;
			}
		}
		model.addAttribute("totalsec", totalsec);
		model.addAttribute("totalvet", totalvet);
		model.addAttribute("totaladm", totaladm);
		/*
		List<Raca> racas = racaService.buscarTodasRacas();
		if (racas.size() > 0) {
			int t = 0;
			Map<String, Integer> surveyMap = new LinkedHashMap<>();
			for (Raca r : racas) {

				List<Animal> separado = animalService.buscarAnimalPorRaca(r.getId());
				for (Animal a : separado) {
					t = (int) separado.stream().count();
					surveyMap.put(r.getNome(), t);
				}

			}
			model.addAttribute("surveyMap", surveyMap);
		}
		List<Especie> especies = especieService.buscarTodasEspecies();
		if (especies.size() > 0) {
			int t = 0;
			Map<String, Integer> surveyMap = new LinkedHashMap<>();
			for (Especie e : especies) {

				List<Animal> separado = animalService.buscarAnimalPorEspecie(e.getId());
				for (Animal a : separado) {
					t = (int) separado.stream().count();
					surveyMap.put(e.getNome(), t);
				}

			}
			model.addAttribute("especies", surveyMap);
		}
		*/
		model.addAttribute("animal", animalService.buscarTodosAnimais());
		model.addAttribute("cliente", clienteService.buscarTodosClientes());
		model.addAttribute("internacao", internacoes);
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
