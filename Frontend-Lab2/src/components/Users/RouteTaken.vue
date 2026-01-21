<template>
  <div class="route-taken-view">
    <h1 class="title">Rutas realizadas</h1>

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
            </tr>
          </thead>

          <tbody>
              <tr v-for="route in routes.data" :key="route.id" >
                <td>{{ route.id }}</td>
                <td>{{ getNameDriverById(route.id_driver) }}</td>
                <td>{{ route.date_ || 'Fecha no disponible' }}</td>
                <td>{{ route.start_time || 'Hora no disponible' }}</td>
                <td>{{ route.end_time || 'Hora no disponible' }}</td>
                <td>{{ route.route_status || 'Estado no disponible' }}</td>
                <td>
                  <div>{{ getCentralNameById(route.id_central) }}</div>
                  <div style="font-size:0.85rem;color:#6b6b6b">{{ getCentralCoordsById(route.id_central) }}</div>
                </td>
                <td>
                  <div>{{ getCentralNameById(route.id_central_finish) }}</div>
                  <div style="font-size:0.85rem;color:#6b6b6b">{{ getCentralCoordsById(route.id_central_finish) }}</div>
                </td>
              </tr>
            </tbody>
        </table>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import routeServices from '@/services/routeservices';
import DriverServices from '@/services/driverservices';
import CentralServices from '@/services/centralservices';
import { jwtDecode } from "jwt-decode";

// Definir variables reactivas
const routes = ref([]); // Lista de rutas asignadas
const userEmail = ref(""); // Email del usuario logueado
const driver = ref(null); // Conductor obtenido desde la API
const centralStart = ref(null); // Central de inicio
const centralEnd = ref(null); // Central de termino
const centralNames = ref({}); // cache id -> name
const driverNames = ref({}); // cache id -> name
const centralCoords = ref({}); // cache id -> 'lat, lng' or placeholder

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

function getNameDriverById(driverId) {
  if (driverId === null || driverId === undefined) return 'Nombre no disponible'

  const key = String(driverId)
  // Devuelve cache si existe
  if (driverNames.value[key]) return driverNames.value[key]

  // Marcar como cargando y disparar la petición en background
  driverNames.value[key] = 'Cargando...'
  DriverServices.getDriverById(driverId)
    .then(response => {
      const d = response && response.data ? response.data : response
      driverNames.value[key] = (d && d.name) ? d.name : 'Nombre no disponible'
    })
    .catch(err => {
      console.error('Error obteniendo el nombre del conductor:', err)
      driverNames.value[key] = 'Nombre no disponible'
    })

  return driverNames.value[key]
}

function getCentralNameById(centralId) {
  if (centralId === null || centralId === undefined) return 'Nombre no disponible'

  const key = String(centralId)
  // Devuelve cache si existe
  if (centralNames.value[key]) return centralNames.value[key]

  // Marcar como cargando y disparar la petición en background
  centralNames.value[key] = 'Cargando...'
  CentralServices.getCentralById(centralId)
    .then(response => {
      const c = response && response.data ? response.data : response
      centralNames.value[key] = (c && c.name) ? c.name : 'Nombre no disponible'
    })
    .catch(err => {
      console.error('Error obteniendo el nombre de la central:', err)
      centralNames.value[key] = 'Nombre no disponible'
    })

  return centralNames.value[key]
}

// Devuelve coordenadas formateadas desde la central (prefiere WKT `location`)
function getCentralCoordsById(centralId) {
  if (centralId === null || centralId === undefined) return 'N/A'
  const key = String(centralId)
  if (centralCoords.value[key]) return centralCoords.value[key]
  centralCoords.value[key] = 'Cargando...'
  CentralServices.getCentralById(centralId)
    .then(res => {
      const c = res && res.data ? res.data : res
      const w = c && (c.location || c.geom || c.wkt)
      if (w && typeof w === 'string' && w.startsWith('POINT(')) {
        const inner = w.slice(6, -1).trim()
        const parts = inner.split(/\s+/)
        if (parts.length >= 2) {
          const x = Number(parts[0])
          const y = Number(parts[1])
          if (!Number.isNaN(x) && !Number.isNaN(y)) {
            centralCoords.value[key] = `${y.toFixed(6)}, ${x.toFixed(6)}`
            return
          }
        }
      }
      // fallback to coord fields
      const cx = c && (c.coord_x || c.longitude)
      const cy = c && (c.coord_y || c.latitude)
      if (cx != null && cy != null) centralCoords.value[key] = `${Number(cy).toFixed(6)}, ${Number(cx).toFixed(6)}`
      else centralCoords.value[key] = ''
    })
    .catch(err => {
      console.error('Error fetching central coords', err)
      centralCoords.value[key] = ''
    })
  return centralCoords.value[key]
}


// Obtener todas las rutas asignadas al conductor logueado
const getallrouteassigned = (driver) => {
  if (!driver || !driver.id) return; // Asegurarnos de que el `driver` tenga un id

  routeServices.getAllRouterByDriverIdFinish(driver.id)
    .then((data) => {

      // data puede venir como respuesta axios (data.data) o como array directo
      const arr = Array.isArray(data && data.data) ? data.data : (Array.isArray(data) ? data : (data && Array.isArray(data) ? data : []));

      // Normalizar y asegurar campos por cada ruta
      arr.forEach(route => {
        route.date_ = route.date_ || 'Fecha no disponible';
        route.start_time = route.start_time || 'Hora no disponible';
        route.end_time = route.end_time || 'Hora no disponible';
        route.id_central = route.id_central || null;
        route.id_central_finish = route.id_central_finish || null;
      });

      // Ordenar descendente por id (nuevas primero)
      arr.sort((a, b) => {
        const idA = a && a.id != null ? Number(a.id) : -Infinity;
        const idB = b && b.id != null ? Number(b.id) : -Infinity;
        return idB - idA;
      });

      // Guardar en la forma que usa la plantilla (routes.data)
      routes.value = { data: arr };
    })
    .catch((error) => {
      console.error("Error al obtener las rutas:", error);
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
.route-taken-view {
  width: 100%;
  max-width: 1200px;
  margin: 2rem auto;
  background: #fff;
  border-radius: 1.2rem;
  box-shadow: 0 6px 18px rgba(78,83,54,0.10);
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
  box-shadow: 0 2px 8px rgba(78,83,54,0.08);
}

.route-table th, .route-table td {
  padding: 1rem 1.2rem;
  text-align: left;
  font-size: 1rem;
  border-bottom: 1px solid #eaeaea;
  vertical-align: middle;
}

.route-table th {
  background: linear-gradient(180deg,#5e6541,#52563f);
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

.container {
  overflow-x: auto;
}
</style>
