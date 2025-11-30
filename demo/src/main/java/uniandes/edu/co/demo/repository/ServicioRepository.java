package uniandes.edu.co.demo.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import uniandes.edu.co.demo.modelo.Servicio;


public interface ServicioRepository extends MongoRepository<Servicio, String> {
    List<Servicio> findByUsuarioCedula(String cedula);
    List<Servicio> findByConductorCedula(String cedula);
}
