package com.sponton.vetsystem.domain;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
@Entity
@Table(name = "animais")
public class Animal extends AbstractEntity {

	@NotBlank(message = "Informe o nome")
	@Column(name = "nome", nullable = false)
	private String nome;
	
	@NotNull(message = "Informe uma data de nascimento")
	@PastOrPresent(message = "Informe uma data válida")
	@Column(name="data_nasc")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate dataNascimento;
	
	@NotNull(message = "Informe o dono")
	@ManyToOne
	@JoinColumn(name = "id_animal_fk")
	private Cliente cliente;
	
	@Valid
	@ManyToOne
	@JoinColumn(name = "id_animalEspecie_fk")
	private Especie especie;
	
	
	@Valid
	@ManyToOne
	@JoinColumn(name = "id_animalRaca_fk")
	private Raca raca;
	
	@Column(name = "sexo", nullable = false)
	@NotBlank(message = "Informe o sexo")
	private String sexo;

	@JsonIgnore
	@OneToMany(mappedBy = "animal", cascade = CascadeType.REMOVE)
	private List<HistoricoAnimal> historico;
	
	@JsonIgnore
	@OneToMany(mappedBy = "animal", cascade = CascadeType.REMOVE)
	private List<Internacao> internacao;
	
	@JsonIgnore
	@OneToMany(mappedBy = "animal", cascade = CascadeType.REMOVE)
	private List<Consulta> consultas;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "foto_id_fk")
	private Foto foto;
	
	@Column(name = "alergias")
	private String alergias;

	@Column(name = "status")
	private String status;
	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Internacao> getInternacao() {
		return internacao;
	}

	public void setInternacao(List<Internacao> internacao) {
		this.internacao = internacao;
	}

	public String getAlergias() {
		return alergias;
	}

	public void setAlergias(String alergias) {
		this.alergias = alergias;
	}

	public Foto getFoto() {
		return foto;
	}

	public void setFoto(Foto foto) {
		this.foto = foto;
	}

	public List<Consulta> getConsultas() {
		return consultas;
	}

	public void setConsultas(List<Consulta> consultas) {
		this.consultas = consultas;
	}

	public List<HistoricoAnimal> getHistorico() {
		return historico;
	}

	public void setHistorico(List<HistoricoAnimal> historico) {
		this.historico = historico;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Especie getEspecie() {
		return especie;
	}

	public void setEspecie(Especie especie) {
		this.especie = especie;
	}

	public Raca getRaca() {
		return raca;
	}

	public void setRaca(Raca raca) {
		this.raca = raca;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}


	
	
}
