package co.andresmorales.leantech.turismo.reservasapi.dto;

import lombok.Getter;
import lombok.Setter;

public class ReservaCreada {
	
	@Getter
	@Setter
	private int id;

	public ReservaCreada(int id) {
		this.id = id;
	}
}
