package co.andresmorales.leantech.turismo.reservasapi.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;


public class Reserva {

	@Getter @Setter private Date fechaIngreso;
	@Getter @Setter private Date fechaSalida;
	@Getter @Setter private short totalDias;
	@Getter @Setter private short numeroPersonas;
	@Getter @Setter private String titularReserva;
	@Getter @Setter private short numeroHabitaciones;
	@Getter @Setter private short numeroMenores;
}
