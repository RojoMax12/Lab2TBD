package com.lab.backend.Services;

import com.lab.backend.Entities.PickUpEntity;
import com.lab.backend.Repository.PickUpRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PickUpServices {

    private PickUpRepository pickUpRepository;

    public PickUpServices(PickUpRepository pickUpRepository) {
        this.pickUpRepository = pickUpRepository;
    }

    public PickUpEntity CreatePickUp(PickUpEntity pickUpEntity) {
        return pickUpRepository.CreatePickUp(pickUpEntity);
    }

    public List<PickUpEntity> getAllPickUps() {
        return pickUpRepository.getAllPickUps();
    }

    public PickUpEntity getPickUpById(Long id) {
        return pickUpRepository.getPickUpById(id);
    }

    public void updatePickUp(Long id, PickUpEntity pickUpEntity) {
        pickUpRepository.updatePickUp(id, pickUpEntity);
    }

    public void deletePickUp(Long id) {
        pickUpRepository.deletePickUp(id);
    }


}
