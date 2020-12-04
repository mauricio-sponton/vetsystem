package com.sponton.vetsystem.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.NumberFormat.Style;

@SuppressWarnings("serial")
@Entity
@Table(name = "consultas")
public class Consulta extends AbstractEntity {

	@NotNull(message="Insira a data da consulta")
	@Column(name="data", nullable = false)
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate data;
	
	@NotNull(message="Insira o horário da consulta")
	@Column(name="hora", nullable = false)
	@DateTimeFormat(iso = ISO.TIME)
	private LocalTime hora;
	
	@Column(name="termino")
	@DateTimeFormat(iso = ISO.TIME)
	private LocalTime termino;
	
	
	@ManyToOne
	private Animal animal;
	
	@ManyToOne
	private Veterinario veterinario;
	
	@Lob
	@Column(name="descricao")
	private String descricao;
	
	@Lob
	@Column(name="prescricao")
	private String prescricao;
	
	@NotNull(message="Informe o peso")
	@NumberFormat(style = Style.NUMBER, pattern = "##,#")
	@Column(nullable = false, columnDefinition = "DECIMAL(7,2) DEFAULT 00.0")
	private BigDecimal peso;
	
	@NotNull(message="Informe a temperatura")
	@NumberFormat(style = Style.NUMBER, pattern = "##,#")
	@Column(nullable = false, columnDefinition = "DECIMAL(7,2) DEFAULT 00.0")
	private BigDecimal temperatura;

	
	public Veterinario getVeterinario() {
		return veterinario;
	}

	public void setVeterinario(Veterinario veterinario) {
		this.veterinario = veterinario;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public LocalTime getHora() {
		return hora;
	}

	public void setHora(LocalTime hora) {
		this.hora = hora;
	}

	public LocalTime getTermino() {
		return termino;
	}

	public void setTermino(LocalTime termino) {
		this.termino = termino;
	}

	public Animal getAnimal() {
		return animal;
	}

	public void setAnimal(Animal animal) {
		this.animal = animal;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getPrescricao() {
		return prescricao;
	}

	public void setPrescricao(String prescricao) {
		this.prescricao = prescricao;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public BigDecimal getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(BigDecimal temperatura) {
		this.temperatura = temperatura;
	}
	
	
}
