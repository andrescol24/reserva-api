package co.andresmorales.leantech.turismo.reservasapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.andresmorales.leantech.turismo.reservasapi.dto.Reserva;
import co.andresmorales.leantech.turismo.reservasapi.repository.ReservaRepository;

/**
 * Clase principal donde se ejecutara la lagica de negocio para crear y guardar
 * reservas
 * 
 * @author Andres Morales
 *
 */
@Service
public class ReservasService {

	private ReservaRepository reservaRepository;

	@Autowired
	public ReservasService(ReservaRepository reservaRepository) {
		this.reservaRepository = reservaRepository;
	}
	
	/**
	 * Permite crear una reserva
	 * @param reserva Reserva a crear
	 * @return Reserva creada
	 */
	public Reserva crearReserva(Reserva reserva) {
		return null;
	}
}
