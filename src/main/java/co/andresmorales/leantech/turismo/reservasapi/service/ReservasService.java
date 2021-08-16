package co.andresmorales.leantech.turismo.reservasapi.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import co.andresmorales.leantech.turismo.reservasapi.dto.Reserva;
import co.andresmorales.leantech.turismo.reservasapi.dto.ReservaCreada;
import co.andresmorales.leantech.turismo.reservasapi.model.ReservaModel;
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
	 * 
	 * @param reserva Reserva a crear
	 * @return {@link ResponseEntity} con el ID asignado
	 */
	public ResponseEntity<ReservaCreada> crearReserva(Reserva reserva) {
		if (reserva.getFechaIngreso() == null || reserva.getFechaSalida() == null
				|| reserva.getTitularReserva() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ReservaModel modelo = new ReservaModel(reserva);
		Logger log = LoggerFactory.getLogger(getClass());
		this.reservaRepository.save(modelo);
		log.info("Se creo la reserva #{}, entre {} y {} para {}", modelo.getId(), modelo.getFechaIngreso(),
				modelo.getFechaIngreso(), modelo.getTitularReserva());
		return new ResponseEntity<>(new ReservaCreada(modelo.getId()), HttpStatus.OK);
	}

	/**
	 * Consulta una reserva por el ID
	 * 
	 * @param id Id de la reserva
	 * @return {@link ResponseEntity} con la reserva consultada. En caso de no ser
	 *         encontrada el ResponseEntity tendra un {@link HttpStatus#NOT_FOUND}
	 */
	public ResponseEntity<Reserva> consultarReserva(int id) {
		Optional<ReservaModel> reservaModel = this.reservaRepository.findById(id);
		if (reservaModel.isPresent()) {
			return new ResponseEntity<>(new Reserva(reservaModel.get()), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
