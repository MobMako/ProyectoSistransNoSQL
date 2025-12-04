
package uniandes.edu.co.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import uniandes.edu.co.demo.modelo.Servicio;

public interface ServicioRepository extends MongoRepository<Servicio, String> {

    List<Servicio> findByUsuarioCedula(String cedula);

    List<Servicio> findByConductorCedula(String cedula);

    // RFC2: Obtener el top N conductores con más servicios
    @Aggregation(pipeline = {
            "{ $group: { _id: '$conductorCedula', totalServicios: { $sum: 1 } } }",
            "{ $sort: { totalServicios: -1 } }",
            "{ $limit: ?0 }",
            "{ $lookup: { from: 'usuariosConductores', localField: '_id', foreignField: 'cedula', as: 'conductorInfo' } }",
            "{ $unwind: '$conductorInfo' }",

            "{ $project: { 'cedula': '$_id', 'nombre': '$conductorInfo.nombre', 'email': '$conductorInfo.email', 'totalServicios': '$totalServicios' } }"
    })
    List<ConductorTopDTO> findTopConductoresByServicios(int limit); // ¡Cambiamos Object[] por ConductorTopDTO!

    // RFC3: Agregación para contar servicios por tipo/nivel en un rango de fechas y
    // ciudad
    @Aggregation(pipeline = {
            "{ $match: { 'fechaInicio': { $gte: ?1, $lte: ?2 }, 'puntos.0.ciudad': ?0 } }",
            "{ $lookup: { from: 'vehiculos', localField: 'vehiculoPlaca', foreignField: 'placa', as: 'vehiculoInfo' } }",
            "{ $unwind: '$vehiculoInfo' }",
            "{ $group: { _id: { tipo: '$vehiculoInfo.tipoServicio', nivel: '$vehiculoInfo.nivel' }, total: { $sum: 1 } } }",
            "{ $project: { _id: 0, tipoServicio: '$_id.tipo', nivel: '$_id.nivel', cantidad: '$total' } }",
            "{ $sort: { cantidad: -1 } }"
    })
    List<ResumenServicioDTO> contarServiciosPorTipoEnRango(String ciudad, String fechaInicio, String fechaFin);

    // RFC3: Cuenta el total de servicios para el denominador del porcentaje
    @Query(value = "{ 'fechaInicio': { $gte: ?1, $lte: ?2 }, 'puntos.0.ciudad': ?0 }", count = true)
    Long contarTotalServiciosEnRango(String ciudad, String fechaInicio, String fechaFin);

    public static class ConductorTopDTO {
        private String cedula;
        private String nombre;
        private String email;
        private Long totalServicios;

        public ConductorTopDTO(String cedula, String nombre, String email, Long totalServicios) {
            this.cedula = cedula;
            this.nombre = nombre;
            this.email = email;
            this.totalServicios = totalServicios;
        }

        public String getCedula() {
            return cedula;
        }

        public String getNombre() {
            return nombre;
        }

        public String getEmail() {
            return email;
        }

        public Long getTotalServicios() {
            return totalServicios;
        }
    }

    public static class ResumenServicioDTO {
        private String tipoServicio;
        private String nivel;
        private Long cantidad;

        public ResumenServicioDTO(String tipoServicio, String nivel, Long cantidad) {
            this.tipoServicio = tipoServicio;
            this.nivel = nivel;
            this.cantidad = cantidad;
        }

        public String getTipoServicio() {
            return tipoServicio;
        }

        public void setTipoServicio(String tipoServicio) {
            this.tipoServicio = tipoServicio;
        }

        public String getNivel() {
            return nivel;
        }

        public void setNivel(String nivel) {
            this.nivel = nivel;
        }

        public Long getCantidad() {
            return cantidad;
        }

        public void setCantidad(Long cantidad) {
            this.cantidad = cantidad;
        }
    }
}
