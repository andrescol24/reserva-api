package co.andresmorales.leantech.turismo.reservasapi.service;

import java.util.Optional;

import org.apache.commons.validator.routines.EmailValidator;
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
	private static final Logger log = LoggerFactory.getLogger(ReservasService.class);

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
		try {
			Optional<String> mensajeError = this.validarPeticionCreacion(reserva);
			if (mensajeError.isPresent()) {
				return new ResponseEntity<>(new ReservaCreada(mensajeError.get()), HttpStatus.BAD_REQUEST);
			} else {
				ReservaModel modelo = new ReservaModel(reserva);
				this.reservaRepository.save(modelo);
				log.info("Se creo una reserva -> {}", modelo);
				this.enviarEmailReservaCreada(reserva.getEmail(), modelo);
				return new ResponseEntity<>(new ReservaCreada(modelo.getId()), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error creando la reserva", e);
			String mensaje = "Ocurrio un error interno.";
			this.enviarEmailReservaNoCreada(reserva, mensaje);
			return new ResponseEntity<>(new ReservaCreada(mensaje), HttpStatus.INTERNAL_SERVER_ERROR);
		}
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

	/**
	 * Valida la peticion de creacion y en caso de ser erronea devolvera un mensaje
	 * 
	 * @param reserva reserva a validar
	 * @return Optional con un valor en caso de que la respuesta sea incorrecta o
	 *         vacio en caso de que sea valida
	 */
	private Optional<String> validarPeticionCreacion(Reserva reserva) {
		if (reserva.getFechaIngreso() == null) {
			return Optional.of("La fecha de ingreso es requerida.");
		}
		if (reserva.getFechaSalida() == null) {
			return Optional.of("La fecha de salida es requerida.");
		}
		if(reserva.getFechaIngreso().after(reserva.getFechaSalida())) {
			return Optional.of("La fecha de salida debe de ser posterior a la fecha de ingreso.");
		}
		if (!EmailValidator.getInstance().isValid(reserva.getEmail())) {
			return Optional.of("Se requiere un email valido para enviar la confirmación.");
		}
		if (reserva.getTitularReserva() == null || reserva.getTitularReserva().isBlank()) {
			return Optional.of("Se require el nombre del titular.");
		}
		if (reserva.getNumeroHabitaciones() > 50 || reserva.getNumeroHabitaciones() < 0) {
			return Optional.of("El número de habitaciones es invalido.");
		}
		if (reserva.getNumeroPersonas() < 0) {
			return Optional.of("El número de personas es invalido.");
		}
		if (reserva.getTotalDias() < 0) {
			return Optional.of("El número total de dias es invalido.");
		}
		return Optional.empty();
	}

	/**
	 * Envia un email
	 * 
	 * @param email   Email del destinatario
	 * @param reserva Reserva creada
	 */
	private void enviarEmailReservaCreada(String email, ReservaModel reserva) {
		log.info("EMAIL Reserva Satisfactoria {}: Su numero de reserva es #{}. ¡Lo esperamos con ansias!", email,
				reserva.getId());
	}

	/**
	 * Envia un email
	 * 
	 * @param email   Email del destinatario
	 * @param reserva Reserva creada
	 */
	private void enviarEmailReservaNoCreada(Reserva peticion, String mensaje) {
		log.info("EMAIL Reserva No Satisfactoria {}: Señor usuario, no pudimos crear su reserva: {}", peticion,
				mensaje);
	}
}
