package co.andresmorales.leantech.turismo.reservasapi.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

/**
 * Clase que representa una entidad de la tabla RESERVA
 * 
 * @author Andres Morales
 *
 */
@Entity
@Data
@SuppressWarnings("unused")
public class ReservaModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private Date fechaIngreso;
	private Date fechaSalida;
	private short totalDias;
	private short numeroPersonas;
	private String titularReserva;
	private short numeroHabitaciones;
	private short numeroMenores;
}
