package uniandes.edu.co.demo.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "servicios")
public class Servicio {
    @Id private String id;
    private String usuarioCedula, conductorCedula, vehiculoPlaca, tipoServicio, nivel;
    private double distancia, costo;
    private Date fechaInicio, fechaFin;
    
    @Data
    public class Punto {
        private double[] coordenadas;
        private String direccion, ciudad;
    }
    private List<Punto> puntos = new ArrayList<>();
    
    @Data
    public class Resena {
        private String paraCedula;
        private int rating;
        private String comentario;
    }
    private List<Resena> reseñas = new ArrayList<>();
}