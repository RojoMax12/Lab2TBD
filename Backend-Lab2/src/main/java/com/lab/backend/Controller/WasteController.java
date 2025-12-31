package com.lab.backend.Controller;

import com.lab.backend.Entities.WasteEntity;
import com.lab.backend.Repository.WasteRepository;
import com.lab.backend.Services.WasteServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waste")
public class WasteController {

    private final WasteServices wasteServices;

    public WasteController(WasteServices wasteServices) {
        this.wasteServices = wasteServices;
    }

    // Crear un nuevo Waste
    @PostMapping("/")
    public ResponseEntity<WasteEntity> createWaste(@RequestBody WasteEntity wasteEntity) {
        WasteEntity createdWaste = wasteServices.CreateWaste(wasteEntity);
        return new ResponseEntity<>(createdWaste, HttpStatus.CREATED);
    }

    // Obtener todos los Wastes
    @GetMapping("/")
    public ResponseEntity<List<WasteEntity>> getAllWastes() {
        List<WasteEntity> wastes = wasteServices.getAllWastes();
        return new ResponseEntity<>(wastes, HttpStatus.OK);
    }

    // Obtener un Waste por ID
    @GetMapping("/{id}")
    public ResponseEntity<WasteEntity> getWasteById(@PathVariable int id) {
        WasteEntity wasteEntity = wasteServices.getWasteById(id);
        if (wasteEntity != null) {
            return new ResponseEntity<>(wasteEntity, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Actualizar un Waste por ID
    @PutMapping("/{id}")
    public ResponseEntity<WasteEntity> updateWaste(@PathVariable int id, @RequestBody WasteEntity wasteEntity) {
        wasteServices.updateWaste(id, wasteEntity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Eliminar un Waste por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaste(@PathVariable int id) {
        wasteServices.deleteWaste(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
