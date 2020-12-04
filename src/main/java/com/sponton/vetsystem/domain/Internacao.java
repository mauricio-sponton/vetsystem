package com.sponton.vetsystem.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.NumberFormat.Style;

import com.fasterxml.jackson.annotation.JsonIgnore;


@SuppressWarnings("serial")
@Entity
@Table(name = "internacoes")
public class Internacao extends AbstractEntity {

	@NotBlank(message = "Informe o status da internação")
	@Column(name = "status")
	private String status;
	
	@Column(name = "descricao")
	private String descricao;
	
	@Column(name = "prescricao")
	private String prescricao;
	
	@NotNull(message="Insira a data de entrada do paciente na internação")
	@Column(name="data_entrada", nullable = false)
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate dataEntrada;
	
	@NotNull(message="Insira o horário de entrada do paciente na internação")
	@Column(name="hora_entrada", nullable = false)
	@DateTimeFormat(iso = ISO.TIME)
	private LocalTime horaEntrada;
	
	@Column(name="data_saida")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate dataSaida;
	
	@Column(name="hora_saida")
	@DateTimeFormat(iso = ISO.TIME)
	private LocalTime horaSaida;
	
	@NotNull(message="Informe o peso")
	@NumberFormat(style = Style.NUMBER, pattern = "##,#")
	@Column(nullable = false, columnDefinition = "DECIMAL(7,2) DEFAULT 00.0")
	private BigDecimal peso;
	
	@NotNull(message="Informe a temperatura")
	@NumberFormat(style = Style.NUMBER, pattern = "##,#")
	@Column(nullable = false, columnDefinition = "DECIMAL(7,2) DEFAULT 00.0")
	private BigDecimal temperatura; 
	
	@ManyToOne
	private Animal animal;
	
	@JsonIgnore
	@OneToMany(mappedBy = "internacao", cascade = CascadeType.REMOVE)
	private List<FotoInternacao> fotos;
	
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

	public List<FotoInternacao> getFotos() {
		return fotos;
	}

	public void setFotos(List<FotoInternacao> fotos) {
		this.fotos = fotos;
	}

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
