package co.andresmorales.leantech.turismo.reservasapi.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
	private static final String RESERVA = "reserva";

	private ReservaRepository reservaRepository;
	private RabbitTemplate queueTemplate;
	private JavaMailSender sender;
	private TemplateEngine template;

	@Autowired
	public ReservasService(ReservaRepository reservaRepository, RabbitTemplate queueTemplate, JavaMailSender sender,
			TemplateEngine template) {
		this.reservaRepository = reservaRepository;
		this.queueTemplate = queueTemplate;
		this.template = template;
		this.sender = sender;
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
		Calendar fechaInicio = Calendar.getInstance();
		fechaInicio.setTime(reserva.getFechaIngreso());
		fechaInicio.set(Calendar.HOUR_OF_DAY, 0);
		fechaInicio.set(Calendar.MINUTE, 0);
		fechaInicio.set(Calendar.SECOND, 0);
		fechaInicio.set(Calendar.MILLISECOND, 0);
		reserva.setFechaIngreso(fechaInicio.getTime());
		
		Calendar fechaFin = Calendar.getInstance();
		fechaFin.setTime(reserva.getFechaSalida());
		fechaFin.set(Calendar.HOUR_OF_DAY, 23);
		fechaFin.set(Calendar.MINUTE, 59);
		fechaFin.set(Calendar.SECOND, 59);
		fechaFin.set(Calendar.MILLISECOND, 999);
		reserva.setFechaSalida(fechaFin.getTime());
		
		if (reserva.getFechaIngreso() == null) {
			return Optional.of("La fecha de ingreso es requerida.");
		}
		if (reserva.getFechaSalida() == null) {
			return Optional.of("La fecha de salida es requerida.");
		}
		if (reserva.getFechaIngreso().before(new Date())) {
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
	 * @throws MessagingException
	 * @throws InterruptedException
	 */
	private void enviarEmailReservaCreada(String email, ReservaModel reserva) throws MessagingException {
		MimeMessage mimeMessage = this.sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.addTo(email);
		helper.setSubject(String.format("Confirmación de Reserva #%d", reserva.getId()));

		Map<String, Object> parametros = new HashMap<>();
		Locale locale = new Locale("es", "CO");
		parametros.put(RESERVA, reserva);
		Context context = new Context(locale, parametros);
		String mensaje = this.template.process(RESERVA, context);
		helper.setText(mensaje, true);
		this.sender.send(helper.getMimeMessage());
	}

	/**
	 * Envia un email
	 * 
	 * @param email   Email del destinatario
	 * @param reserva Reserva creada
	 */
	private void enviarEmailReservaNoCreada(Reserva peticion, String mensaje) {
		try {
			MimeMessage mimeMessage = this.sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.addTo(peticion.getEmail());
			helper.setSubject("Reserva no realizada :(");

			Map<String, Object> parametros = new HashMap<>();
			Locale locale = new Locale("es", "CO");
			parametros.put(RESERVA, peticion);
			parametros.put("mensaje", mensaje);
			Context context = new Context(locale, parametros);
			String mensajeHtml = this.template.process("reserva_no_creada", context);
			helper.setText(mensajeHtml, true);
			this.sender.send(helper.getMimeMessage());
		} catch (MessagingException e) {
			log.error("Error enviando email de error", e);
		}

	}
}
