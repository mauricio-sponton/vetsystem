package com.sponton.vetsystem.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
@Entity
@Table(name = "racas")
public class Raca extends AbstractEntity{

	@NotBlank(message = "Informe a raça")
	@Column(name = "nome", nullable = false, unique = true)
	private String nome;
	
	@NotNull(message = "Informe a espécie")
	@ManyToOne
	@JoinColumn(name = "id_raca_fk")
	private Especie especie;

	@JsonIgnore
	@OneToMany(mappedBy = "raca")
	private List<Animal> animais;
	
	public List<Animal> getAnimais() {
		return animais;
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

	public Especie getEspecie() {
		return especie;
	}

	public void setEspecie(Especie especie) {
		this.especie = especie;
	}
	
}
