package com.sponton.vetsystem.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sponton.vetsystem.datatables.Datatables;
import com.sponton.vetsystem.datatables.DatatablesColunas;
import com.sponton.vetsystem.domain.Cliente;
import com.sponton.vetsystem.domain.Foto;
import com.sponton.vetsystem.domain.FotoInternacao;
import com.sponton.vetsystem.repository.FotoInternacaoRepository;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class FotoInternacaoService {

	@Autowired 
	private FotoInternacaoRepository repository;
	
	@Autowired
	private Datatables datatables;
	
	@Transactional(readOnly = false)
	public void salvarFotos(MultipartFile[] files, FotoInternacao foto) throws IOException {
		for (int i = 0; i < files.length; i++) {
			Path currentPath = Paths.get(".");
			Path absolutePath = currentPath.toAbsolutePath();
			foto.setPath(absolutePath + "/src/main/resources/static/uploads/");
			foto.setThumb(absolutePath + "/src/main/resources/static/uploads/thumb/");
			
			byte[] bytes = files[i].getBytes();
			Path path = Paths.get(foto.getPath() + files[i].getOriginalFilename());
			Files.write(path, bytes);
			BufferedImage original = ImageIO.read(new File(foto.getPath() + files[i].getOriginalFilename()));
			BufferedImage outputImage = resizeImage(original, 100, 100);
			ImageIO.write(outputImage, "jpg", new File(foto.getThumb() + files[i].getOriginalFilename()));
			repository.save(foto);
		}

	}

	@Transactional(readOnly = true)
	public Map<String, Object> buscarTodos(HttpServletRequest request, Long id) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.FOTOS_INTERNACOES);
		Page<FotoInternacao> page = datatables.getSearch().isEmpty() ? repository.findByInternacao(datatables.getPageable(), id)
				: repository.findByName(datatables.getSearch(), datatables.getPageable(), id);
		return datatables.getResponse(page);
	}
	
	 static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        Thumbnails.of(originalImage)
	            .size(targetWidth, targetHeight)
	            .outputFormat("JPEG")
	            .outputQuality(0.90)
	            .toOutputStream(outputStream);
	        byte[] data = outputStream.toByteArray();
	        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
	        return ImageIO.read(inputStream);
	    }

	@Transactional(readOnly = false)
	public void remover(Long id) {
		repository.deleteById(id);
		
	}

	@Transactional(readOnly = true)
	public FotoInternacao buscarPorId(Long id) {
		return repository.findById(id).get();
	}

	@Transactional(readOnly = false)
	public void salvarFoto(MultipartFile file, FotoInternacao foto) throws IOException{
		Path currentPath = Paths.get(".");
		Path absolutePath = currentPath.toAbsolutePath();
		foto.setPath(absolutePath + "/src/main/resources/static/uploads/");
		foto.setThumb(absolutePath + "/src/main/resources/static/uploads/thumb/");
		//foto.setTipo(file.getContentType());
		if(foto.getNome()!=null) {
			foto.setNome(foto.getNome());
		}
		foto.setFileName(file.getOriginalFilename());
		byte[] bytes = file.getBytes();
		Path path = Paths.get(foto.getPath() + file.getOriginalFilename());
		Files.write(path, bytes);
		BufferedImage original = ImageIO.read(new File(foto.getPath() +  file.getOriginalFilename()));
		BufferedImage outputImage = resizeImage(original, 100, 100);
		ImageIO.write(outputImage, "jpg", new File(foto.getThumb() +  file.getOriginalFilename()));
		repository.save(foto);
		
	}
	@Transactional(readOnly = false)
	public void salvar(@Valid FotoInternacao foto) {
	
		repository.save(foto);
		
	}

	@Transactional(readOnly = true)
	public List<FotoInternacao> buscarPorInternacaoId(Long id) {
		return repository.findByInternacaoId(id);
	}

	
}
