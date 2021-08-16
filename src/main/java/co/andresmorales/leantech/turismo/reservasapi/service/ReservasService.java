package co.andresmorales.leantech.turismo.reservasapi.service;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import co.andresmorales.leantech.turismo.reservasapi.config.MQReservasConfig;
import co.andresmorales.leantech.turismo.reservasapi.dto.Reserva;
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

	private static final Logger log = LoggerFactory.getLogger(ReservasService.class);

	private ReservaRepository reservaRepository;
	private RabbitTemplate queueTemplate;

	@Autowired
	public ReservasService(ReservaRepository reservaRepository, RabbitTemplate queueTemplate) {
		this.reservaRepository = reservaRepository;
		this.queueTemplate = queueTemplate;
	}

	/**
	 * Agrega la peticion de creacion de la reserva en una cola para luego ser
	 * procesada solo en caso de que la peticion sea correcta.
	 * 
	 * @param reserva Reserva a crear
	 * @return {@link ResponseEntity} Con un mensaje descriptivo:
	 *         <ul>
	 *         <li>HTTP STATUS 400: La peticion es incorrecta</li>
	 *         <li>HTTP STATUS 202: Se agrego la peticion a la cola y sera
	 *         notificado por email</li>
	 *         </ul>
	 */
	public ResponseEntity<String> crearReservaPublisher(Reserva reserva) {
		Optional<String> mensajeError = this.validarPeticionCreacion(reserva);
		if (mensajeError.isPresent()) {
			return new ResponseEntity<>(mensajeError.get(), HttpStatus.BAD_REQUEST);
		} else {
			this.queueTemplate.convertAndSend(MQReservasConfig.EXCHANGE_NAME, MQReservasConfig.ROUTING_KEY_CREAR,
					reserva);
			return new ResponseEntity<>("Creación de la reserva en proceso.", HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Obtiene los mensajes puesto en la cola
	 * {@link MQReservasConfig#QUEUE_NAME_CREAR} y los procesa
	 * 
	 * @param reserva Reserva obtenida de la cola de mensajes
	 */
	@RabbitListener(queues = MQReservasConfig.QUEUE_NAME_CREAR)
	public void crearReservaConsumer(Reserva reserva) {
		try {
			ReservaModel modelo = new ReservaModel(reserva);
			this.reservaRepository.save(modelo);
			log.info("Se creo una reserva -> {}", modelo);
			this.enviarEmailReservaCreada(reserva.getEmail(), modelo);
		} catch (Exception e) {
			log.error("Error creando la reserva", e);
			String mensaje = "Ocurrio un error interno.";
			this.enviarEmailReservaNoCreada(reserva, mensaje);
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
		if(reserva.getFechaIngreso().before(new Date())) {
			return Optional.of("La fecha de ingreso debe ser posterior a hoy.");
		}
		if (reserva.getFechaIngreso().after(reserva.getFechaSalida())) {
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
	 * @throws InterruptedException
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
