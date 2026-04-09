package com.smarttraffic.backend.service;

import com.smarttraffic.backend.model.Intersection;
import com.smarttraffic.backend.model.TrafficHistory;
import com.smarttraffic.backend.model.TrafficLight;
import com.smarttraffic.backend.repository.IntersectionRepository;
import com.smarttraffic.backend.repository.TrafficHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate; // websocket
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrafficManagementService {

    @Autowired
    private IntersectionRepository intersectionRepository;

    @Autowired
    private TrafficHistoryRepository historyRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<Intersection> getAllIntersections() {
        return intersectionRepository.findAll();
    }

    public Intersection updateTrafficVolume(Long intersectionId, int newCarCount, String conditionStr) {
        Optional<Intersection> optionalIntersection = intersectionRepository.findById(intersectionId);

        if (optionalIntersection.isPresent()) {
            Intersection intersection = optionalIntersection.get();

            intersection.setCurrentCarCount(newCarCount);

            Intersection.RoadCondition condition = Intersection.RoadCondition.valueOf(conditionStr);
            intersection.setCondition(condition);

            // Calcula o tempo sugerido antes de salvar
            int tempoSugerido;
            if (newCarCount > 25 || condition == Intersection.RoadCondition.ACCIDENT) {
                tempoSugerido = 60; // Trânsito pesado ou acidente
            } else if (newCarCount > 10) {
                tempoSugerido = 45; // Fluxo moderado
            } else {
                tempoSugerido = 30; // Fluxo leve
            }

            if (intersection.getTrafficLights() != null) {
                for (TrafficLight light : intersection.getTrafficLights()) {

                    if (condition == Intersection.RoadCondition.ACCIDENT) {
                        light.setState(TrafficLight.LightState.RED);
                    }
                    else if (condition == Intersection.RoadCondition.HEAVY_RAIN) {
                        if (newCarCount > 35) {
                            light.setState(TrafficLight.LightState.GREEN);
                        } else if (newCarCount > 20) {
                            light.setState(TrafficLight.LightState.YELLOW);
                        } else {
                            light.setState(TrafficLight.LightState.RED);
                        }
                    }
                    else {
                        if (newCarCount > 25) {
                            light.setState(TrafficLight.LightState.GREEN);
                        } else if (newCarCount > 10) {
                            light.setState(TrafficLight.LightState.YELLOW);
                        } else {
                            light.setState(TrafficLight.LightState.RED);
                        }
                    }
                }
            }

            // Salva no histórico
            TrafficHistory historyEntry = new TrafficHistory(intersection, newCarCount, conditionStr, tempoSugerido);
            historyRepository.save(historyEntry);

            // Salva o estado atual do cruzamento
            Intersection savedIntersection = intersectionRepository.save(intersection);

            // Envia o objeto atualizado para o canal "/topic/traffic"
            // Vai receber o JSON na hora
            messagingTemplate.convertAndSend("/topic/traffic", savedIntersection);

            return savedIntersection;
        }

        return null;
    }

    public List<TrafficHistory> getHistoryByIntersection(Long id) {
        return historyRepository.findByIntersectionIdOrderByTimestampDesc(id);
    }

    // Radar geográfico
    // Calculo de distância entre dois pontos (Haversine)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raio da Terra em km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Busca cruzamentos próximos dentro de um raio (km)
    public List<Intersection> getNearbyIntersections(double targetLat, double targetLon, double radiusInKm) {
        List<Intersection> allIntersections = intersectionRepository.findAll();

        return allIntersections.stream()
                .filter(intersection -> calculateDistance(
                        targetLat, targetLon,
                        intersection.getLatitude(), intersection.getLongitude()
                ) <= radiusInKm)
                .toList();
    }

    // Busca cruzamentos filtrando por uma cidade específica para a prefeitura
    public List<Intersection> getIntersectionsByCity(String cityName) {
        List<Intersection> all = intersectionRepository.findAll();
        return all.stream()
                .filter(i -> i.getCity() != null && i.getCity().equalsIgnoreCase(cityName))
                .toList();
    }

    // ONDA VERDE
    // Força o sinal verde em um raio específico para desafogar o trânsito
    public List<Intersection> triggerGreenWave(Long sourceIntersectionId, double radiusInKm) {
        Optional<Intersection> sourceOpt = intersectionRepository.findById(sourceIntersectionId);

        if (sourceOpt.isPresent()) {
            Intersection source = sourceOpt.get();

            // Usa o Radar para achar quem está perto
            List<Intersection> nearbyIntersections = getNearbyIntersections(
                    source.getLatitude(), source.getLongitude(), radiusInKm);

            // Abre o semáforo de todo mundo da região
            for (Intersection inter : nearbyIntersections) {
                if (inter.getTrafficLights() != null) {
                    for (TrafficLight light : inter.getTrafficLights()) {
                        light.setState(TrafficLight.LightState.GREEN);
                    }
                }
                // Salva no banco e avisa o front-end via WebSocket na mesma hora
                Intersection saved = intersectionRepository.save(inter);
                messagingTemplate.convertAndSend("/topic/traffic", saved);
            }

            return nearbyIntersections; // Retorna quem foi afetado pela onda verde
        }
        return null;
    }
}