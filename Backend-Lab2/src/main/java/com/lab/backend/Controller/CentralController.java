package com.lab.backend.Controller;

import com.lab.backend.Entities.CentralEntity;
import com.lab.backend.Services.CentralServices;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/central")
public class CentralController {

    private CentralServices centralServices;


    private CentralController(CentralServices centralServices) {
        this.centralServices = centralServices;
    }

    @GetMapping("/Allcentral")
    public List<CentralEntity> getAllCentrals(){
        return centralServices.getAllCentrals();
    }

    @PostMapping("/")
    public CentralEntity createCentral(@RequestBody CentralEntity central){
        return centralServices.createCentral(central);
    }

    @GetMapping("/{id}")
    public CentralEntity getCentralById(@PathVariable Long id){
        return centralServices.getCentral(id);

    }

    @DeleteMapping("/{id}")
    public void deleteCentralById(@PathVariable Long id){
        centralServices.deleteCentral(id);
    }

    @PutMapping("/UpdateCentral/{id}")
    public void updateCentral(@PathVariable Long id, @RequestBody CentralEntity central){
        centralServices.updateCentral(id,central);
    }
}
