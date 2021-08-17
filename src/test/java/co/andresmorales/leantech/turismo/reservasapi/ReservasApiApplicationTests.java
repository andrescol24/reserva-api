package co.andresmorales.leantech.turismo.reservasapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;

import co.andresmorales.leantech.turismo.reservasapi.dto.Reserva;
import co.andresmorales.leantech.turismo.reservasapi.service.ReservasService;

@SpringBootTest
class ReservasApiApplicationTests {
	
	@Autowired
	private ReservasService service;
	
	@MockBean
	private RabbitTemplate template;
	
	@MockBean
	private JavaMailSender sender;
	
	@Bean
    ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(new MockConnectionFactory());
    }

	@Test
	void testCrearReservaExitosa() {
		// ANTES
		Reserva reserva = new Reserva();
		reserva.setEmail("pepito@hotmail.com");
		Calendar fechaIngreso = Calendar.getInstance();
		fechaIngreso.add(Calendar.DAY_OF_YEAR, 1);
		reserva.setFechaIngreso(fechaIngreso.getTime());
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 3);
		reserva.setFechaSalida(calendar.getTime());
		reserva.setNumeroHabitaciones((short)1);
		reserva.setNumeroPersonas((short)2);
		reserva.setTitularReserva("Andres Morales");
		reserva.setTotalDias((short)3);
		
		// ACTUAR
		var respuesta = this.service.crearReservaPublisher(reserva);
		
		// ASERTAR
		assertEquals(HttpStatus.ACCEPTED, respuesta.getStatusCode());
	}
	
	@Test
	void testCrearReservaEmailErroneo() {
		// ANTES
		Reserva reserva = new Reserva();
		reserva.setEmail("pepitohotmail.com");
		reserva.setFechaIngreso(new Date());
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 3);
		reserva.setFechaSalida(calendar.getTime());
		reserva.setNumeroHabitaciones((short)1);
		reserva.setNumeroPersonas((short)2);
		reserva.setTitularReserva("Andres Morales");
		reserva.setTotalDias((short)3);
		
		// ACTUAR
		var respuesta = this.service.crearReservaPublisher(reserva);
		
		// ASERTAR
		assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
	}
	
	

}
