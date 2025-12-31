import { defineStore } from 'pinia';
import { jwtDecode } from "jwt-decode"// Asegúrate de tener esta dependencia instalada

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: null, // Almacenamos el token en el estado
  }),
  actions: {
    // Inicializamos el token desde el almacenamiento local
    initFromStorage() {
      try {
        const token = localStorage.getItem('jwt');
        if (token) {
          const decoded = jwtDecode(token); // Decodificamos el token
          if (decoded.exp < Date.now() / 1000) {
            // Si el token ha expirado, lo eliminamos
            this.clear();
          } else {
            // Si no ha expirado, lo almacenamos en el estado
            this.token = token;
          }
        }
      } catch (e) {
        this.token = null; // Si hay un error, lo ponemos a null
      }
    },

    // Guardar el token en el estado y almacenamiento local
    setToken(token) {
      this.token = token;
      try {
        localStorage.setItem('jwt', token); // Guardamos el token en localStorage
      } catch (e) {
        console.warn('No se pudo guardar token', e); // Mostramos una advertencia si no se puede guardar
      }
    },

    // Eliminar el token tanto del estado como de localStorage
    clear() {
      this.token = null;
      try {
        localStorage.removeItem('jwt'); // Eliminamos el token de localStorage
      } catch (e) {
        console.warn('No se pudo eliminar token', e); // Mostramos una advertencia si no se puede eliminar
      }
    },
  },
});
