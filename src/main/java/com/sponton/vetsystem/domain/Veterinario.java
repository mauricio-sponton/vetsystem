package com.sponton.vetsystem.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;






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
	

	@OneToOne()
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@JsonIgnore
	@OneToMany(mappedBy = "veterinario", cascade = CascadeType.REMOVE)
	private List<Consulta> consultas;
	
	@JsonIgnore
	@OneToMany(mappedBy = "veterinario", cascade = CascadeType.REMOVE)
	private List<Agendamento> agendamentos;
	
	@JsonIgnore
	@OneToMany(mappedBy = "veterinario", cascade = CascadeType.REMOVE)
	private List<CargaHoraria> horarios;
	
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

	@NotBlank(message="Informe seu cpf")
	@Column(name = "cpf", unique = true, nullable = false)
	private String cpf;
	
	@JsonIgnore
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "veterinarios_tem_notificacoes",
			joinColumns = @JoinColumn(name = "id_veterinario", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "id_notificacao", referencedColumnName = "id")
    )
	private List<Notificacao> notificacoes;
	
	public List<Notificacao> getNotificacoes() {
		return notificacoes;
	}

	public void setNotificacoes(List<Notificacao> notificacoes) {
		this.notificacoes = notificacoes;
	}

	public List<CargaHoraria> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<CargaHoraria> horarios) {
		this.horarios = horarios;
	}

	public List<Agendamento> getAgendamentos() {
		return agendamentos;
	}

	public void setAgendamentos(List<Agendamento> agendamentos) {
		this.agendamentos = agendamentos;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Foto getFoto() {
		return foto;
	}

	public void setFoto(Foto foto) {
		this.foto = foto;
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

	public List<Consulta> getConsultas() {
		return consultas;
	}

	public void setConsultas(List<Consulta> consultas) {
		this.consultas = consultas;
	}

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
