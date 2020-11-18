package com.sponton.vetsystem.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sponton.vetsystem.domain.Foto;
import com.sponton.vetsystem.repository.FotoRepository;

@Service
public class FotoService {

	@Autowired
	private FotoRepository fotoRepository;

	@Transactional(readOnly = false)
	public void salvarFoto(MultipartFile file, Foto foto) throws Exception {

		Path currentPath = Paths.get(".");
		Path absolutePath = currentPath.toAbsolutePath();
		foto.setPath(absolutePath + "/src/main/resources/static/uploads/");
		byte[] bytes = file.getBytes();
		Path path = Paths.get(foto.getPath() + file.getOriginalFilename());
		Files.write(path, bytes);
		fotoRepository.save(foto);
	}

	@Transactional(readOnly = true)
	public Foto buscarFotoId(Long id) {
		return fotoRepository.findById(id).get();
	}

	@Transactional(readOnly = false)
	public void salvar(Foto foto) {
		fotoRepository.save(foto);

	}

	@Transactional(readOnly = false)
	public void salvarFotos(MultipartFile[] files, Foto foto) throws IOException {
		for (int i = 0; i < files.length; i++) {
			Path currentPath = Paths.get(".");
			Path absolutePath = currentPath.toAbsolutePath();
			foto.setPath(absolutePath + "/src/main/resources/static/uploads/");
			byte[] bytes = files[i].getBytes();
			Path path = Paths.get(foto.getPath() + files[i].getOriginalFilename());
			Files.write(path, bytes);
			fotoRepository.save(foto);
		}

	}
/*
	@Transactional(readOnly = true)
	public List<Foto> buscarFotosPorId(Long id) {
		return fotoRepository.findByFotosId(id);
	}
	*/
}
