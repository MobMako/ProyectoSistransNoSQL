package uniandes.edu.co.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import uniandes.edu.co.demo.modelo.Servicio;
import uniandes.edu.co.demo.modelo.UsuarioConductor;
import uniandes.edu.co.demo.modelo.UsuarioServicio;
import uniandes.edu.co.demo.modelo.Vehiculo;
import uniandes.edu.co.demo.modelo.Vehiculo.Disponibilidad;
import uniandes.edu.co.demo.repository.ServicioRepository;
import uniandes.edu.co.demo.repository.ServicioRepository.ConductorTopDTO;
import uniandes.edu.co.demo.repository.ServicioRepository.ResumenServicioDTO;
import uniandes.edu.co.demo.repository.UsuarioConductorRepository;
import uniandes.edu.co.demo.repository.UsuarioServicioRepository;
import uniandes.edu.co.demo.repository.VehiculoRepository;

@RestController
@RequestMapping("/api")
public class AlpescabController {
    @Autowired
    UsuarioServicioRepository usuarioRepo;
    @Autowired
    UsuarioConductorRepository conductorRepo;
    @Autowired
    VehiculoRepository vehiculoRepo;
    @Autowired
    ServicioRepository servicioRepo;

    // RF1: Registrar usuario servicio
    @PostMapping("/usuarios-servicio")
    public UsuarioServicio rf1(@RequestBody UsuarioServicio usuario) {
        return usuarioRepo.save(usuario);
    }

    // RF2: Registrar usuario conductor
    @PostMapping("/usuarios-conductor")
    public UsuarioConductor rf2(@RequestBody UsuarioConductor conductor) {
        return conductorRepo.save(conductor);
    }

    // RF3: Registrar vehículo
    @PostMapping("/vehiculos/{cedulaConductor}")
    public Vehiculo rf3(@PathVariable String cedulaConductor, @RequestBody Vehiculo vehiculo) {
        // Asignar la cedula recibida en la URL al vehículo antes de guardar
        vehiculo.setConductorCedula(cedulaConductor);
        return vehiculoRepo.save(vehiculo);
    }

    // RF4: Registrar disponibilidades
    @PutMapping("/vehiculos/{placa}/disponibilidades")
    public Vehiculo rf4(@PathVariable String placa, @RequestBody List<Disponibilidad> disponibilidades) {
        Optional<Vehiculo> optVehiculo = vehiculoRepo.findByPlaca(placa);
        Vehiculo vehiculo = optVehiculo.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado con placa: " + placa));
        vehiculo.setDisponibilidadesFijas(disponibilidades);
        return vehiculoRepo.save(vehiculo);
    }

    // RF5: Actualizar disponibilidades
    @PutMapping("/vehiculos/{placa}/disponibilidades/{index}")
    public Vehiculo rf5(@PathVariable String placa, @PathVariable int index, @RequestBody Disponibilidad disp) {
        Vehiculo vehiculo = vehiculoRepo.findByPlaca(placa).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado con placa: " + placa));
        vehiculo.getDisponibilidadesFijas().set(index, disp);
        return vehiculoRepo.save(vehiculo);
    }

    // RF6: Solicitar servicio
    @PostMapping("/usuarios-servicio/{cedula}/servicios")
    public ResponseEntity<?> rf6(@PathVariable String cedula,
            @RequestBody List<Servicio.Punto> puntos) {

        if (!usuarioRepo.existsByCedula(cedula)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario de servicios no encontrado: " + cedula);
        }

        if (puntos == null || puntos.size() < 2) {
            return ResponseEntity.badRequest()
                    .body("Se requieren al menos punto de inicio y punto de llegada");
        }

        Servicio.Punto origen = puntos.get(0);
        Servicio.Punto destino = puntos.get(1);

        Vehiculo vehiculo = vehiculoRepo.findAll().stream()
                .findFirst()
                .orElse(null);

        if (vehiculo == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("No hay vehículos disponibles");
        }

        double distancia = 10.0;
        if (origen.getCoordenadas() != null && destino.getCoordenadas() != null
                && origen.getCoordenadas().length == 2 && destino.getCoordenadas().length == 2) {
            distancia = calcularDistancia(origen.getCoordenadas(), destino.getCoordenadas());
        }

        int costo = (int) Math.round(distancia * 3000); // 3000 por km se ajusta como se quiera

        Servicio servicio = new Servicio();
        String genId = "SERV" + System.currentTimeMillis();
        servicio.set_id(genId);
        servicio.setId(genId);
        servicio.setUsuarioCedula(cedula);
        servicio.setConductorCedula(vehiculo.getConductorCedula());
        servicio.setVehiculoPlaca(vehiculo.getPlaca());
        servicio.setPuntos(puntos);
        servicio.setDistancia(distancia);
        servicio.setCosto(costo);
        servicio.setFechaInicio(LocalDateTime.now().toString());
        servicio.setFechaFin("");

        Servicio guardado = servicioRepo.save(servicio);

        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    private double calcularDistancia(double[] p1, double[] p2) {
        double lat1 = Math.toRadians(p1[0]);
        double lon1 = Math.toRadians(p1[1]);
        double lat2 = Math.toRadians(p2[0]);
        double lon2 = Math.toRadians(p2[1]);

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c; // kilómetros
    }

    // RF7: Registrar fin de viaje (servicio prestado)
    @PutMapping("/servicios/{id}/finalizar")
    public ResponseEntity<?> rf7FinalizarServicio(@PathVariable String id) {

        Optional<Servicio> opt = servicioRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Servicio no encontrado: " + id);
        }

        Servicio servicio = opt.get();

        servicio.setFechaFin(LocalDateTime.now().toString());

        Optional<Vehiculo> optVehiculo = vehiculoRepo.findById(servicio.getVehiculoPlaca());
        if (optVehiculo.isPresent()) {
            Vehiculo vehiculo = optVehiculo.get();
        }

        Servicio cerrado = servicioRepo.save(servicio);

        return ResponseEntity.ok(cerrado);
    }

    // RFC1: Histórico de servicios por usuario
    @GetMapping("/usuarios-servicio/{cedula}/servicios")
    public List<Servicio> rfc1(@PathVariable String cedula) {
        if (!usuarioRepo.existsByCedula(cedula)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Usuario de servicios no encontrado con cédula: " + cedula);
        }
        return servicioRepo.findByUsuarioCedula(cedula);
    }

    // RFC2: Top 20 Conductores con más servicios
    @GetMapping("/conductores/top-servicios")
    public ResponseEntity<List<ConductorTopDTO>> rfc2() {
        int limit = 20;
        List<ConductorTopDTO> resultados = servicioRepo.findTopConductoresByServicios(limit);

        if (resultados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resultados);
    }

    // RFC3: Utilización de servicios en ciudad y rango de fechas
    @GetMapping("/servicios/utilizacion-ciudad")
    public ResponseEntity<List<UtilizacionServicioDTO>> rfc3(
            @RequestParam String ciudad,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {

        List<ResumenServicioDTO> conteosPorTipo = servicioRepo.contarServiciosPorTipoEnRango(ciudad, fechaInicio,
                fechaFin);

        Long totalServicios = servicioRepo.contarTotalServiciosEnRango(ciudad, fechaInicio, fechaFin);

        if (totalServicios == 0) {
            return ResponseEntity.noContent().build();
        }

        List<UtilizacionServicioDTO> resultados = conteosPorTipo.stream()
                .map(dto -> {
                    String tipoServicio = dto.getTipoServicio();
                    String nivel = dto.getNivel();
                    Long cantidad = dto.getCantidad();
                    double porcentaje = (double) cantidad * 100 / totalServicios;

                    return new UtilizacionServicioDTO(tipoServicio, nivel, cantidad, porcentaje);
                })
                .sorted((a, b) -> Long.compare(b.getCantidad(), a.getCantidad()))
                .toList();

        return ResponseEntity.ok(resultados);
    }

    public static class UtilizacionServicioDTO {
        private String tipoServicio;
        private String nivel;
        private Long cantidad;
        private double porcentaje;

        public UtilizacionServicioDTO(String tipoServicio, String nivel, Long cantidad, double porcentaje) {
            this.tipoServicio = tipoServicio;
            this.nivel = nivel;
            this.cantidad = cantidad;
            this.porcentaje = porcentaje;
        }

        public String getTipoServicio() {
            return tipoServicio;
        }

        public String getNivel() {
            return nivel;
        }

        public Long getCantidad() {
            return cantidad;
        }

        public double getPorcentaje() {
            return porcentaje;
        }
    }

}
