package com.lab.backend.Services;

import com.lab.backend.Entities.DriverEntity;
import com.lab.backend.Repository.DriverRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverServices {

    private DriverRepository driverRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DriverServices(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public DriverEntity CreateDriver(DriverEntity driverEntity) {
        // Hash password before storing
        if (driverEntity.getPassword() != null && !driverEntity.getPassword().isEmpty()) {
            String hashed = passwordEncoder.encode(driverEntity.getPassword());
            driverEntity.setPassword(hashed);
        }
        return driverRepository.CreateDriver(driverEntity);
    }

    public List<DriverEntity> getAllDrivers() {
        return driverRepository.getAllDrivers();
    }

    public DriverEntity getDriverById(Long id) {
        return driverRepository.getDriverById(id);
    }

    public DriverEntity getDriverByEmail(String email) {
        return driverRepository.getDriverByEmail(email);
    }

    public void updateDriver(Long id, DriverEntity driverEntity) {
        // If password is being updated, hash it
        if (driverEntity.getPassword() != null && !driverEntity.getPassword().isEmpty()) {
            String hashed = passwordEncoder.encode(driverEntity.getPassword());
            driverEntity.setPassword(hashed);
        }
        driverRepository.updateDriver(id, driverEntity);
    }

    public void deleteDriver(Long id) {
        driverRepository.deleteDriver(id);
    }

}
