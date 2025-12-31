import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',  // Asegúrate de que esta URL sea la correcta según tu backend
    headers: {
        'Content-Type': 'application/json',  // Especifica que el tipo de contenido será JSON
    },
    withCredentials: true,  // Esto es útil si necesitas enviar cookies entre dominios
    timeout: 5000,  // Timeout de 5 segundos, ajusta si es necesario
});

// Interceptor para agregar el token JWT si existe
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('jwt');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;  // Agrega el token JWT en el header
        }
        return config;  // Devuelve la configuración modificada para continuar con la petición
    },
    error => Promise.reject(error)  // Manejo de errores de la solicitud
);

// Interceptor para manejar errores de la respuesta
api.interceptors.response.use(
    response => response,  // Si la respuesta es exitosa, la devuelve tal cual
    error => {
        // Verifica si hubo un error de red
        if (error.code === 'ERR_NETWORK') {
            console.error('Error de conexión con el servidor. Por favor, verifica que el backend esté en ejecución.');
        } else if (error.response) {  // Si hay una respuesta del servidor, pero con un error (4xx, 5xx)
            console.error('Error en la respuesta del servidor:', error.response.data);
            // Agregar manejo específico para errores 4xx o 5xx si es necesario
            if (error.response.status === 401) {
                // Si el estado es 401, podría ser que el token JWT ha expirado
                console.error('Token expirado o no autorizado. Por favor, inicia sesión nuevamente.');
            } else if (error.response.status === 403) {
                // Si el estado es 403, podría ser un problema de permisos
                console.error('Acceso denegado. No tienes permisos suficientes.');
            }
        } else if (error.request) {  // Si no hay respuesta del servidor
            console.error('No se recibió respuesta del servidor:', error.request);
        } else {  // Si ocurre otro tipo de error
            console.error('Error en la petición:', error.message);
        }
        return Promise.reject(error);  // Devuelve el error para que sea manejado por el componente
    }
);

export default api;
