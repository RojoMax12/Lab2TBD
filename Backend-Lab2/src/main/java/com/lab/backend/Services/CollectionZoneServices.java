package com.lab.backend.Services;

import com.lab.backend.Entities.CollectionZoneEntity;
import com.lab.backend.Repository.CollectionZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionZoneServices {
    private final CollectionZoneRepository collectionZoneRepository;

    public CollectionZoneServices(CollectionZoneRepository collectionZoneRepository) {
        this.collectionZoneRepository = collectionZoneRepository;
    }

    public CollectionZoneEntity createCollectionZone(CollectionZoneEntity cz) {
        return collectionZoneRepository.CreateCollectionZone(cz);
    }

    public List<CollectionZoneEntity> getAllCollectionZones() {
        return collectionZoneRepository.GetAllCollectionZones();
    }

    public CollectionZoneEntity getCollectionZoneById(Long id) {
        return collectionZoneRepository.GetCollectionZoneById(id);
    }

    public void updateCollectionZone(Long id, CollectionZoneEntity cz) {
        collectionZoneRepository.UpdateCollectionZone(id, cz);
    }

    public void deleteCollectionZone(Long id) {
        collectionZoneRepository.DeleteCollectionZone(id);
    }
}
