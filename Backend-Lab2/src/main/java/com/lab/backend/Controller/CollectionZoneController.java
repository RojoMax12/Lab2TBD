package com.lab.backend.Controller;

import com.lab.backend.Entities.CollectionZoneEntity;
import com.lab.backend.Services.CollectionZoneServices;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collection-zones")
public class CollectionZoneController {
    private final CollectionZoneServices collectionZoneServices;

    public CollectionZoneController(CollectionZoneServices collectionZoneServices) {
        this.collectionZoneServices = collectionZoneServices;
    }

    @PostMapping("/")
    public CollectionZoneEntity create(@RequestBody CollectionZoneEntity cz) {
        return collectionZoneServices.createCollectionZone(cz);
    }

    @GetMapping("/")
    public List<CollectionZoneEntity> getAll() {
        return collectionZoneServices.getAllCollectionZones();
    }

    @GetMapping("/{id}")
    public CollectionZoneEntity getById(@PathVariable Long id) {
        return collectionZoneServices.getCollectionZoneById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody CollectionZoneEntity cz) {
        collectionZoneServices.updateCollectionZone(id, cz);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        collectionZoneServices.deleteCollectionZone(id);
    }
}
