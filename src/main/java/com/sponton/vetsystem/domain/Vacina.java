package com.sponton.vetsystem.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@SuppressWarnings("serial")
@Entity
@Table(name = "vacinas")
public class Vacina extends AbstractEntity {

	@NotBlank(message = "Informe a descrição da vacina")
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	@NotBlank(message="Informe o número de aplicações")
	@Column(name = "doses", nullable = false)
	private String doses;
	
	@NotNull(message="Informe o intervalo de dias entre aplicações")
	@Digits(integer = 5, fraction = 0)
	@Column(nullable = false)
	@Min(value= 1, message="O número de dias não pode ser menor do que 1")
	@Max(value= 365, message ="O número de dias não pode ultrapassar 365")
	private Integer intervalo;
	
	@Valid
	@ManyToOne
	@JoinColumn(name = "id_vacina_fk")
	private Especie especie;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDoses() {
		return doses;
	}

	public void setDoses(String doses) {
		this.doses = doses;
	}

	public Integer getIntervalo() {
		return intervalo;
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
