package com.lab.backend.Controller;

import com.lab.backend.Entities.ContainerEntity;
import com.lab.backend.Services.ContainerServices;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/containers")
public class ContainerController {
    private final ContainerServices containerService;

    public ContainerController(ContainerServices containerService) {
        this.containerService = containerService;
    }

    @PostMapping("/")
    public ContainerEntity createContainer(@RequestBody ContainerEntity container) {
        return containerService.CreateContainer(container);
    }

    @GetMapping("/")
    public List<ContainerEntity> getAllContainers() {
        return containerService.getAllContainers();
    }

    @GetMapping("/{id}")
    public ContainerEntity getContainerById(@PathVariable Long id) {
        return containerService.getContainerById(id);
    }

    @PutMapping("/{id}")
    public void updateContainer(@PathVariable Long id, @RequestBody ContainerEntity container) {
        containerService.updateContainer(id, container);
    }

    @DeleteMapping("/{id}")
    public void deleteContainer(@PathVariable Long id) {
        containerService.deleteContainer(id);
    }

    @GetMapping("/problematic")
    public List<Map<String, Object>> getProblematicContainers() {
        return containerService.getProblematicContainersReport();
    }

    @GetMapping("/density-analysis") // Nuevo endpoint
    public List<Map<String, Object>> getContainerDensityAnalysisReport() {
        return containerService.getContainerDensityAnalysis();
    }

    @GetMapping("/NoRecolectados")
    public List<Map<String, Object>> getContenedoresSinRecoleccionReciente() {
        return containerService.getContenedoresSinRecoleccionReciente();
    }

    @GetMapping("/outside-zone")
    public List<Map<String, Object>> getContainersOutsideZone() {
        return containerService.getContainersOutsideZone();
    }

    // Endpoint para obtener el contenedor más cercano.
    // Soporta: ?location=POINT(lon lat)  OR ?lon=...&lat=...
    @GetMapping("/nearest")
    public ContainerEntity getNearestContainer(@RequestParam(required = false) Double lon,
                                               @RequestParam(required = false) Double lat,
                                               @RequestParam(required = false) String location) {
        if (location != null && !location.isBlank()) {
            return containerService.findNearestContainerByWkt(location);
        }
        if (lon != null && lat != null) {
            return containerService.findNearestContainer(lon, lat);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide 'location' (WKT) or 'lon' and 'lat' parameters.");
    }
}
