package com.sponton.vetsystem.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sponton.vetsystem.domain.Foto;
import com.sponton.vetsystem.repository.FotoRepository;

@Service
public class FotoService {

	@Autowired
	private FotoRepository fotoRepository;

	public void salvarFoto(MultipartFile file, Foto foto) throws Exception {

		Path currentPath = Paths.get(".");
		Path absolutePath = currentPath.toAbsolutePath();
		foto.setPath(absolutePath + "/src/main/resources/static/uploads/");
		byte[] bytes = file.getBytes();
		Path path = Paths.get(foto.getPath() + file.getOriginalFilename());
		Files.write(path, bytes);
		fotoRepository.save(foto);
	}

	public Foto buscarFotoId(Long id) {
		return fotoRepository.findById(id).get();
	}
}
