package com.sponton.vetsystem.domain;

import java.time.LocalDate;
import java.time.LocalTime;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;


@SuppressWarnings("serial")
@Entity
@Table(name = "internacoes")
public class Internacao extends AbstractEntity {

	@NotBlank(message = "Informe o status da internação")
	@Column(name = "status", nullable = false)
	private String status;
	
	@Column(name = "descricao")
	private String descricao;
	
	@Column(name = "prescricao")
	private String prescricao;
	
	@Column(name="data_entrada")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate dataEntrada;
	
	@Column(name="hora_entrada")
	@DateTimeFormat(iso = ISO.TIME)
	private LocalTime horaEntrada;
	
	@Column(name="data_saida")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate dataSaida;
	
	@Column(name="hora_saida")
	@DateTimeFormat(iso = ISO.TIME)
	private LocalTime horaSaida;
	
	
	@ManyToOne
	private Animal animal;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public LocalDate getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(LocalDate dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public LocalTime getHoraEntrada() {
		return horaEntrada;
	}

	public void setHoraEntrada(LocalTime horaEntrada) {
		this.horaEntrada = horaEntrada;
	}

	public LocalDate getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(LocalDate dataSaida) {
		this.dataSaida = dataSaida;
	}

	public LocalTime getHoraSaida() {
		return horaSaida;
	}

	public void setHoraSaida(LocalTime horaSaida) {
		this.horaSaida = horaSaida;
	}

	public Animal getAnimal() {
		return animal;
	}

	public void setAnimal(Animal animal) {
		this.animal = animal;
	}
	
}
