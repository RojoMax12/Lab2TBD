import api from './api.js';

export default {

    // Obtener todas las zonas de recolección
    getAllCollectionZones() {
        return api.get('/api/collection-zones/');
    },

    // Obtener una zona de recolección por ID
    getCollectionZoneById(zoneId) {
        return api.get(`/api/collection-zones/${zoneId}`);
    },

    // Crear una nueva zona de recolección
    createCollectionZone(zoneData) {
        return api.post('/api/collection-zones/', zoneData);
    },

    // Actualizar una zona de recolección existente
    updateCollectionZone(zoneId, zoneData) {
        return api.put(`/api/collection-zones/${zoneId}`, zoneData);
    },

    // Eliminar una zona de recolección
    deleteCollectionZone(zoneId) {
        return api.delete(`/api/collection-zones/${zoneId}`);
    }

};