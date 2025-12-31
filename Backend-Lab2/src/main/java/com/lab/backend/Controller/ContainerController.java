package com.lab.backend.Controller;

import com.lab.backend.Entities.ContainerEntity;
import com.lab.backend.Services.ContainerServices;
import org.springframework.web.bind.annotation.*;
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
}
