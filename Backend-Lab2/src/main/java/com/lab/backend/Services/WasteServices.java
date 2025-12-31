package com.lab.backend.Services;

import com.lab.backend.Entities.WasteEntity;
import com.lab.backend.Repository.WasteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WasteServices {
    private WasteRepository wasteRepository;

    public WasteServices(WasteRepository wasteRepository) {
        this.wasteRepository = wasteRepository;
    }

    public WasteEntity CreateWaste(WasteEntity wasteEntity) {
        return wasteRepository.CreateWaste(wasteEntity);
    }

    public List<WasteEntity> getAllWastes() {
        return wasteRepository.GetAllWastes();
    }

    public WasteEntity getWasteById(int id) {
        return wasteRepository.GetWasteById(id);
    }

    public void updateWaste(int id, WasteEntity wasteEntity) {
        wasteRepository.updateWaste(id, wasteEntity);
    }

    public void deleteWaste(int id) {
        wasteRepository.deleteWaste(id);
    }
}
