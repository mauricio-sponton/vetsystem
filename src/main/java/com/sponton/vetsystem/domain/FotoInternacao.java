package com.sponton.vetsystem.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@SuppressWarnings("serial")
@Entity
@Table(name = "fotos_internacao")
public class FotoInternacao extends AbstractEntity {

	@Column(name="path")
	private String path;
	
	@Column(name="thumb")
	private String thumb;
	
	@Column(name="titulo")
	private String fileName;
	
	@Lob
	@Column(name="descricao")
	private String descricao;
	
	@Column(name="data")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate data;
	
	@ManyToOne
	@JoinColumn(name = "id_internacao_fk")
	private Internacao internacao;

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public Internacao getInternacao() {
		return internacao;
	}

	public void setInternacao(Internacao internacao) {
		this.internacao = internacao;
	}
	
	
	
}
