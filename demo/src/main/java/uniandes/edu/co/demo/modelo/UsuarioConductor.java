package uniandes.edu.co.demo.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;


@Data
@Document(collection = "usuariosConductores")
public class UsuarioConductor {

    @Id private String id;
    private String cedula, nombre, email, celular;

}
