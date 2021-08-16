package co.andresmorales.leantech.turismo.reservasapi.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import co.andresmorales.leantech.turismo.reservasapi.dto.Reserva;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase que representa una entidad de la tabla RESERVA
 * 
 * @author Andres Morales
 *
 */
@Entity(name = "RESERVA")
public class ReservaModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	@Setter
	private int id;

	@Getter
	@Setter
	private Timestamp fechaIngreso;

	@Getter
	@Setter
	private Timestamp fechaSalida;

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

	public ReservaModel() {
	}

	/**
	 * Crea una instancia del modelo sin el ID ya que es asignado por la base de
	 * datos
	 * 
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

	@Override
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy");
		return String.format("[reserva #%d: de %s a %s para %s]", this.id, format.format(this.fechaIngreso),
				format.format(this.fechaSalida), this.titularReserva);
	}

}
