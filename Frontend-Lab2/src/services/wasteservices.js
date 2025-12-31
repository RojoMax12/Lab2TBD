import api from './api.js';

export default {

    createWaste(wasteEntity) {
    return api.post('/api/waste/', wasteEntity)
  },

  // Obtener todos los Wastes
  getAllWastes() {
    return api.get('/api/waste/')
  },

  // Obtener un Waste por ID
  getWasteById(id) {
    return api.get(`/api/waste/${id}`)
  },

  // Actualizar un Waste
  updateWaste(id, wasteEntity) {
    return api.put(`/api/waste/${id}`, wasteEntity)

  },

  // Eliminar un Waste
  deleteWaste(id) {
    return api.delete(`/api/waste/${id}`)

    }
}