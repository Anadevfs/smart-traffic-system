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
}