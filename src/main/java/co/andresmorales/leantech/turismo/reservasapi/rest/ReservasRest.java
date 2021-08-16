package co.andresmorales.leantech.turismo.reservasapi.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import co.andresmorales.leantech.turismo.reservasapi.dto.Reserva;
import co.andresmorales.leantech.turismo.reservasapi.dto.ReservaCreada;
import co.andresmorales.leantech.turismo.reservasapi.service.ReservasService;

/**
 * Rest para recibir las peticiones HTTP para las reservas
 * 
 * @author Andres Morales
 *
 */
@RestController
public class ReservasRest {

	private ReservasService servicio;

	@Autowired
	public ReservasRest(ReservasService servicio) {
		this.servicio = servicio;
	}

	/**
	 * Permite consultar una reserva
	 * 
	 * @param id ID de la reserva
	 * @return Reserva
	 */
	@GetMapping("/consultar-reserva/{id}")
	public ResponseEntity<Reserva> consultarReserva(@PathVariable("id") Integer id) {
		return this.servicio.consultarReserva(id);
	}

	/**
	 * Permite crear una reserva
	 * 
	 * @param reserva Reserva a crear
	 * @return Respose con el HTTP status y el ID de la reserva creada en caso de
	 *         ser creada
	 */
	@PostMapping("/crear-reserva")
	public ResponseEntity<ReservaCreada> crearReserva(@RequestBody Reserva reserva) {
		return this.servicio.crearReserva(reserva);
	}

}
