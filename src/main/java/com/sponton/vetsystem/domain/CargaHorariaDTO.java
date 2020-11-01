package com.sponton.vetsystem.domain;

import java.util.ArrayList;
import java.util.List;



public class CargaHorariaDTO {

	
	private List<CargaHoraria> cargas;
	
	public CargaHorariaDTO() {
		this.cargas = new ArrayList<>();
	}
	public CargaHorariaDTO(List<CargaHoraria> cargas) {
		this.cargas = cargas;
	}
	public void addCarga(CargaHoraria cargaHoraria) {
		this.cargas.add(cargaHoraria);
	}

	public List<CargaHoraria> getCargas() {
		return cargas;
	}


	public void setCargas(List<CargaHoraria> cargas) {
		this.cargas = cargas;
	}

	
	
}
