package com.sponton.vetsystem.domain;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@SuppressWarnings("serial")
@Entity
@Table(name = "cargaHoraria")
public class CargaHoraria extends AbstractEntity{
	
    public CargaHoraria() {
    	
    }
	

	@Column(name = "dia")
	private int diaDaSemana;
	
	@DateTimeFormat(iso = ISO.TIME)
	@Column(name = "inicio")
	private LocalTime inicio;
	
	@DateTimeFormat(iso = ISO.TIME)
	@Column(name = "fim")
	private LocalTime fim;
	
	@ManyToOne
	private Veterinario veterinario;

	@Column(name = "ativo", columnDefinition = "TINYINT(1)")
	private boolean ativo;
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public int getDiaDaSemana() {
		return diaDaSemana;
	}

	public void setDiaDaSemana(int diaDaSemana) {
		this.diaDaSemana = diaDaSemana;
	}

	public LocalTime getInicio() {
		return inicio;
	}

	public void setInicio(LocalTime inicio) {
		this.inicio = inicio;
	}

	public LocalTime getFim() {
		return fim;
	}

	public void setFim(LocalTime fim) {
		this.fim = fim;
	}

	public Veterinario getVeterinario() {
		return veterinario;
	}

	public void setVeterinario(Veterinario veterinario) {
		this.veterinario = veterinario;
	}
	
	
}
