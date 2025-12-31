package com.lab.backend.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.backend.Entities.RouteEntity;
import com.lab.backend.Repository.RouteRepository;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Service;
import org.springframework.util.RouteMatcher;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class RouteServices {

    private final RouteRepository routeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RouteServices(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public RouteEntity createRoute(RouteEntity routeEntity) {
        return routeRepository.CreateRoute(routeEntity);
    }

    public List<RouteEntity> getAllRoutesByIddriverPending(Long iddriver) {
        return routeRepository.getAllRoutesByDriverPending(iddriver);
    }

    public List<RouteEntity> getAllRoutesByIddriverFinish(Long iddriver) {
        System.out.println( routeRepository.getAllRoutesByDriverFinish(iddriver));
        return routeRepository.getAllRoutesByDriverFinish(iddriver);
    }
    public RouteEntity getRouteByIdDriverAndStatus(Long id, String status) {
        System.out.println(routeRepository.getRouteByIdDriverAndStatus(id, status));
        return routeRepository.getRouteByIdDriverAndStatus(id, status);
    }

    public List<RouteEntity> findAllRoutes() {
        return routeRepository.getAllRoutes();
    }

    public void updateRoute(Long id, RouteEntity routeEntity) {
        routeRepository.updateRoute(id, routeEntity);
    }

    public void updateRouteStatus(Long id, String status) {routeRepository.updateRouteStatus(id, status);}

    public void deleteRoute(Long id) {
        routeRepository.deleteRoute(id);
    }

    public void planificarRuta(List<Long> contenedorIds, Long idDriver, Long idCentral, Long idPickUpPoint, LocalTime   startime, LocalTime endtime) {
        try {
            String contenedoresJson = objectMapper.writeValueAsString(contenedorIds);
            routeRepository.planificarRuta(contenedoresJson, idDriver, idCentral, idPickUpPoint, startime, endtime);
        } catch (Exception e) {
            System.err.println("Error en servicio planificarRuta: " + e.getMessage());
            throw new RuntimeException("Error al planificar ruta", e);
        }
    }

    public RouteEntity findRouteById(Long id) {
        return routeRepository.getRouteById(id);
    }


    public List<java.util.Map<String, Object>> findInefficientRoutes() {
        return routeRepository.findInefficientRoutes();
    }

    public List<Map<String, Object>> findDriverEfficiency() {
            return routeRepository.findDriverEfficiency();
    }

    public List<Map<String, Object>> compareWastePerformance() {
        return routeRepository.compareWastePerformance();
    }

    public void updateContainerWeight(Long routeId, double newWeight) {
        routeRepository.updateContainerWeight(routeId, newWeight);
    }

}
