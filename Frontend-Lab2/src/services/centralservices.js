import api from './api.js';

export default {

    createCentral(centralData) {
        return api.post('/api/central/', centralData);

    },

    deleteCentral(centralId) {
        return api.delete(`/api/central/${centralId}`);
    },

    updateCentral(centralId, centralData) {
        return api.put(`/api/central/UpdateCentral/${centralId}`, centralData);
    },

    getAllCentrals() {
        return api.get('/api/central/Allcentral');
    },

    getCentralById(centralId) {
        return api.get(`/api/central/${centralId}`);
    }

};