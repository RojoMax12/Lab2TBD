package com.lab.backend.Controller;

import com.lab.backend.DTO.ContainerDTO;
import com.lab.backend.DTO.RouteRequestDTO;
import com.lab.backend.Entities.RouteEntity;
import com.lab.backend.Services.RouteServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/route")
public class RouteController {

    private final RouteServices routeService;

    public RouteController(RouteServices routeService) {
        this.routeService = routeService;
    }

    public static class PlanificarRutaRequest {
        public List<ContainerDTO> contenedores;
        public Long idDriver;
        public Long idCentral;
        public Long idCentralFinish;
        public String date;
        public LocalTime startTime;
        public LocalTime endTime;
    }

    @PostMapping("/planroute")
    public ResponseEntity<?> planificarRuta(@RequestBody RouteRequestDTO request) {
        try {
            // Validación de seguridad para PostGIS
            if (request.getContenedores() == null || request.getContenedores().size() < 2) {
                return ResponseEntity.badRequest().body("Se requieren al menos 2 contenedores.");
            }

            RouteEntity nuevaRuta = routeService.createRoute(request);

            return ResponseEntity.ok(nuevaRuta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/")
    public List<RouteEntity> findAllRoute(){
        return routeService.findAllRoutes();
    }

    @GetMapping("/driver/pending/{id}")
    public List<RouteEntity> findAllRouteByDriverPending(@PathVariable Long id){
        return routeService.getAllRoutesByIddriverPending(id);
    }

    @GetMapping("/driver/finish/{id}")
    public List<RouteEntity> findAllRouteByDriverFinish(@PathVariable Long id){
        return routeService.getAllRoutesByIddriverFinish(id);
    }

    @GetMapping("/{id}")
    public RouteEntity findRouteById(@PathVariable Long id){
        return routeService.findRouteById(id);
    }

    @GetMapping("/status/{iddriver}/{status}")
    public RouteEntity findRouteByStatusAndIdDriver(@PathVariable Long iddriver, @PathVariable String status){
        System.out.println(routeService.getRouteByIdDriverAndStatus(iddriver, status));
        return routeService.getRouteByIdDriverAndStatus(iddriver, status);
    }

    @PutMapping("/{id}")
    public void updateRoute(Long id, @RequestBody RouteEntity route){
        routeService.updateRoute(id, route);
    }

    @PutMapping("/status/{id}/{status}")
    public void updateRouteStatus(@PathVariable Long id, @PathVariable String status){
        routeService.updateRouteStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteRoute(@PathVariable Long id){
        routeService.deleteRoute(id);
    }

    @GetMapping("/inefficient-routes")
    public ResponseEntity<List<Map<String, Object>>> getInefficientRoutes() {
        List<Map<String, Object>> inefficientRoutes = routeService.findInefficientRoutes();
        return ResponseEntity.ok(inefficientRoutes);
    }

    @GetMapping("/efficiency")
    public ResponseEntity<List<Map<String, Object>>> getDriverEfficiency() {
        List<Map<String, Object>> efficiency = routeService.findDriverEfficiency();
        return ResponseEntity.ok(efficiency);
    }

    @GetMapping("/waste-performance")
    public ResponseEntity<List<Map<String, Object>>> compareWastePerformance() {
        List<Map<String, Object>> result = routeService.compareWastePerformance();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update-container-weight/{routeId}")
    public ResponseEntity<String> updateContainerWeight(
            @PathVariable Long routeId,
            @RequestParam double newWeight) {
        try {
            routeService.updateContainerWeight(routeId, newWeight);
            return ResponseEntity.ok("Contenedores de la ruta " + routeId + " actualizados a " + newWeight + " kg");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(" Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/kilometers")
    public ResponseEntity<Double> getRouteDistance(@PathVariable Long id) {
        Double km = routeService.getRouteDistance(id);
        return ResponseEntity.ok(km);
    }

}
