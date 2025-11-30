package uniandes.edu.co.demo.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uniandes.edu.co.demo.modelo.Servicio;
import uniandes.edu.co.demo.modelo.Servicio.Resena;
import uniandes.edu.co.demo.modelo.UsuarioConductor;
import uniandes.edu.co.demo.modelo.UsuarioServicio;
import uniandes.edu.co.demo.modelo.Vehiculo;
import uniandes.edu.co.demo.modelo.Vehiculo.Disponibilidad;
import uniandes.edu.co.demo.repository.ServicioRepository;
import uniandes.edu.co.demo.repository.UsuarioConductorRepository;
import uniandes.edu.co.demo.repository.UsuarioServicioRepository;
import uniandes.edu.co.demo.repository.VehiculoRepository;

@RestController
@RequestMapping("/api")
public class AlpescabController {
    @Autowired UsuarioServicioRepository usuarioRepo;
    @Autowired UsuarioConductorRepository conductorRepo;
    @Autowired VehiculoRepository vehiculoRepo;
    @Autowired ServicioRepository servicioRepo;
    
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
    @PostMapping("/vehiculos")
    public Vehiculo rf3(@RequestBody Vehiculo vehiculo) {
        return vehiculoRepo.save(vehiculo);
    }
    
    // RF4: Registrar disponibilidades (embebidas en vehículo)
    @PutMapping("/vehiculos/{placa}/disponibilidades")
    public Vehiculo rf4(@PathVariable String placa, @RequestBody List<Disponibilidad> disponibilidades) {
        Vehiculo vehiculo = vehiculoRepo.findById(placa).orElseThrow();
        vehiculo.setDisponibilidadesFijas(disponibilidades);
        return vehiculoRepo.save(vehiculo);
    }
    
    // RF5: Actualizar disponibilidades
    @PutMapping("/vehiculos/{placa}/disponibilidades/{index}")
    public Vehiculo rf5(@PathVariable String placa, @PathVariable int index, @RequestBody Disponibilidad disp) {
        Vehiculo vehiculo = vehiculoRepo.findById(placa).orElseThrow();
        vehiculo.getDisponibilidadesFijas().set(index, disp);
        return vehiculoRepo.save(vehiculo);
    }

    // RF6: Solicitar servicio (simplificado)
    @PostMapping("/servicios")
    public Servicio rf6(@RequestBody Servicio servicio) {
        servicio.setFechaInicio(new Date());
        return servicioRepo.save(servicio);
    }


    // RF7: Agregar reseña al servicio
    @PutMapping("/servicios/{id}/reseñas")
    public Servicio rf7(@PathVariable String id, @RequestBody Resena reseña) {
        Servicio servicio = servicioRepo.findById(id).orElseThrow();
        servicio.getReseñas().add(reseña);
        return servicioRepo.save(servicio);
    }


}
