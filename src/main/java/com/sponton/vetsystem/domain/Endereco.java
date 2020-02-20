package com.sponton.vetsystem.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;



@SuppressWarnings("serial")
@Entity
@Table(name = "enderecos")
public class Endereco extends AbstractEntity {
	
	@NotBlank(message="Informe seu endereço")
	@Column(nullable = false)
	private String logradouro;
	
	@NotBlank(message="Informe seu bairro")
	@Column(nullable = false)
	private String bairro;
	
	@NotBlank(message="Informe sua cidade")
	@Column(nullable = false)
	private String cidade;
	
	@NotNull(message="Selecione um estado")
	@Column(nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	private UF uf;
	
	@NotBlank(message="Informe seu cep")
	@Column(nullable = false, length = 9)
	private String cep;
	
	@NotNull(message="Informe o número de sua residência")
	@Digits(integer = 5, fraction = 0)
	@Column(nullable = false, length = 5)
	private Integer numero;
	
	@Size(max = 255)
	private String complemento;

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	
	

}
