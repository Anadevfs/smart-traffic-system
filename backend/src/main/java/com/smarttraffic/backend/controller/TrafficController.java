package com.smarttraffic.backend.controller;

import com.smarttraffic.backend.model.Intersection;
import com.smarttraffic.backend.model.TrafficHistory;
import com.smarttraffic.backend.service.TrafficManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/traffic")
@CrossOrigin(origins = "*") // javascript consegue acessar o sistema sem ser bloqueado
public class TrafficController {

    @Autowired
    private TrafficManagementService service;

    // traça para o front ler os dados atuais do mapa
    @GetMapping("/intersections")
    public List<Intersection> getMapData() {
        return service.getAllIntersections();
    }

    // traço para o python enviar os dados em tempo real
    @PostMapping("/{id}/update")
    public Intersection updateTraffic(@PathVariable Long id, @RequestBody Map<String, Object> payload) {

        // extrai e converte para int
        int cars = (int) payload.get("carCount");

        // Extraímos a condição (ex: "NORMAL", "ACCIDENT")
        String condition = (String) payload.getOrDefault("condition", "NORMAL");

        // Envia para o Service processar decidir a cor e fazer o save da LOG
        return service.updateTrafficVolume(id, cars, condition);
    }

    // ver o histórico de um cruzamento específico
    @GetMapping("/{id}/history")
    public List<TrafficHistory> getHistory(@PathVariable Long id) {
        return service.getHistoryByIntersection(id);
    }

    // radar para buscar cruzamentos proximos por latitude, longitude e raio
    @GetMapping("/nearby")
    public List<Intersection> getNearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double radius) {

        // chama o service para calcular a distancia e filtrar os cruzamentos
        return service.getNearbyIntersections(lat, lon, radius);
    }

    // rota para ver apenas os dados da cidade (Recife ou SP)
    @GetMapping("/city/{name}")
    public List<Intersection> getByCity(@PathVariable String name) {
        // chama o filtro de cidade no service
        return service.getIntersectionsByCity(name);
    }

    // inicia a onda verde num raio de X quilometros a partir de um cruzamento travado
    @PostMapping("/{id}/green-wave")
    public List<Intersection> activateGreenWave(
            @PathVariable Long id,
            @RequestParam(defaultValue = "2.0") double radius) {

        return service.triggerGreenWave(id, radius);
    }
}