package com.lab.backend.Services;

import com.lab.backend.Entities.AdminEntity;
import com.lab.backend.Repository.AdminRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServices {

    private AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminServices(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public AdminEntity createAdmin(AdminEntity admin) {
        // Hash password before storing (same behavior as DriverServices)
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            String hashed = passwordEncoder.encode(admin.getPassword());
            admin.setPassword(hashed);
        }
        return adminRepository.CreateAdmin(admin);
    }

    public List<AdminEntity> getAllAdmins() {
        return adminRepository.getAllAdmins();
    }

    public AdminEntity getAdminById(Long id) {
        return adminRepository.getAdminById(id);
    }

    public AdminEntity getAdminByEmail(String email) {
        return adminRepository.getAdminByEmail(email);
    }

    public void updateAdmin(Long id, AdminEntity admin) {
        // If password is being updated, hash it
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            String hashed = passwordEncoder.encode(admin.getPassword());
            admin.setPassword(hashed);
        }
        adminRepository.updateAdmin(id, admin);
    }

    public void deleteAdmin(Long id) {
        adminRepository.deleteAdminById(id);
    }
}
