import api from './api.js';

export default {

    createContainer(containerData) {
        return api.post('/api/containers/', containerData);
    },

    deleteContainer(containerId) {
        return api.delete(`/api/containers/${containerId}`);
    },

    updateContainer(containerId, containerData) {
        return api.put(`/api/containers/${containerId}`, containerData);
    },

    getAllContainers() {
        return api.get('/api/containers/');
    },
    
    getContainerById(containerId) {
        return api.get(`/api/containers/${containerId}`);
    },

    ContainerWithProblems() {
        return api.get('/api/containers/problematic');
    },

    DensityAnalysisContainer() {
        return api.get('/api/containers/density-analysis');
    },

    ContainersWithoutCollection() {
        return api.get('/api/containers/NoRecolectados');
    }

};