package co.andresmorales.leantech.turismo.reservasapi.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import co.andresmorales.leantech.turismo.reservasapi.dto.Reserva;
import lombok.Data;

/**
 * Clase que representa una entidad de la tabla RESERVA
 * 
 * @author Andres Morales
 *
 */
@Entity
@Data
public class ReservaModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private Timestamp fechaIngreso;
	private Timestamp fechaSalida;
	private short totalDias;
	private short numeroPersonas;
	private String titularReserva;
	private short numeroHabitaciones;
	private short numeroMenores;
	
	public ReservaModel() {	}
	
	/**
	 * Crea una instancia del modelo sin el ID ya que es asignado por la base de datos
	 * @param reserva DTO recibido
	 */
	public ReservaModel(Reserva reserva) { 
		this.fechaIngreso = new Timestamp(reserva.getFechaIngreso().getTime());
		this.fechaSalida = new Timestamp(reserva.getFechaSalida().getTime());
		this.totalDias = reserva.getTotalDias();
		this.numeroPersonas = reserva.getNumeroPersonas();
		this.titularReserva = reserva.getTitularReserva();
		this.numeroHabitaciones = reserva.getNumeroHabitaciones();
		this.numeroMenores = reserva.getNumeroMenores();
	}
}
