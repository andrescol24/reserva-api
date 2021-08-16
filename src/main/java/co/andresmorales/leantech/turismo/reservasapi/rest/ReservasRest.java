package co.andresmorales.leantech.turismo.reservasapi.rest;

import javax.websocket.server.PathParam;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import co.andresmorales.leantech.turismo.reservasapi.dto.Reserva;

/**
 * Rest para recibir las peticiones HTTP para las reservas
 * @author Andres Morales
 *
 */
@RestController
public class ReservasRest {
	
	/**
	 * Permite consultar una reserva
	 * @param id ID de la reserva
	 * @return 
	 */
	@GetMapping("/consultar-reserva/{id}")
	public ResponseEntity<Reserva> consultarReserva(@PathParam("id") Integer id) {
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/crear-reserva")
	public ResponseEntity<String> crearReserva() {
		return new ResponseEntity<>("Creando Reserva 5", HttpStatus.OK);
	}
	
}
