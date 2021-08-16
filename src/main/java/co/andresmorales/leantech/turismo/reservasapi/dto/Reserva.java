package co.andresmorales.leantech.turismo.reservasapi.dto;

import java.util.Date;

import co.andresmorales.leantech.turismo.reservasapi.model.ReservaModel;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO que representa una reserva realizada por un usuario
 * 
 * @author Andres Morales
 *
 */
public class Reserva {

	@Getter
	@Setter
	private int id;

	@Getter
	@Setter
	private Date fechaIngreso;

	@Getter
	@Setter
	private Date fechaSalida;

	@Getter
	@Setter
	private short totalDias;

	@Getter
	@Setter
	private short numeroPersonas;

	@Getter
	@Setter
	private String titularReserva;

	@Getter
	@Setter
	private short numeroHabitaciones;

	@Getter
	@Setter
	private short numeroMenores;

	/**
	 * Crea una instancia de la reserva obteniendo los datos del modelo
	 * 
	 * @param reservaModel modelo
	 */
	public Reserva(ReservaModel reservaModel) {
		this.id = reservaModel.getId();
		this.fechaIngreso = reservaModel.getFechaIngreso();
		this.fechaSalida = reservaModel.getFechaSalida();
		this.totalDias = reservaModel.getTotalDias();
		this.numeroPersonas = reservaModel.getNumeroPersonas();
		this.titularReserva = reservaModel.getTitularReserva();
		this.numeroHabitaciones = reservaModel.getNumeroHabitaciones();
		this.numeroMenores = reservaModel.getNumeroMenores();
	}
}
