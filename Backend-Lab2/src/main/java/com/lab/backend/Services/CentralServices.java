package com.lab.backend.Services;


import com.lab.backend.Entities.CentralEntity;
import com.lab.backend.Repository.CentralRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CentralServices {

    private CentralRepository centralRepository;

    public CentralServices(CentralRepository centralRepository) {
        this.centralRepository = centralRepository;
    }

    public List<CentralEntity> getAllCentrals() {
        return centralRepository.getAllCentrals();
    }

    public CentralEntity createCentral(CentralEntity central) {
        return centralRepository.createCentral(central);
    }

    public void updateCentral(Long id ,CentralEntity central) {
        centralRepository.updateCentral(id, central);
    }

    public void deleteCentral(Long id) {
        centralRepository.deleteCentral(id);
    }

    public CentralEntity getCentral(Long id) {
        return centralRepository.getCentralById(id);
    }



}
