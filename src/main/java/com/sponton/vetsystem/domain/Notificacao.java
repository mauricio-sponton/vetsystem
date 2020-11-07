package com.sponton.vetsystem.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "notificacoes")
public class Notificacao extends AbstractEntity{

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
