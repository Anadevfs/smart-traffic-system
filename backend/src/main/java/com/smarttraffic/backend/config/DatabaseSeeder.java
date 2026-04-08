package com.smarttraffic.backend.config;

import com.smarttraffic.backend.model.Intersection;
import com.smarttraffic.backend.model.TrafficLight;
import com.smarttraffic.backend.repository.IntersectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private IntersectionRepository intersectionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se o banco já tem dados para não duplicar toda vez que der play
        if (intersectionRepository.count() == 0) {
            System.out.println("🌱 Semeando o banco de dados com SP e Recife...");

            // Agamenon Magalhães
            Intersection recife1 = new Intersection();
            recife1.setName("Cruzamento Agamenon Magalhães");
            recife1.setCurrentCarCount(45);
            recife1.setCondition(Intersection.RoadCondition.NORMAL);
            recife1.setLatitude(-8.0527);
            recife1.setLongitude(-34.8912);
            recife1.setCity("Recife");
            recife1.setState("PE");

            // Criando e amarrando um semáforo nesta rua
            TrafficLight tl1 = new TrafficLight();
            tl1.setState(TrafficLight.LightState.RED);
            tl1.setIntersection(recife1);
            recife1.getTrafficLights().add(tl1);

            // RECIFE: Rua da Aurora
            Intersection recife2 = new Intersection();
            recife2.setName("Rua da Aurora");
            recife2.setCurrentCarCount(12);
            recife2.setCondition(Intersection.RoadCondition.NORMAL);
            recife2.setLatitude(-8.0581);
            recife2.setLongitude(-34.8814);
            recife2.setCity("Recife");
            recife2.setState("PE");

            TrafficLight tl2 = new TrafficLight();
            tl2.setState(TrafficLight.LightState.GREEN);
            tl2.setIntersection(recife2);
            recife2.getTrafficLights().add(tl2);

            // SÃO PAULO: Av. Paulista
            Intersection sp1 = new Intersection();
            sp1.setName("Av. Paulista x Consolação");
            sp1.setCurrentCarCount(80);
            sp1.setCondition(Intersection.RoadCondition.NORMAL);
            sp1.setLatitude(-23.5555);
            sp1.setLongitude(-46.6631);
            sp1.setCity("São Paulo");
            sp1.setState("SP");

            TrafficLight tl3 = new TrafficLight();
            tl3.setState(TrafficLight.LightState.RED);
            tl3.setIntersection(sp1);
            sp1.getTrafficLights().add(tl3);

            // SÃO PAULO: Marginal Tietê
            Intersection sp2 = new Intersection();
            sp2.setName("Marginal Tietê - Bandeiras");
            sp2.setCurrentCarCount(120);
            sp2.setCondition(Intersection.RoadCondition.ACCIDENT); // Acidente
            sp2.setLatitude(-23.5186);
            sp2.setLongitude(-46.6346);
            sp2.setCity("São Paulo");
            sp2.setState("SP");

            TrafficLight tl4 = new TrafficLight();
            tl4.setState(TrafficLight.LightState.RED);
            tl4.setIntersection(sp2);
            sp2.getTrafficLights().add(tl4);

            // Salva todas as ruas no banco de uma vez (o Cascade do JPA salva os semáforos junto)
            intersectionRepository.saveAll(Arrays.asList(recife1, recife2, sp1, sp2));

            System.out.println("Banco de dados populado com semáforos com sucesso!");
        } else {
            System.out.println("Banco de dados já possui informações. Seeder ignorado.");
        }
    }
}