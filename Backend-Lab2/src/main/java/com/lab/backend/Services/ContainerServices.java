package com.lab.backend.Services;

import com.lab.backend.Entities.ContainerEntity;
import com.lab.backend.Repository.ContainerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ContainerServices {
    private ContainerRepository containerRepository;

    public ContainerServices(ContainerRepository containerRepository) {
        this.containerRepository = containerRepository;
    }

    public ContainerEntity CreateContainer(ContainerEntity container) {
        return containerRepository.CreateContainer(container);
    }

    public List<ContainerEntity> getAllContainers() {

        return containerRepository.GetAllContainers();
    }

    public ContainerEntity getContainerById(Long id) {

        return containerRepository.GetContainerById(id);
    }

    public void updateContainer(Long id, ContainerEntity container) {
        containerRepository.UpdateContainer(id, container);
    }

    public void deleteContainer(Long id) {

        containerRepository.DeleteContainer(id);
    }

    public List<Map<String, Object>> getProblematicContainersReport() {
        return containerRepository.getProblematicContainers();
    }

    public List<Map<String, Object>> getContainerDensityAnalysis() {
        return containerRepository.getMonthlyContainerDensityAnalysis();
    }

    // ✅ Lógica de servicio para exponer la consulta
    public List<Map<String, Object>> getContenedoresSinRecoleccionReciente() {
        return containerRepository.getContenedoresSinRecoleccionReciente();
    }

    

}