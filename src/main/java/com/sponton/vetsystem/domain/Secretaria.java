package com.sponton.vetsystem.domain;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@SuppressWarnings("serial")
@Entity
@Table(name = "secretarias")
public class Secretaria extends AbstractEntity{

	@NotBlank(message="Informe seu nome")
	@Column(name = "nome", nullable = false)
	private String nome;
	

	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "foto_id_fk")
	private Foto foto;
	
	@Column(name = "sexo", nullable = false)
	@NotBlank(message = "Informe o sexo")
	private String sexo;
	
	@NotBlank(message="Informe seu telefone")
	@Size(min=11, max=14, message="Informe um telefone válido")
	@Column(name = "telefone", nullable = false)
	private String telefone;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "secretarias_tem_notificacoes",
			joinColumns = @JoinColumn(name = "id_secretaria", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "id_notificacao", referencedColumnName = "id")
    )
	private List<Notificacao> notificacoes;

	
	public List<Notificacao> getNotificacoes() {
		return notificacoes;
	}


	public void setNotificacoes(List<Notificacao> notificacoes) {
		this.notificacoes = notificacoes;
	}


	public String getSexo() {
		return sexo;
	}


	public void setSexo(String sexo) {
		this.sexo = sexo;
	}


	public String getTelefone() {
		return telefone;
	}


	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}


	public Foto getFoto() {
		return foto;
	}


	public void setFoto(Foto foto) {
		this.foto = foto;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	
}
