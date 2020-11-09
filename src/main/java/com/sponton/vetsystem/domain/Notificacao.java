package com.sponton.vetsystem.domain;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@SuppressWarnings("serial")
@Entity
@Table(name = "notificacoes")
public class Notificacao extends AbstractEntity{

	@Column(name="data")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate data;
	
	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}
	
	@Column(name = "titulo")
	private String titulo;
	
	@Lob
	@Column(name = "descricao")
	private String descricao;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "secretarias_tem_notificacoes",
			joinColumns = @JoinColumn(name = "id_notificacao", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "id_secretaria", referencedColumnName = "id")
    )
	private List<Secretaria> secretarias;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "veterinarios_tem_notificacoes",
			joinColumns = @JoinColumn(name = "id_notificacao", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "id_veterinario", referencedColumnName = "id")
    )
	private List<Veterinario> veterinarios;
	/*
	@ManyToOne
	@JoinColumn(name = "id_agendamento_fk")
	private Agendamento agendamentos;
	

	public Agendamento getAgendamento() {
		return agendamentos;
	}

	public void setAgendamento(Agendamento agendamento) {
		this.agendamentos = agendamento;
	}
	*/

	public List<Veterinario> getVeterinarios() {
		return veterinarios;
	}

	public void setVeterinarios(List<Veterinario> veterinarios) {
		this.veterinarios = veterinarios;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Secretaria> getSecretarias() {
		return secretarias;
	}

	public void setSecretarias(List<Secretaria> secretarias) {
		this.secretarias = secretarias;
	}
	
}
