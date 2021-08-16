package co.andresmorales.leantech.turismo.reservasapi.dto;

import lombok.Getter;
import lombok.Setter;

public class ReservaCreada {
	
	@Getter
	@Setter
	private int id;
	
	@Getter
	@Setter
	private String mensajeError;

	public ReservaCreada(String mensajeError) {
		this.mensajeError = mensajeError;
	}
	
	public ReservaCreada(int id) {
		this.id = id;
	}
}
