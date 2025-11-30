package uniandes.edu.co.demo.modelo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "vehiculos")
public class Vehiculo {
    @Id private String id;
    private String placa, conductorCedula, tipo, marca, modelo, tipoServicio, nivel, ciudadPlaca;
    private int capacidad;
    
    @Data
    public class Disponibilidad {
        private String dia, horaInicio, horaFin, tipoServicio;
    }
    private List<Disponibilidad> disponibilidadesFijas = new ArrayList<>();
}
