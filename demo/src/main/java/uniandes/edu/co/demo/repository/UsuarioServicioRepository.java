package uniandes.edu.co.demo.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import uniandes.edu.co.demo.modelo.UsuarioServicio;

public interface UsuarioServicioRepository extends MongoRepository<UsuarioServicio, String> {
    Optional<UsuarioServicio> findByCedula(String cedula);
    boolean existsByCedula(String cedula); 
}