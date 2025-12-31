package com.lab.backend.Controller;

import com.lab.backend.Entities.Route_ContainerEntity;
import com.lab.backend.Services.Route_ContainerServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/routecontainer")
public class Route_ContainerController {

    private final Route_ContainerServices routeContainerServices;

    // Inyección por constructor (más confiable que @Autowired en campo)
    public Route_ContainerController(Route_ContainerServices routeContainerServices) {
        this.routeContainerServices = routeContainerServices;
    }


    @PostMapping("/create")
    public ResponseEntity<Route_ContainerEntity> create(@RequestBody Route_ContainerEntity routeContainerEntity) {
        Route_ContainerEntity newRelation = routeContainerServices.createRouteContainer(routeContainerEntity);
        return ResponseEntity.ok(newRelation);
    }


    @GetMapping("/")
    public ResponseEntity<List<Route_ContainerEntity>> getAll() {
        return ResponseEntity.ok(routeContainerServices.getAllRouteContainers());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Route_ContainerEntity> getById(@PathVariable Long id) {
        Route_ContainerEntity relation = routeContainerServices.getRouteContainerById(id);
        return ResponseEntity.ok(relation);
    }


    @GetMapping("/container/{containerId}")
    public ResponseEntity<List<Route_ContainerEntity>> getByContainerId(@PathVariable("containerId") Long containerId) {
        return ResponseEntity.ok(routeContainerServices.getRouteContainerByContainer(containerId));
    }


    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<Route_ContainerEntity>> getByRouteId(@PathVariable("routeId") Long routeId) {
        return ResponseEntity.ok(routeContainerServices.getRouteContainersByRoute(routeId));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeContainerServices.deleteRouteContainer(id);
        return ResponseEntity.noContent().build();
    }
}
