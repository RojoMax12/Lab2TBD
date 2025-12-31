import api from './api.js';

export default {
  getAllPickUp() {
    // common backend naming is unknown, try primary candidate
    return api.get('/api/pickups/');
  },
  getPickUpById(id) {
    return api.get(`/api/pickups/${id}`);
  },
  createPickUp(pickUpData) {
    return api.post('/api/pickups/', pickUpData);
  },
  updatePickUp(id, pickUpData) {
    return api.put(`/api/pickups/${id}`, pickUpData);
  },
  deletePickUp(id) {
    return api.delete(`/api/pickups/${id}`);
  }
}
