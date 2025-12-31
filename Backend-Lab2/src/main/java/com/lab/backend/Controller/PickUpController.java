package com.lab.backend.Controller;

import com.lab.backend.Entities.DriverEntity;
import com.lab.backend.Entities.PickUpEntity;
import com.lab.backend.Entities.RouteEntity;
import com.lab.backend.Services.DriverServices;
import com.lab.backend.Services.PickUpServices;
import com.lab.backend.Services.RouteServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pickups")
public class PickUpController {

    private final PickUpServices pickUpServices;
    private final DriverServices driverServices;
    private final RouteServices routeServices;

    public PickUpController(PickUpServices pickUpServices, DriverServices driverServices, RouteServices routeServices) {
        this.pickUpServices = pickUpServices;
        this.driverServices = driverServices;
        this.routeServices = routeServices;
    }

    /**
     * Crear un pickup.
     * Permisos:
     *  - Admin puede crear para cualquier ruta.
     *  - Driver puede crear solo para rutas que le pertenecen (route.id_driver == driver.id) y cuando la ruta está en proceso.
     */
    @PostMapping("/")
    public ResponseEntity<?> createPickUp(@RequestBody PickUpEntity pickUp) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(403).body("No autenticado");
        }

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            PickUpEntity created = pickUpServices.CreatePickUp(pickUp);
            return ResponseEntity.ok(created);
        }

        // Si no es admin, verificar que es driver y que la ruta pertenece al driver
        boolean isDriver = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DRIVER"));
        if (!isDriver) {
            return ResponseEntity.status(403).body("No tienes permisos para crear pickups");
        }

        String username = auth.getName(); // el JwtFilter establece el username (email) como principal
        DriverEntity driver = driverServices.getDriverByEmail(username);
        if (driver == null) {
            return ResponseEntity.status(403).body("Driver no encontrado");
        }

        // Obtener la ruta y verificar titularidad
        RouteEntity route = routeServices.findRouteById(pickUp.getId_route());
        if (route == null) {
            return ResponseEntity.status(404).body("Ruta no encontrada");
        }

        if (route.getId_driver() == null || !route.getId_driver().equals(driver.getId())) {
            return ResponseEntity.status(403).body("No puedes crear pickups para una ruta que no te pertenece");
        }

        // Opcional: permitir solo cuando la ruta está en proceso
        if (route.getRoute_status() == null || !route.getRoute_status().equalsIgnoreCase("EnProceso")) {
            return ResponseEntity.status(403).body("La ruta no está en estado 'EnProceso' y no permite crear pickups");
        }

        // Todo OK: crear pickup
        PickUpEntity created = pickUpServices.CreatePickUp(pickUp);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/")
    public List<PickUpEntity> getAllPickUps() {
        return pickUpServices.getAllPickUps();
    }

    @GetMapping("/{id}")
    public PickUpEntity getPickUpById(@PathVariable Long id) {
        return pickUpServices.getPickUpById(id);
    }

    @PutMapping("/{id}")
    public void updatePickUp(@PathVariable Long id, @RequestBody PickUpEntity pickUp) {
        pickUpServices.updatePickUp(id, pickUp);
    }

    @DeleteMapping("/{id}")
    public void deletePickUp(@PathVariable Long id) {
        pickUpServices.deletePickUp(id);
    }
}
