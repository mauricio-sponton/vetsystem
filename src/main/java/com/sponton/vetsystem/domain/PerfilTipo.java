package com.sponton.vetsystem.domain;

public enum PerfilTipo {
	ADMIN(1, "ADMIN"), VETERINARIO(2, "VETERINARIO"), SECRETARIA(3, "SECRETARIA");
	
	private long cod;
	private String desc;

	private PerfilTipo(long cod, String desc) {
		this.cod = cod;
		this.desc = desc;
	}

	public long getCod() {
		return cod;
	}

	public String getDesc() {
		return desc;
	}
}
