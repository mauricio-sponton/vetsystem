package com.sponton.vetsystem.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;






@SuppressWarnings("serial")
@Entity
@Table(name = "veterinarios")
public class Veterinario extends AbstractEntity{

	@NotBlank(message="Informe seu nome")
	@Column(name = "nome", nullable = false)
	private String nome;
	

	@NotNull(message="Informe o CRMV")
	@Digits(integer = 5, fraction = 0)

	@Column(name = "crmv", unique = true, nullable = false, length=5)
	private Integer crmv;
	

	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getCrmv() {
		return crmv;
	}

	public void setCrmv(Integer crmv) {
		this.crmv = crmv;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	
}
