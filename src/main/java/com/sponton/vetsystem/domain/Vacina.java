package com.sponton.vetsystem.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
@Entity
@Table(name = "vacinas")
public class Vacina extends AbstractEntity {

	@NotBlank(message = "Informe a descrição da vacina")
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	
	@NotNull(message="Informe o intervalo de dias entre aplicações")
	@Digits(integer = 5, fraction = 0)
	@Column(name = "doses", nullable = false)
	private Integer doses;
	
	@NotNull(message="Informe o intervalo de dias entre aplicações")
	@Digits(integer = 5, fraction = 0)
	@Column(nullable = false)
	@Min(value= 1, message="O número de dias não pode ser menor do que 1")
	@Max(value= 365, message ="O número de dias não pode ultrapassar 365")
	private Integer intervalo;
	
	@Valid
	@ManyToOne
	@JoinColumn(name = "id_especie_fk")
	private Especie especie;
	
	@JsonIgnore
	@OneToMany(mappedBy = "vacina", cascade = CascadeType.REMOVE)
	private List<Aplicacao> aplicacoes;

	public List<Aplicacao> getAplicacoes() {
		return aplicacoes;
	}

	public void setAplicacoes(List<Aplicacao> aplicacoes) {
		this.aplicacoes = aplicacoes;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
/*
	public String getDoses() {
		return doses;
	}

	public void setDoses(String doses) {
		this.doses = doses;
	}
*/
	public Integer getIntervalo() {
		return intervalo;
	}

	public Integer getDoses() {
		return doses;
	}

	public void setDoses(Integer doses) {
		this.doses = doses;
	}

	public void setIntervalo(Integer intervalo) {
		this.intervalo = intervalo;
	}

	public Especie getEspecie() {
		return especie;
	}

	public void setEspecie(Especie especie) {
		this.especie = especie;
	}
	
	
}
