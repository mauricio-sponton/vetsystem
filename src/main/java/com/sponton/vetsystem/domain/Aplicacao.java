package com.sponton.vetsystem.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@SuppressWarnings("serial")
@Entity
@Table(name = "aplicacoes")
public class Aplicacao extends AbstractEntity{
	
	@NotNull(message = "Informe a data da aplicação da vacina")
	@Column(name="data_aplicacao")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate dataAplicacao;
	
	@Valid
	@ManyToOne
	@JoinColumn(name = "id_vacina_fk")
	private Vacina vacina;
	
	
	@JoinColumn(name = "id_animal_fk")
	@ManyToOne
	private Animal animal;

	public Animal getAnimal() {
		return animal;
	}

	public void setAnimal(Animal animal) {
		this.animal = animal;
	}

	public LocalDate getDataAplicacao() {
		return dataAplicacao;
	}

	public void setDataAplicacao(LocalDate dataAplicacao) {
		this.dataAplicacao = dataAplicacao;
	}

	public Vacina getVacina() {
		return vacina;
	}

	public void setVacina(Vacina vacina) {
		this.vacina = vacina;
	}
	
	

}
