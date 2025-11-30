package uniandes.edu.co.demo.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import uniandes.edu.co.demo.modelo.UsuarioConductor;

public interface UsuarioConductorRepository extends MongoRepository<UsuarioConductor, String> {
    Optional<UsuarioConductor> findByCedula(String cedula);
}