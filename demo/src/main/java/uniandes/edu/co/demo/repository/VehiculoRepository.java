package uniandes.edu.co.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import uniandes.edu.co.demo.modelo.Vehiculo;

public interface VehiculoRepository extends MongoRepository<Vehiculo, String> {
    List<Vehiculo> findByConductorCedula(String cedula);
    Optional<Vehiculo> findByPlaca(String placa);
    
}
