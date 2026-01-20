package com.lab.backend.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.backend.DTO.ContainerDTO;
import com.lab.backend.DTO.RouteRequestDTO;
import com.lab.backend.Entities.RouteEntity;
import com.lab.backend.Repository.RouteRepository;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Service;
import org.springframework.util.RouteMatcher;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RouteServices {

    private final RouteRepository routeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RouteServices(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public RouteEntity createRoute(RouteRequestDTO dto) {
        RouteEntity entity = new RouteEntity();

        // 1. Mapeo de campos básicos
        entity.setId_driver(dto.getIdDriver());
        entity.setId_central(dto.getIdCentral());
        entity.setId_central_finish(dto.getIdCentralFinish());

        // 2. Manejo de Fecha y Horas con validación de segundos
        try {
            entity.setDate_(java.sql.Date.valueOf(dto.getDate()));

            // Aseguramos formato HH:mm:ss para java.sql.Time
            String startStr = dto.getStartTime().length() == 5 ? dto.getStartTime() + ":00" : dto.getStartTime();
            String endStr = dto.getEndTime().length() == 5 ? dto.getEndTime() + ":00" : dto.getEndTime();

            entity.setStart_time(java.sql.Time.valueOf(startStr));
            entity.setEnd_time(java.sql.Time.valueOf(endStr));
        } catch (Exception e) {
            throw new RuntimeException("Error en el formato de fecha u hora: " + e.getMessage());
        }

        entity.setRoute_status("Pendiente");

        // 3. GENERACIÓN DE PUNTOS PARA ST_MakeLine
        List<String> puntosWkt = new ArrayList<>();

        if (dto.getContenedores() != null && !dto.getContenedores().isEmpty()) {
            for (ContainerDTO c : dto.getContenedores()) {
                // Verificación de coordenadas no nulas
                if (c.getLongitude() != null && c.getLatitude() != null) {
                    // Formato exacto requerido para ST_GeomFromText en la subconsulta
                    puntosWkt.add("POINT(" + c.getLongitude() + " " + c.getLatitude() + ")");
                }
            }
        }

        // 4. VALIDACIÓN CRÍTICA: ST_MakeLine requiere al menos un punto para no fallar
        if (puntosWkt.isEmpty()) {
            throw new RuntimeException("No se puede crear una ruta sin contenedores con coordenadas válidas.");
        }

        // 5. LLAMADA AL REPOSITORIO
        // Se pasan los puntos como lista; el Repositorio los convertirá en String[] o String unido
        RouteEntity savedRoute = routeRepository.CreateRoute(entity, puntosWkt);

        // 6. VINCULACIÓN INTERMEDIA Y ACTUALIZACIÓN DE ESTADO
        // Solo si la ruta se guardó correctamente en la tabla 'route'
        if (savedRoute != null && savedRoute.getId() != null) {
            routeRepository.linkContainersToRoute(savedRoute.getId(), dto.getContenedores());
        }

        return savedRoute;
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
        routeRepository.deleteRouteAndReleaseContainers(id);
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

    public Double getRouteDistance(Long id) {
        return routeRepository.calculateRouteDistance(id);
    }

}