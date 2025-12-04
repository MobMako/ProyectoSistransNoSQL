package uniandes.edu.co.demo.modelo;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "servicios")
public class Servicio {

   @Id private String _id; // clave primaria en Mongo
    private String id; // campo lógico exigido por el schema

    private String usuarioCedula;
    private String conductorCedula;
    private String vehiculoPlaca;

    private List<Punto> puntos = new ArrayList<>();

    private double distancia;   // bsonType: double
    private int costo;          // bsonType: int

    private String fechaInicio; // guardadas como string
    private String fechaFin;

    private List<Resena> reseñas = new ArrayList<>();

    @Data
    @NoArgsConstructor
    public static class Punto {
        private double[] coordenadas; // [lat, lon]
        private String direccion;
        private String ciudad;
    }

    @Data
    @NoArgsConstructor
    public static class Resena {
        private String paraCedula;
        private int rating;
        private String comentario;
    }
}
