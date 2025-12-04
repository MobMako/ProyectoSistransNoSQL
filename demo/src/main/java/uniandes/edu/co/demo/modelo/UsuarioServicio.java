package uniandes.edu.co.demo.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "usuariosServicios")
public class UsuarioServicio {
    @Id private String id;
    private String cedula, nombre, email, celular;

    private Tarjeta tarjeta;

    @Data
    public static class Tarjeta {
        private String numero, nombreTarjeta, vencimiento, codigoSeguridad;
    }
}

