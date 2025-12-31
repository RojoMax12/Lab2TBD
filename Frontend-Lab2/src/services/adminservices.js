import api from './api.js';

export default {

    // Obtener todos los administradores
    getAllAdmins() {
        return api.get('/api/admin/');
    }

    // Obtener un administrador por ID
    ,
    getAdminById(adminId) {
        return api.get(`/api/admin/${adminId}`);

    }

    // Crear un nuevo administrador
    ,
    createAdmin(adminData) {
        return api.post('/api/admin/', adminData);
    }
    ,

    getAdminByEmail(email) {
        return api.get(`/api/admin/email/${email}`);
    }

    ,

    updateAdmin(adminId, adminData) {
        return api.put(`/api/admin/${adminId}`, adminData);
    }

    // Eliminar un administrador
    ,
    deleteAdmin(adminId) {
        return api.delete(`/api/admin/${adminId}`);
    }

};