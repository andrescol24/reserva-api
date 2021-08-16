package co.andresmorales.leantech.turismo.reservasapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.andresmorales.leantech.turismo.reservasapi.model.ReservaModel;

/**
 * Permite realizar operaciones sobre la tabla RESERVA
 * 
 * @author Andres Morales
 *
 */
public interface ReservaRepository extends JpaRepository<ReservaModel, Integer> {

}
