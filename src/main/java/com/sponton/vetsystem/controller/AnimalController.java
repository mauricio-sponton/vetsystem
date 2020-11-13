package com.sponton.vetsystem.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTML;
import javax.validation.Valid;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.commons.compress.utils.IOUtils;
//import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.sponton.vetsystem.domain.Animal;
import com.sponton.vetsystem.domain.Aplicacao;
import com.sponton.vetsystem.domain.Cliente;
import com.sponton.vetsystem.domain.Consulta;
import com.sponton.vetsystem.domain.Especie;
import com.sponton.vetsystem.domain.Foto;
import com.sponton.vetsystem.domain.HistoricoAnimal;
import com.sponton.vetsystem.domain.Internacao;
import com.sponton.vetsystem.domain.PerfilTipo;
import com.sponton.vetsystem.domain.Raca;
import com.sponton.vetsystem.domain.Secretaria;
import com.sponton.vetsystem.domain.Vacina;
import com.sponton.vetsystem.domain.Veterinario;
import com.sponton.vetsystem.service.AnimalService;
import com.sponton.vetsystem.service.AplicacaoService;
import com.sponton.vetsystem.service.ClienteService;
import com.sponton.vetsystem.service.ConsultaService;
import com.sponton.vetsystem.service.EspecieService;
import com.sponton.vetsystem.service.FotoService;
import com.sponton.vetsystem.service.HistoricoAnimalService;
import com.sponton.vetsystem.service.InternacaoService;
import com.sponton.vetsystem.service.RacaService;
import com.sponton.vetsystem.service.SecretariaService;
import com.sponton.vetsystem.service.VacinaService;
import com.sponton.vetsystem.service.VeterinarioService;

@Controller
@RequestMapping("pacientes")
public class AnimalController {

	private final String file = "src/main/resources/static/uploads/";
	private final String imagem = "src/main/resources/static/image/";

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

	@Autowired
	private AplicacaoService aplicacaoService;

	@Autowired
	private VacinaService vacinaService;
	
	@Autowired
	private InternacaoService internacaoService;

	@GetMapping("/cadastrar")
	public String novoAnimal(Animal animal) {
		return "animal/cadastro";
	}

	@PostMapping("/salvar")
	public String salvarAnimal(@Valid Animal animal, BindingResult result, RedirectAttributes attr,
			@AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file, ModelMap model) {
		if (result.hasErrors()) {
			model.addAttribute("erro", "Por favor preencha os dados");
			return "animal/lista";
		}

		String titulo = animal.getEspecie().getNome();
		String titulo2 = animal.getRaca().getNome();
		Especie especie = especieService.buscarPorTitulos(new String[] { titulo }).stream().findFirst().get();
		Raca raca = racaService.buscarPorTitulos(new String[] { titulo2 }).stream().findFirst().get();

		if (raca.getEspecie().getId() != especie.getId()) {
			attr.addFlashAttribute("falha", "Espécie e raça não condizem!");
			return "redirect:/pacientes/listar";
		}
		HistoricoAnimal historico = new HistoricoAnimal();
		LocalDate data = LocalDate.now();
		LocalTime hora = LocalTime.now();
		if (!file.isEmpty()) {
			if (animal.getFoto().hasNotId()) {
				Foto foto = new Foto();
				foto.setFileName(file.getOriginalFilename());
				foto.setPath("/uploads/");
				animal.setFoto(foto);
				try {
					fotoService.salvarFoto(file, foto);
				} catch (Exception e) {
					attr.addFlashAttribute("falha", "Foto n possui id!");
				}
			}
			/*
			if (animal.getFoto().hasId()) {
				Foto foto = fotoService.buscarFotoId(animal.getFoto().getId());
				foto.setFileName(file.getOriginalFilename());
					foto.setPath("/uploads/");
					animal.setFoto(foto);
				try {
					fotoService.salvarFoto(file, foto);
				} catch (Exception e) {
					attr.addFlashAttribute("falha", "Foto já cadastrada!");
				}
			}
*/
		}
		if (!file.isEmpty() && animal.hasId()) {
			Foto foto = fotoService.buscarFotoId(animal.getFoto().getId());
			foto.setFileName(file.getOriginalFilename());
			foto.setPath("/uploads/");
			animal.setFoto(foto);
			
			try {
				fotoService.salvar(foto);
			} catch (Exception e) {
				attr.addFlashAttribute("falha", "Erro ao cadastrar foto!");
			}
		}
		if (file.isEmpty() && animal.hasId()) {
			Foto foto = fotoService.buscarFotoId(animal.getFoto().getId());
			animal.setFoto(foto);
			try {
				fotoService.salvar(foto);
			} catch (Exception e) {
				attr.addFlashAttribute("falha", "Erro ao cadastrar foto!");
			}
		}
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.VETERINARIO.getDesc()))) {
			Veterinario veterinario = veterinarioService.buscarPorEmail(user.getUsername());
			
			if (animal.hasNotId()) {
				historico.setDescricao("O paciente foi criado com sucesso");
				historico.setTipo("Dados");
				historico.setUsuario(veterinario.getNome() + " (veterinario)");
				historico.setData(data);
				historico.setHora(hora);
				animal.setStatus("Normal");

			}
			if (animal.hasId()) {
				Animal status = service.buscarPorId(animal.getId());
				Foto foto = fotoService.buscarFotoId(animal.getFoto().getId());
				StringBuilder mud = new StringBuilder();
				if (!status.getNome().contains(animal.getNome())) {
					mud.append("O nome do paciente foi mudado de " + status.getNome() + " para " + animal.getNome()
							+ "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getCliente().getNome().contains(animal.getCliente().getNome())) {
					mud.append("O dono do paciente foi mudado de " + status.getCliente().getNome() + " para "
							+ animal.getCliente().getNome() + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getRaca().getNome().contains(animal.getRaca().getNome())) {
					mud.append("A raça do paciente foi mudado de " + status.getRaca().getNome() + " para "
							+ animal.getRaca().getNome() + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getEspecie().getNome().contains(animal.getEspecie().getNome())) {
					mud.append("A espécie do paciente foi mudado de " + status.getEspecie().getNome() + " para "
							+ animal.getEspecie().getNome() + "." + ";");
					historico.setDescricao(mud.toString());
				}

				if (!status.getDataNascimento().equals(animal.getDataNascimento())) {
					mud.append("A data de nascimento do paciente foi mudado de "
							+ status.getDataNascimento().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
							+ " para "
							+ animal.getDataNascimento().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
							+ "." + ";");
					historico.setDescricao(mud.toString());
				}

				if (!status.getSexo().contains(animal.getSexo())) {
					mud.append("O sexo do paciente foi mudado de " + status.getSexo() + " para " + animal.getSexo()
							+ "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getAlergias().contains(animal.getAlergias())) {
					mud.append("As alergias do paciente foram alteradas " + "." + ";");
					historico.setDescricao(mud.toString());
				}
				
				if (historico.getDescricao() != null) {
					historico.setTipo("Mudar dados");
					historico.setUsuario(veterinario.getNome() + " (veterinario)");
					historico.setData(data);
					historico.setHora(hora);
					animal.setStatus(status.getStatus());
				}

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
				animal.setStatus("Normal");

			}
			if (animal.hasId()) {
				Animal status = service.buscarPorId(animal.getId());
				StringBuilder mud = new StringBuilder();
				if (!status.getNome().contains(animal.getNome())) {
					mud.append("O nome do paciente foi mudado de " + status.getNome() + " para " + animal.getNome()
							+ "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getCliente().getNome().contains(animal.getCliente().getNome())) {
					mud.append("O dono do paciente foi mudado de " + status.getCliente().getNome() + " para "
							+ animal.getCliente().getNome() + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getRaca().getNome().contains(animal.getRaca().getNome())) {
					mud.append("A raça do paciente foi mudado de " + status.getRaca().getNome() + " para "
							+ animal.getRaca().getNome() + "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getEspecie().getNome().contains(animal.getEspecie().getNome())) {
					mud.append("A espécie do paciente foi mudado de " + status.getEspecie().getNome() + " para "
							+ animal.getEspecie().getNome() + "." + ";");
					historico.setDescricao(mud.toString());
				}

				if (!status.getDataNascimento().equals(animal.getDataNascimento())) {
					mud.append("A data de nascimento do paciente foi mudado de "
							+ status.getDataNascimento().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
							+ " para "
							+ animal.getDataNascimento().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
							+ "." + ";");
					historico.setDescricao(mud.toString());
				}

				if (!status.getSexo().contains(animal.getSexo())) {
					mud.append("O sexo do paciente foi mudado de " + status.getSexo() + " para " + animal.getSexo()
							+ "." + ";");
					historico.setDescricao(mud.toString());
				}
				if (!status.getAlergias().contains(animal.getAlergias())) {
					mud.append("As alergias do paciente foram alteradas " + "." + ";");
					historico.setDescricao(mud.toString());
				}
				
				if (historico.getDescricao() != null) {
					historico.setTipo("Mudar dados");
					historico.setUsuario(secretaria.getNome() + " (secretária)");
					historico.setData(data);
					historico.setHora(hora);
					animal.setStatus(status.getStatus());
				}

			}
		}
		
		try {
			animal.setEspecie(especie);
			animal.setRaca(raca);
			service.salvarAnimal(animal);
			if (historico.getDescricao() != null) {
				historico.setAnimal(animal);
				historicoAnimalService.salvar(historico);
			}

			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		} catch (Exception e) {
			attr.addFlashAttribute("falha", "Erro ao cadastrar o paciente!");
		}

		return "redirect:/pacientes/listar";

	}

	@ModelAttribute("clientes")
	public List<Cliente> listaDeClientes() {
		return clienteService.buscarTodosClientes();
	}

	@GetMapping("/listar")
	public String listarAnimais(Animal animal) {
		return "animal/lista";
	}

	@GetMapping("/datatables/server")
	public ResponseEntity<?> listarAnimaisDatatables(HttpServletRequest request) {
		return ResponseEntity.ok(service.buscarTodos(request));
	}

	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("animal", service.buscarPorId(id));
		return "animal/lista";
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		return "redirect:/pacientes/listar";
	}

	@GetMapping("/visualizar/{id}")
	public String visualizar(@PathVariable("id") Long id, Internacao internacao, Aplicacao aplicacao, Consulta consulta, ModelMap model) {
		Animal animal = service.buscarPorId(id);
		aplicacao.setId(null);
		internacao.setId(null);
		consulta.setId(null);
		Especie especie = especieService.buscarEspeciePorAnimal(animal.getEspecie().getNome());
		model.addAttribute("animal", service.buscarPorId(id));
		model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorAnimal(id));
		//model.addAttribute("consulta", consultaService.buscarConsultaPorAnimal(id));
		model.addAttribute("vacinas", vacinaService.buscarTodasVacinasPorEspecie(especie.getNome()));
		
		
		return "animal/visualizar";
	}

	@GetMapping("/buscar/tipo/{tipo}/paciente/{id}")
	public ResponseEntity<?> getPorTipo(@PathVariable("tipo") String tipo, ModelMap model, @PathVariable("id") Long id) {
		List<HistoricoAnimal> historico = historicoAnimalService.buscarHistoricoPorTipo(tipo, id);
		return ResponseEntity.ok(historico);
	}

	@PostMapping("/buscar/data/paciente/{id}")
	public String getPorData(@RequestParam("teste") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
			ModelMap model, @PathVariable("id") Long id) {
		model.addAttribute("animal", service.buscarPorId(id));
		model.addAttribute("historico", historicoAnimalService.buscarHistoricoPorData(data, id));
		//model.addAttribute("consulta", consultaService.buscarConsultaPorAnimal(id));
		return "animal/visualizar";
	}

	@GetMapping("/titulo")
	public ResponseEntity<?> getAnimaisPorTermo(@RequestParam("termo") String termo) {
		List<Animal> animais = service.buscarAnimaisByTermo(termo);
		return ResponseEntity.ok(animais);
	}

	@GetMapping("/titulo/{termo}")
	public ResponseEntity<?> getAnimaisPorAlergias(@PathVariable("termo") String termo) {
		List<String> animais = service.buscarAnimaisByAlergias(termo);
		return ResponseEntity.ok(animais);
	}
	@ModelAttribute("caracteristicas")
	public List<String> getCaracteristicas() {
		List<String> lista = new ArrayList<>();
		lista.add("Castrado");
		lista.add("Bravo");
		return lista;
	}
	@GetMapping(value ="/download/historico/paciente/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody void criarArquivoHistorico(@PathVariable("id") Long id, RedirectAttributes attr, HttpServletRequest request, HttpServletResponse response) throws IOException, DocumentException{
		Animal animal = service.buscarPorId(id);
		List<HistoricoAnimal> historico = historicoAnimalService.buscarHistoricoPorAnimal(id);
		String arquivo = "historico";
		BufferedWriter writer = new BufferedWriter(new FileWriter(file + arquivo + ".html"));
		
		StringBuilder paciente = new StringBuilder();
		paciente.append("<img style=\"width:200px;height:60px;\" src=\"src/main/resources/static/image/loginho.png\"/>");
		paciente.append("<h2 style=\"color:green;font-weight:bold;\">");
		paciente.append(String.format("%10s%n%n","Paciente: " + animal.getNome()));
		paciente.append("</h2>");
		writer.write(paciente.toString());
		for(HistoricoAnimal h : historico) {
			StringBuilder build = new StringBuilder();
			build.append("<span style=\"color:gray;font-weight:bold;\">");
			build.append("Dia: " + h.getData().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) + " ");
			build.append("</span>");
			build.append("<span style=\"color:gray;font-weight:bold;display:inline\">");
			build.append(String.format("%10s%n%n", "às: " + h.getHora().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))));
			build.append("</span><p style=\"color:gray;font-weight:bold;display:block; margin-bottom:5px\">");
			build.append(String.format("%10s%n%n","Ocorrência: " + h.getDescricao()));
			build.append("</p>");
			writer.write(build.toString());
		}
		writer.close();
		conveterHTMLparaPDF(arquivo, request, response);
		
	}
	@GetMapping(value ="/download/consultas/paciente/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody void criarArquivoConsultas(@PathVariable("id") Long id, RedirectAttributes attr, HttpServletRequest request, HttpServletResponse response) throws IOException, DocumentException{
		Animal animal = service.buscarPorId(id);
		List<Consulta> consultas = consultaService.buscarConsultaPorAnimal(id);
		String arquivo = "consulta";
		BufferedWriter writer = new BufferedWriter(new FileWriter(file + arquivo + ".html"));
		
		StringBuilder paciente = new StringBuilder();
		paciente.append("<hr/>");
		paciente.append("<img style=\"width:200px;height:60px;\" src=\"src/main/resources/static/image/loginho.png\"/>");
		paciente.append("<h2 style=\"color:green;font-weight:bold;\">");
		paciente.append(String.format("%10s%n%n","Paciente: " + animal.getNome()));
		paciente.append("</h2>");
		paciente.append("<hr/>");
		writer.write(paciente.toString());
		for(Consulta c : consultas) {
			StringBuilder build = new StringBuilder();
			build.append("<span style=\"color:gray;font-weight:bold;\">");
			build.append("Consulta realizada no dia: " + c.getData().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) + " ");
			build.append("</span>");
			build.append("<span style=\"color:gray;font-weight:bold;display:inline\">");
			build.append("às: " + c.getHora().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
			build.append("</span>");
			build.append("<p style=\"color:gray;font-weight:bold;display:block; margin-bottom:5px\">");
			build.append("Peso: " + c.getPeso() + "Kg ");
			build.append("Temperatura: " + c.getTemperatura() + "ºC");
			build.append("</p>");
			if(c.getTermino() !=null) {
				build.append("<p style=\"color:gray;font-weight:bold;display:block; margin-bottom:5px\">");
				build.append("Consulta encerrada às: " + c.getTermino().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
				build.append("</p>");
			}
			if(!c.getDescricao().isEmpty()) {
				build.append("<p style=\"color:gray;font-weight:bold;display:block; margin-bottom:5px\">");
				build.append("Anotações: " + c.getDescricao());
				build.append("</p>");
			}
			if(!c.getPrescricao().isEmpty()) {
				build.append("<p style=\"color:gray;font-weight:bold;display:block; margin-bottom:5px\">");
				build.append("Prescrição: " + c.getPrescricao());
				build.append("</p>");
			}
			build.append("<div style=\"width: 100%; height: 2px; background-color: gray\" />");
			writer.write(build.toString());
		}
		writer.close();
		conveterHTMLparaPDF(arquivo, request, response);
		
	}
	@GetMapping(value ="/download/imunizacoes/paciente/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody void criarArquivoAplicacao(@PathVariable("id") Long id, RedirectAttributes attr, HttpServletRequest request, HttpServletResponse response) throws IOException, DocumentException{
		Animal animal = service.buscarPorId(id);
		List<Aplicacao> aplicacoes = aplicacaoService.buscarAplicacaoPorIdAnimal(id);
		String arquivo = "imunizacoes";
		BufferedWriter writer = new BufferedWriter(new FileWriter(file + arquivo + ".html"));
		
		StringBuilder paciente = new StringBuilder();
		paciente.append("<hr/>");
		paciente.append("<img style=\"width:200px;height:60px;\" src=\"src/main/resources/static/image/loginho.png\"/>");
		paciente.append("<h2 style=\"color:green;font-weight:bold;\">");
		paciente.append(String.format("%10s%n%n","Paciente: " + animal.getNome()));
		paciente.append("</h2>");
		paciente.append("<hr/>");
		writer.write(paciente.toString());
		for(Aplicacao a : aplicacoes) {
			StringBuilder build = new StringBuilder();
			build.append("<p style=\"color:gray;font-weight:bold;\">");
			build.append("Imunização aplicada no dia: " + a.getDataAplicacao().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) + " ");
			build.append("</p>");
			
			if(a.getDoses() == 0) {
				build.append("<p style=\"color:gray;font-weight:bold;\">");
				build.append("Dose anual");
				build.append("</p>");
			}else {
				build.append("<p style=\"color:gray;font-weight:bold\">");
				build.append(a.getDoses() + "ª dose de " + a.getVacina().getDoses());
				build.append("</p>");
			}
			
			
			build.append("<p style=\"color:gray;font-weight:bold;\">");
			build.append("Próxima aplicação estimada: " + a.getProximaAplicacao().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
			build.append("</p><br/>");
			build.append("<div style=\"width: 100%; height: 2px; background-color: gray;\">");
			build.append("</div>");
			
			writer.write(build.toString());
		}
		writer.close();
		conveterHTMLparaPDF(arquivo, request, response);
		
	}
	private void conveterHTMLparaPDF(String arquivo, HttpServletRequest request, HttpServletResponse response) throws IOException, DocumentException {
		Document document = new Document();
		PdfWriter writerPdf = PdfWriter.getInstance(document, new FileOutputStream(file + arquivo + ".pdf"));
		document.open();
		XMLWorkerHelper.getInstance().parseXHtml(writerPdf, document, new FileInputStream(file + arquivo + ".html"));
		document.close();
		
		File files = new File(file + arquivo + ".pdf");
		FileInputStream in = new FileInputStream(files);
		byte[] content = new byte[(int) files.length()];
		in.read(content);
		ServletContext sc = request.getSession().getServletContext();
		String mimetype = sc.getMimeType(files.getName());
		response.reset();
		response.setContentType(mimetype);
		response.setContentLength(content.length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + files.getName() + "\"");
		org.springframework.util.FileCopyUtils.copy(content, response.getOutputStream());
		
		
	}

	/*
	@GetMapping(value ="/download/historico/pdf")
	public String downloadEmPdf(RedirectAttributes attr) throws DocumentException, IOException {
		
		Document pdfDoc = new Document(PageSize.A4);
		PdfWriter.getInstance(pdfDoc, new FileOutputStream(file + "teste.pdf")).setPdfVersion(PdfWriter.PDF_VERSION_1_7);
		pdfDoc.open();
		
		Font myFont = new Font();
		myFont.setStyle(Font.NORMAL);
		myFont.setSize(12);
		pdfDoc.add(new Paragraph("\n"));
		
		BufferedReader br = new BufferedReader(new FileReader(file + "teste.html"));
		String strLine;
		while((strLine = br.readLine()) != null) {
			Paragraph para = new Paragraph(strLine + "\n", myFont);
			para.setAlignment(Element.ALIGN_JUSTIFIED);
			pdfDoc.add(para);
		}
		pdfDoc.close();
		br.close();
		
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file + "teste.pdf"));
		document.open();
		XMLWorkerHelper.getInstance().parseXHtml(writer, document, new FileInputStream(file + "teste.html"));
		document.close();
		attr.addFlashAttribute("sucesso", "historico criado");
		return "redirect:/pacientes/listar";
	}
	
	@GetMapping(value ="/download/historico", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody void downloadHistorico(HttpServletRequest request, HttpServletResponse response) throws IOException{	
		File files = new File(file + "teste.pdf");
		FileInputStream in = new FileInputStream(files);
		byte[] content = new byte[(int) files.length()];
		in.read(content);
		ServletContext sc = request.getSession().getServletContext();
		String mimetype = sc.getMimeType(files.getName());
		response.reset();
		response.setContentType(mimetype);
		response.setContentLength(content.length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + files.getName() + "\"");
		org.springframework.util.FileCopyUtils.copy(content, response.getOutputStream());
		
	}
	*/

	
}
