import api from './api.js';

export default {

    // Crear ruta
    createroute(routeData) {
        return api.post('/api/route/planroute', routeData);
    },

    // Obtener rutas ineficientes
    inefficientRoutes() {
        return api.get('/api/route/inefficient-routes');
    },

    // Obtener eficiencia del conductor
    efficiency() {
        return api.get('/api/route/efficiency');
    },

    // Comparar desempeño de residuos
    wasteperformance() {
        return api.get('/api/route/waste-performance');
    },

    // Actualizar el peso del contenedor
    updateContainerWeight(routeId, newWeight) {
        return api.put(`/api/route/update-container-weight/${routeId}`, null, {
            params: { newWeight }
        });
    },

    // Obtener todas las rutas
    getAllRoutes() {
        return api.get('/api/route/');
    },

    // Obtener rutas asignadas al conductor (Pendientes)
    getAllRouterByDriverIdPending(driverId) {
        return api.get(`/api/route/driver/pending/${driverId}`);
    },

    // Obtener rutas asignadas al conductor (Finalizadas)
    getAllRouterByDriverIdFinish(driverId) {
        return api.get(`/api/route/driver/finish/${driverId}`);
    },

    // Obtener ruta por ID
    getRouteById(routeId) {
        return api.get(`/api/route/${routeId}`);
    },

    findRouteByStatusAndIdDriver(IdDriver, status){ 
        return api.get(`/api/route/status/${IdDriver}/${status}`);
    }
    ,
    // Eliminar ruta
    deleteRoute(routeId) {
        return api.delete(`/api/route/${routeId}`);
    },

    // Actualizar ruta
    updateRoute(routeId, routeData) {
        return api.put(`/api/route/${routeId}`, routeData);
    },

    // Actualizar el estado de la ruta
    updateRouteStatus(routeId, status) {
        return api.put(`/api/route/status/${routeId}/${status}`);
    }
};
