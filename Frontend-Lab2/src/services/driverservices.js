import api from './api.js';

export default {

    // Obtener todos los conductores
    getAllDrivers() {
        return api.get('/api/drivers/all');
    },

    // Obtener un conductor por ID
    getDriverById(driverId) {
        return api.get(`/api/drivers/${driverId}`);
    },

    // Crear un nuevo conductor
    createDriver(driverData) {
        return api.post('/api/drivers/', driverData);
    }, 

    // actualizar un conductor existente
    updateDriver(driverId, driverData) {
        return api.put(`/api/drivers/${driverId}`, driverData);
    },
    
    // eliminar un conductor
    deleteDriver(driverId) {
        return api.delete(`/api/drivers/${driverId}`);
    },

    getDriverByEmail(email) {
        return api.get(`/api/drivers/email/${email}`);
    }
};