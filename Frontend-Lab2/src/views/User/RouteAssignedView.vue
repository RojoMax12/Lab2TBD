<template>
  <div>
    <HomeDriverView />

    <div class="route-assigned-view">
      <h1 class="title">Rutas Asignadas</h1>

      <button class="actual-route" @click="VolverInicio">Ver Ruta Actual</button>

      <div class="container">
        <div class="horizontal-scroll">
          <table class="route-table">
            <thead>
              <tr>
                <th>ID Ruta</th>
                <th>Conductor</th>
                <th>Fecha</th>
                <th>Hora Inicio</th>
                <th>Hora Fin</th>
                <th>Estado Ruta</th>
                <th>Central de inicio</th>
                <th>Central de termino</th>
                <th>Acciones</th>
              </tr>
            </thead>

            <tbody>
              <tr v-for="route in routes.data" :key="route.id">
                <td>{{ route.id }}</td>
                <td>{{ getNameDriverById(route.id_driver) }}</td>
                <td>{{ route.date_ }}</td>
                <td>{{ route.start_time }}</td>
                <td>{{ route.end_time }}</td>
                <td>{{ route.route_status }}</td>
                <td>{{ getNameCentralById(route.id_central) }}</td>
                <td>{{ getNameCentralById(route.id_central_finish) }}</td>
                <td class="row-actions">
                  <button
                    class="btn-take"
                    @click="takeRoute(route)"
                    :disabled="isBlocked(route)"
                  >
                    <!-- Mostrar texto según estado -->
                    {{ route.route_status === 'EnProceso' || route.route_status === 'En Proceso' ? 'En Proceso' : (route.route_status === 'Tomada' ? 'Ruta Tomada' : 'Tomar') }}
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import HomeDriverView from '@/components/Users/HomeDrive.vue';
import routeServices from '@/services/routeservices';
import DriverServices from '@/services/driverservices';
import centralServices from '@/services/centralservices';
import { jwtDecode } from "jwt-decode";
import { useRouter } from 'vue-router';

const router = useRouter();

// Definir variables reactivas
const routes = ref([]); // Lista de rutas asignadas
const userEmail = ref(""); // Email del usuario logueado
const driver = ref(null); // Conductor obtenido desde la API
const central = ref(null); // Central obtenida desde la API
const centralNames = ref({}); // cache id -> name


// Obtener los datos del conductor logueado
async function getDriverData(email) {
  try {

    const response = await DriverServices.getDriverByEmail(email);

    // Si la respuesta es exitosa, asignar los datos al objeto `driver`
    driver.value = response.data;


    // Ahora que tenemos los datos del conductor, obtenemos sus rutas asignadas
    if (driver.value) {
      getallrouteassigned(driver.value); // Llamar a la función para obtener las rutas
    }
  } catch (err) {
    console.error("Error obteniendo los datos del conductor:", err);
  }
}

function getNameDriverById(id) {
  if (!driver.value || String(driver.value.id) !== String(id)) {
    return "Desconocido";
  }
  return `${driver.value.name} ${driver.value.last_name}`;
}

function getNameCentralById(id) {
  if (id === null || id === undefined) return 'Desconocido'

  // If we have it cached, return synchronously (so template can render)
  if (centralNames.value[String(id)]) return centralNames.value[String(id)]

  // Otherwise, start an async fetch in background and return a placeholder
  centralServices.getCentralById(id)
    .then(res => {
      const c = (res && res.data) ? res.data : res
      if (c && String(c.id) === String(id)) {
        centralNames.value[String(id)] = c.name || 'Desconocido'
      } else {
        centralNames.value[String(id)] = 'Desconocido'
      }
    })
    .catch(err => {
      console.error('Error loading central', id, err)
      centralNames.value[String(id)] = 'Desconocido'
    })

  return 'Cargando...'
}

// Navegar a la vista de ruta actual
const VolverInicio = () => {
  router.push('/driver');
}

// Obtener todas las rutas asignadas al conductor logueado (pendientes + ruta en curso)
const getallrouteassigned = async (driver) => {
  if (!driver || !driver.id) return; // Asegurarnos de que el `driver` tenga un id

  try {
    // 1) Obtener pendientes (lista)
    const pendingRes = await routeServices.getAllRouterByDriverIdPending(driver.id);
    const pending = Array.isArray(pendingRes.data) ? pendingRes.data : (pendingRes.data || []);

    // 2) Intentar obtener la ruta en estado 'EnProceso' o 'Tomada' (si existe)
    const activeStatuses = ['EnProceso', 'En Proceso', 'Tomada'];
    const activeRoutes = [];

    for (const st of activeStatuses) {
      try {
        const r = await routeServices.findRouteByStatusAndIdDriver(driver.id, st);
        if (r && r.data) {
          // r may be an axios response with .data or a raw object
          const routeObj = r.data && typeof r.data === 'object' ? r.data : r;
          // avoid duplicates
          if (!pending.some(p => String(p.id) === String(routeObj.id)) && !activeRoutes.some(a => String(a.id) === String(routeObj.id))) {
            activeRoutes.push(routeObj);
          }
        }
      } catch (err) {
        // Ignore if not found or other errors for this specific status
      }
    }

    // 3) Combinar pendientes + activas para mostrar en la tabla (mantenemos formato axios-like { data: [...] })
    const combined = [...activeRoutes, ...pending];
    routes.value = { data: combined };

  } catch (error) {
    console.error('Error al obtener las rutas:', error);
  }
};

// Comprueba si hay alguna ruta activa en proceso para este conductor
const hasActiveRoute = () => {
  if (!routes.value || !routes.value.data) return false;
  return routes.value.data.some(r => {
    const s = (r.route_status || '').toString();
    return s === 'EnProceso' || s === 'En Proceso' || s === 'Tomada';
  });
}

// Determina si una ruta concreta debe estar bloqueada (no se puede tomar)
const isBlocked = (route) => {
  // Si la ruta ya está en proceso o tomada, bloquear su botón (no permitir volver a "tomar")
  if (!route) return true;
  const status = (route.route_status || '').toString();
  // If this route is already EnProceso, keep it disabled for taking (it's already taken)
  if (status === 'EnProceso' || status === 'En Proceso' || status === 'Tomada') return true;

  // Si hay otra ruta activa (EnProceso/Tomada), bloquear esta
  if (hasActiveRoute()) return true;

  // Otherwise it's available
  return false;
}

// Función para tomar la ruta
const takeRoute = (route) => {
  if (!route) return;

  // Si ya hay una ruta activa, no permitimos tomar otra
  if (hasActiveRoute()) {
    alert('Ya tienes una ruta en curso. Debes finalizarla antes de tomar otra.');
    return;
  }

  // Marcar localmente como en proceso para feedback inmediato
  route.route_status = 'EnProceso';
  route.id_driver = driver.value.id;


  // Llamada al backend para actualizar el estado
  routeServices.updateRouteStatus(route.id, 'EnProceso')
    .then(() => {
      // Refrescar la lista para reflejar cambios y bloqueo de otras rutas
      if (driver.value) getallrouteassigned(driver.value);
      alert(`Ruta con ID ${route.id} ha sido tomada y está en proceso.`);
      // Redirigir al path /driver para que el conductor vea su vista de ruta
      try {
        router.push('/driver');
      } catch (navErr) {
        console.warn('No se pudo redirigir a /driver:', navErr);
      }
    })
    .catch((error) => {
      console.error('Error al actualizar la ruta:', error);
      alert('Hubo un error al asignar la ruta. Intenta de nuevo.');
      // Revertir cambio local en caso de error
      route.route_status = 'Pendiente';
    });
};

onMounted(() => {
  const token = localStorage.getItem('jwt');  // Obtener el token del almacenamiento local

  if (!token) return;  // Si no hay token, no hacer nada

  try {
    const decoded = jwtDecode(token);  // Decodificar el token JWT

    userEmail.value = decoded.sub || decoded.email || null;

    if (userEmail.value) {
      getDriverData(userEmail.value);  // Llamar para obtener los datos del conductor
    }
  } catch (error) {
    console.error("Error al decodificar el token:", error);
  }
});
</script>

<style scoped>
.route-assigned-view {
  width: 100%;
  max-width: 1200px;
  margin: 2rem auto;
  background: #fff;
  border-radius: 1.2rem;
  box-shadow: 0 6px 18px rgba(78, 83, 54, 0.10);
  padding: 2rem 1.2rem;
  font-family: system-ui, -apple-system, "Segoe UI", Roboto, Arial;
}

.title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #4e5336;
  margin-bottom: 1.6rem;
  text-align: center;
}

.horizontal-scroll {
  overflow-x: auto;
  padding-bottom: 10px;
}

.route-table {
  width: 100%;
  min-width: 1000px;
  border-collapse: separate;
  border-spacing: 0;
  background: #fff;
  border-radius: 1rem;
  box-shadow: 0 2px 8px rgba(78, 83, 54, 0.08);
}

.route-table th, .route-table td {
  padding: 1rem 1.2rem;
  text-align: left;
  font-size: 1rem;
  border-bottom: 1px solid #eaeaea;
  vertical-align: middle;
}

.route-table th {
  background: linear-gradient(180deg, #5e6541, #52563f);
  color: #fff;
  font-weight: 600;
  letter-spacing: 0.2px;
}

.route-table td {
  color: #4e5336;
  font-weight: 500;
  background: #f7f7f7;
}

.route-table tr:last-child td {
  border-bottom: none;
}

@media (max-width: 900px) {
  .route-taken-view {
    max-width: 98vw;
    padding: 1.2rem 0.5rem;
  }
  .route-table th, .route-table td {
    padding: 0.7rem 0.7rem;
    font-size: 0.95rem;
  }
}

.scrollable-table {
  max-height: 300px;
  overflow-y: auto;
}

.btn-take {
  background: #4e5336;
  color: #fff;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.18s, box-shadow 0.18s;
}

.btn-take:disabled {
  background: #dcdcdc;
  cursor: not-allowed;
}

.btn-take:hover:not(:disabled) {
  background-color: #3f4732;
}

.actual-route {
  background: #4e5336;
  color: #fff;
  border: none;
  padding: 0.8rem 2rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 800;
  margin-bottom: 1.5rem;
  transition: background 0.18s, box-shadow 0.18s;
}

.actual-route:hover {
  background-color: #3f4732;
}
</style>
