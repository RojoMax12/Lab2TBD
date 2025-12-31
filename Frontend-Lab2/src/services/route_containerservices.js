import api from './api.js';

export default {

    // Crear un nuevo registro route_container
    createRouteContainer(data) {
        return api.post('/api/routecontainer/create', data);
    },

    // Obtener todos los registros route_container
    getAllRouteContainers() {
        return api.get('/api/routecontainer/');
    },

    // Obtener un registro route_container por su ID
    getRouteContainerById(id) {
        return api.get(`/api/routecontainer/${id}`);
    },

    // Obtener todos los contenedores asociados a una ruta
    getRouteContainersByRoute(routeId) {
        return api.get(`/api/routecontainer/route/${routeId}`);
    },

    // Obtener todas las rutas asociadas a un contenedor
    getRouteContainerByContainer(containerId) {
        return api.get(`/api/routecontainer/container/${containerId}`);
    },

    // Eliminar un registro route_container
    deleteRouteContainer(id) {
        return api.delete(`/api/routecontainer/${id}`);
    }
};
