package com.lab.backend.Controller;

import com.lab.backend.Entities.AdminEntity;
import com.lab.backend.Services.AdminServices;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private AdminServices adminServices;

    public AdminController(AdminServices adminServices) {
        this.adminServices = adminServices;
    }

    @PostMapping("/")
    public AdminEntity createAdmin(@RequestBody AdminEntity admin) {
        return adminServices.createAdmin(admin);
    }

    @GetMapping("/")
    public List<AdminEntity> getAllAdmins() {
        return adminServices.getAllAdmins();
    }

    @GetMapping("/{id}")
    public AdminEntity getAdminById(@PathVariable Long id) {
        return adminServices.getAdminById(id);
    }

    @GetMapping("/email/{email}")
    public AdminEntity getAdminByEmail(@PathVariable String email) {
        return adminServices.getAdminByEmail(email);
    }

    @PutMapping("/{id}")
    public void updateAdmin(@PathVariable Long id, @RequestBody AdminEntity admin) {
        adminServices.updateAdmin(id, admin);
    }

    @DeleteMapping("/{id}")
    public void deleteAdmin(@PathVariable Long id) {
        adminServices.deleteAdmin(id);
    }
}
