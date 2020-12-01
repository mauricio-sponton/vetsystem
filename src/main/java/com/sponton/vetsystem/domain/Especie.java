package com.sponton.vetsystem.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;



@SuppressWarnings("serial")
@Entity
@Table(name = "especies")
public class Especie extends AbstractEntity{


	@NotBlank(message = "Informe a espécie")
	@Column(name = "nome", nullable = false, unique = true)
	private String nome;
	
	@JsonIgnore
	@OneToMany(mappedBy = "especie", cascade = CascadeType.REMOVE)
	private List<Raca> racas;
	
	@JsonIgnore
	@OneToMany(mappedBy = "especie", cascade = CascadeType.REMOVE)
	private List<Vacina> vacinas;

	@JsonIgnore
	@OneToMany(mappedBy = "especie", cascade = CascadeType.REMOVE)
	private List<Animal> animais;
	
	
	public List<Animal> getAnimais() {
		return animais;
	}

	public List<Vacina> getVacinas() {
		return vacinas;
	}

	public void setVacinas(List<Vacina> vacinas) {
		this.vacinas = vacinas;
	}

	public void setAnimais(List<Animal> animais) {
		this.animais = animais;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Raca> getRacas() {
		return racas;
	}

	public void setRacas(List<Raca> racas) {
		this.racas = racas;
	}
}
