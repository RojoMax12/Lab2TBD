package com.lab.backend.Services;

import com.lab.backend.Entities.Route_ContainerEntity;
import com.lab.backend.Repository.Route_ContainerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Route_ContainerServices {

    private final Route_ContainerRepository routeContainerRepository;

    public Route_ContainerServices(Route_ContainerRepository routeContainerRepository) {
        this.routeContainerRepository = routeContainerRepository;
    }

    public Route_ContainerEntity createRouteContainer(Route_ContainerEntity routeContainerEntity) {
        return routeContainerRepository.createRouteContainer(routeContainerEntity);
    }

    public List<Route_ContainerEntity> getAllRouteContainers() {
        return routeContainerRepository.getAllRouteContainers();
    }

    public Route_ContainerEntity getRouteContainerById(Long id) {
        return routeContainerRepository.getRouteContainerById(id);
    }

    public List<Route_ContainerEntity> getRouteContainersByRoute(Long routeId) {
        return routeContainerRepository.getRouteContainersByRouteId(routeId);
    }

    public List<Route_ContainerEntity> getRouteContainerByContainer(Long containerId) {
        return routeContainerRepository.getRouteContainerByContainerId(containerId);
    }

    public void deleteRouteContainer(Long id) {
        routeContainerRepository.deleteRouteContainer(id);
    }
}
