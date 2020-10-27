package com.sponton.vetsystem.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@SuppressWarnings("serial")
@Entity
@Table(name = "agendamentos")
public class Agendamento extends AbstractEntity {

	@Lob
	@Column(name = "descricao")
	private String descricao;
	
	@NotBlank(message = "Informe o tipo do evento")
	@Column(name = "tipo", nullable = false)
	private String tipo;
	
	@NotNull(message = "Informe a data de início do evento")
	@Column(name="inicio")
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDateTime inicio;
	
	@NotNull(message = "Informe a data de fim do evento")
	@Column(name="fim")
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDateTime fim;
	
	@ManyToOne
	@JoinColumn(name = "id_secretaria_fk")
	private Secretaria secretaria;
	
	@ManyToOne
	@JoinColumn(name = "id_veterinario_fk")
	private Veterinario veterinario;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public LocalDateTime getInicio() {
		return inicio;
	}

	public void setInicio(LocalDateTime inicio) {
		this.inicio = inicio;
	}

	public LocalDateTime getFim() {
		return fim;
	}

	public void setFim(LocalDateTime fim) {
		this.fim = fim;
	}

	public Secretaria getSecretaria() {
		return secretaria;
	}

	public void setSecretaria(Secretaria secretaria) {
		this.secretaria = secretaria;
	}

	public Veterinario getVeterinario() {
		return veterinario;
	}

	public void setVeterinario(Veterinario veterinario) {
		this.veterinario = veterinario;
	}
	
	
}
