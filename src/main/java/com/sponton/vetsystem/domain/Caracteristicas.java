package com.sponton.vetsystem.domain;

public enum Caracteristicas {

	Castrado("Castrado"),
	Bravo("Bravo");
	
	private String descricao;

	Caracteristicas(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
	
}
