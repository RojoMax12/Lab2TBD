package com.lab.backend.Controller;

import com.lab.backend.Configuration.JwtUtil;
import com.lab.backend.Entities.AdminEntity;
import com.lab.backend.Entities.DriverEntity;
import com.lab.backend.Services.AdminServices;
import com.lab.backend.Services.DriverServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final DriverServices driverServices;
    private final AdminServices adminServices;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(JwtUtil jwtUtil, DriverServices driverServices, AdminServices adminServices) {
        this.jwtUtil = jwtUtil;
        this.driverServices = driverServices;
        this.adminServices = adminServices;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> user) {

        String username = user.get("username");
        String email = user.get("email");
        String password = user.get("password");

        System.out.println("======= LOGIN REQUEST =======");
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("=============================");

        if (email == null || password == null) {
            System.out.println("ERROR → Missing credentials");
            return ResponseEntity.badRequest().body(Map.of("error", "Faltan credenciales"));
        }

        // Buscar admin y driver
        AdminEntity admin = adminServices.getAdminByEmail(email);
        DriverEntity driver = driverServices.getDriverByEmail(email);

        System.out.println("Admin found? " + (admin != null));
        System.out.println("Driver found? " + (driver != null));

        // =============== LOGIN ADMIN ===============
        if (admin != null) {

            System.out.println("Admin role: " + admin.getRole());
            String storedPass = admin.getPassword();

            boolean passCorrect =
                    passwordEncoder.matches(password, storedPass) ||  // si está encriptada
                            password.equals(storedPass);                      // si está en texto plano

            if (!passCorrect) {
                System.out.println("ERROR → Incorrect password for admin");
                return ResponseEntity.status(403).body(Map.of("error", "Credenciales inválidas"));
            }

            System.out.println("SUCCESS → Admin logged in");
            String token = jwtUtil.generateToken(admin.getEmail(), "admin");

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role", "admin"
            ));
        }

        // =============== LOGIN DRIVER ===============
        if (driver != null) {

            String storedPass = driver.getPassword();

            boolean passCorrect =
                    passwordEncoder.matches(password, storedPass) ||
                            password.equals(storedPass);

            if (!passCorrect) {
                System.out.println("ERROR → Incorrect password for driver");
                return ResponseEntity.status(403).body(Map.of("error", "Credenciales inválidas"));
            }

            System.out.println("SUCCESS → Driver logged in");
            String token = jwtUtil.generateToken(driver.getEmail(), "driver");

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role", "driver"
            ));
        }

        // Ninguno existe
        System.out.println("ERROR → No admin or driver matched the email");
        return ResponseEntity.status(403).body(Map.of("error", "Credenciales inválidas"));
    }



}
